package com.SmartHealthRemoteSystem.SHSR.Analytics.Repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * MongoDB aggregation queries for the Analytics module.
 * Uses MongoTemplate to avoid loading entire collections into memory.
 */
@Repository
public class AnalyticsAggregationRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AnalyticsAggregationRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    // ── User counts ──────────────────────────────────────────────────────────

    public long countUsersByRole(String role) {
        Query q = new Query(Criteria.where("role").is(role));
        return mongoTemplate.count(q, "User");
    }

    // ── ImagingRequest counts ─────────────────────────────────────────────────

    public long countPendingReports() {
        Query q = new Query(Criteria.where("status").is("PENDING"));
        return mongoTemplate.count(q, "RadiologyReport");
    }

    public long countRequestsThisMonth() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);

        Query q = new Query(Criteria.where("createdDate")
                .gte(start.getTime()).lt(end.getTime()));
        return mongoTemplate.count(q, "RadiologyRequest");
    }

    public long countRequestsLastMonth() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        start.add(Calendar.MONTH, -1);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);

        Query q = new Query(Criteria.where("createdDate")
                .gte(start.getTime()).lt(end.getTime()));
        return mongoTemplate.count(q, "RadiologyRequest");
    }

    public long countEmergencyThisMonth() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);

        Query q = new Query(Criteria.where("urgencyLevel").is("EMERGENCY")
                .and("createdDate").gte(start.getTime()).lt(end.getTime()));
        return mongoTemplate.count(q, "RadiologyRequest");
    }

    // ── Monthly request counts (last N months) ────────────────────────────────

    /**
     * Returns a map of "YYYY-MM" → count for the last {@code months} calendar months.
     */
    public Map<String, Long> getMonthlyRequestCounts(int months) {
        Map<String, Long> result = new LinkedHashMap<>();
        List<String> keys = getMonthKeys(months);
        for (String key : keys) {
            result.put(key, 0L);
        }

        Date rangeStart = getStartOfNMonthsAgo(months);
        Query q = new Query(Criteria.where("createdDate").gte(rangeStart));
        List<Document> docs = mongoTemplate.find(q, Document.class, "RadiologyRequest");

        for (Document doc : docs) {
            Date d = doc.getDate("createdDate");
            if (d == null) continue;
            String key = toMonthKey(d);
            result.merge(key, 1L, Long::sum);
        }
        return result;
    }

    /**
     * Counts ImagingRequests per modality over the last {@code months} months.
     * Returns a map of modalityName → count.
     */
    public Map<String, Long> getModalityCountsForLastMonths(int months) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("XRAY", 0L);
        result.put("CT", 0L);
        result.put("MRI", 0L);
        result.put("ULTRASOUND", 0L);

        Date rangeStart = getStartOfNMonthsAgo(months);
        Query q = new Query(Criteria.where("createdDate").gte(rangeStart));
        List<Document> docs = mongoTemplate.find(q, Document.class, "RadiologyRequest");

        for (Document doc : docs) {
            String modality = doc.getString("modality");
            if (modality != null) {
                result.merge(modality, 1L, Long::sum);
            }
        }
        return result;
    }

    /**
     * Returns per-modality counts broken down by month key.
     * Map structure: modalityName → (YYYY-MM → count)
     */
    public Map<String, Map<String, Long>> getModalityCountsByMonth(int months) {
        List<String> keys = getMonthKeys(months);

        Map<String, Map<String, Long>> result = new LinkedHashMap<>();
        for (String mod : Arrays.asList("XRAY", "CT", "MRI", "ULTRASOUND")) {
            Map<String, Long> monthMap = new LinkedHashMap<>();
            for (String k : keys) monthMap.put(k, 0L);
            result.put(mod, monthMap);
        }

        Date rangeStart = getStartOfNMonthsAgo(months);
        Query q = new Query(Criteria.where("createdDate").gte(rangeStart));
        List<Document> docs = mongoTemplate.find(q, Document.class, "RadiologyRequest");

        for (Document doc : docs) {
            Date d = doc.getDate("createdDate");
            String modality = doc.getString("modality");
            if (d == null || modality == null) continue;
            String monthKey = toMonthKey(d);
            result.computeIfAbsent(modality, k -> new LinkedHashMap<>()).merge(monthKey, 1L, Long::sum);
        }
        return result;
    }

    // ── Emergency analytics ───────────────────────────────────────────────────

    public Map<String, Long> getMonthlyEmergencyCounts(int months) {
        Map<String, Long> result = new LinkedHashMap<>();
        List<String> keys = getMonthKeys(months);
        for (String k : keys) result.put(k, 0L);

        Date rangeStart = getStartOfNMonthsAgo(months);
        Query q = new Query(Criteria.where("urgencyLevel").is("EMERGENCY")
                .and("createdDate").gte(rangeStart));
        List<Document> docs = mongoTemplate.find(q, Document.class, "RadiologyRequest");

        for (Document doc : docs) {
            Date d = doc.getDate("createdDate");
            if (d == null) continue;
            result.merge(toMonthKey(d), 1L, Long::sum);
        }
        return result;
    }

    /** Returns counts of emergency requests broken into 4 week buckets of the current month. */
    public Map<String, Long> getWeeklyEmergencyCountsThisMonth() {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("Week 1", 0L);
        result.put("Week 2", 0L);
        result.put("Week 3", 0L);
        result.put("Week 4", 0L);

        Calendar monthStart = Calendar.getInstance();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        monthStart.set(Calendar.HOUR_OF_DAY, 0);
        monthStart.set(Calendar.MINUTE, 0);
        monthStart.set(Calendar.SECOND, 0);
        monthStart.set(Calendar.MILLISECOND, 0);

        Calendar monthEnd = (Calendar) monthStart.clone();
        monthEnd.add(Calendar.MONTH, 1);

        Query q = new Query(Criteria.where("urgencyLevel").is("EMERGENCY")
                .and("createdDate").gte(monthStart.getTime()).lt(monthEnd.getTime()));
        List<Document> docs = mongoTemplate.find(q, Document.class, "RadiologyRequest");

        for (Document doc : docs) {
            Date d = doc.getDate("createdDate");
            if (d == null) continue;
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int dom = c.get(Calendar.DAY_OF_MONTH);
            if (dom <= 7)       result.merge("Week 1", 1L, Long::sum);
            else if (dom <= 14) result.merge("Week 2", 1L, Long::sum);
            else if (dom <= 21) result.merge("Week 3", 1L, Long::sum);
            else                result.merge("Week 4", 1L, Long::sum);
        }
        return result;
    }

    // ── Productivity analytics ────────────────────────────────────────────────

    /** Returns radiologistId → report count map. */
    public Map<String, Long> getRadiologistReportCounts() {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Document> docs = mongoTemplate.findAll(Document.class, "RadiologyReport");
        for (Document doc : docs) {
            String rid = doc.getString("radiologistId");
            if (rid != null) result.merge(rid, 1L, Long::sum);
        }
        return result;
    }

    /** Returns uploadedBy (radiographerId) → image count map. */
    public Map<String, Long> getRadiographerImageCounts() {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Document> docs = mongoTemplate.findAll(Document.class, "MedicalImage");
        for (Document doc : docs) {
            String uid = doc.getString("uploadedBy");
            if (uid != null) result.merge(uid, 1L, Long::sum);
        }
        return result;
    }

    /** Returns radiographerId → appointment count map. */
    public Map<String, Long> getRadiographerAppointmentCounts() {
        Map<String, Long> result = new LinkedHashMap<>();
        List<Document> docs = mongoTemplate.findAll(Document.class, "RadiologyAppointment");
        for (Document doc : docs) {
            String rid = doc.getString("radiographerId");
            if (rid != null) result.merge(rid, 1L, Long::sum);
        }
        return result;
    }

    // ── Monthly appointment/report trend data ─────────────────────────────────

    public Map<String, Long> getMonthlyAppointmentCounts(int months) {
        Map<String, Long> result = new LinkedHashMap<>();
        List<String> keys = getMonthKeys(months);
        for (String k : keys) result.put(k, 0L);

        Date rangeStart = getStartOfNMonthsAgo(months);
        Query q = new Query(Criteria.where("createdDate").gte(rangeStart));
        List<Document> docs = mongoTemplate.find(q, Document.class, "RadiologyAppointment");

        for (Document doc : docs) {
            Date d = doc.getDate("createdDate");
            if (d == null) continue;
            result.merge(toMonthKey(d), 1L, Long::sum);
        }
        return result;
    }

    public Map<String, Long> getMonthlyReportCounts(int months) {
        Map<String, Long> result = new LinkedHashMap<>();
        List<String> keys = getMonthKeys(months);
        for (String k : keys) result.put(k, 0L);

        Date rangeStart = getStartOfNMonthsAgo(months);
        Query q = new Query(Criteria.where("createdDate").gte(rangeStart));
        List<Document> docs = mongoTemplate.find(q, Document.class, "RadiologyReport");

        for (Document doc : docs) {
            Date d = doc.getDate("createdDate");
            if (d == null) continue;
            result.merge(toMonthKey(d), 1L, Long::sum);
        }
        return result;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public String toMonthKey(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c.get(Calendar.YEAR) + "-" + String.format("%02d", c.get(Calendar.MONTH) + 1);
    }

    public List<String> getMonthKeys(int n) {
        List<String> keys = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -i);
            keys.add(c.get(Calendar.YEAR) + "-" + String.format("%02d", c.get(Calendar.MONTH) + 1));
        }
        return keys;
    }

    private Date getStartOfNMonthsAgo(int n) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -(n - 1));
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
