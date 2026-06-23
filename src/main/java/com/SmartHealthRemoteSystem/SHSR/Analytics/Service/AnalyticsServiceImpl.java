package com.SmartHealthRemoteSystem.SHSR.Analytics.Service;

import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.*;
import com.SmartHealthRemoteSystem.SHSR.Analytics.Repository.AnalyticsAggregationRepository;
import com.SmartHealthRemoteSystem.SHSR.Radiology.repository.MedicalImageRepository;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.model.Report;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.repository.ReportRepository;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.*;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.ImagingRequestRepository;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.RadiologyAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final String[] MONTH_NAMES = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    private final ImagingRequestRepository     imagingRequestRepo;
    private final RadiologyAppointmentRepository appointmentRepo;
    private final ReportRepository             reportRepo;
    private final MedicalImageRepository       imageRepo;
    private final AnalyticsAggregationRepository aggRepo;

    @Autowired
    public AnalyticsServiceImpl(
            ImagingRequestRepository imagingRequestRepo,
            RadiologyAppointmentRepository appointmentRepo,
            ReportRepository reportRepo,
            MedicalImageRepository imageRepo,
            AnalyticsAggregationRepository aggRepo) {
        this.imagingRequestRepo = imagingRequestRepo;
        this.appointmentRepo    = appointmentRepo;
        this.reportRepo         = reportRepo;
        this.imageRepo          = imageRepo;
        this.aggRepo            = aggRepo;
    }

    // ── UCR026 / Dashboard KPIs ───────────────────────────────────────────────

    @Override
    public DashboardStatsDTO getDashboardStats() {
        long totalRequests    = imagingRequestRepo.count();
        long totalEmergency   = imagingRequestRepo.findByUrgencyLevel(UrgencyLevel.EMERGENCY).size();
        long activeRads       = aggRepo.countUsersByRole("RADIOLOGIST");
        long pendingReports   = aggRepo.countPendingReports();
        long thisMonth        = aggRepo.countRequestsThisMonth();
        long lastMonth        = aggRepo.countRequestsLastMonth();
        long emergencyMonth   = aggRepo.countEmergencyThisMonth();

        double growth = lastMonth == 0 ? 0 :
                Math.round(((double)(thisMonth - lastMonth) / lastMonth) * 1000.0) / 10.0;

        TurnaroundDTO tat = getTurnaroundMetrics();

        return DashboardStatsDTO.builder()
                .totalImagingRequests(totalRequests)
                .avgTurnaroundDays(round1(tat.getAvgAppointmentTurnaroundDays()))
                .totalEmergencyCases(totalEmergency)
                .avgEmergencyResponseMinutes(round1(tat.getAvgReportTurnaroundDays() * 24 * 60))
                .activeRadiologists(activeRads)
                .pendingReports(pendingReports)
                .requestGrowthPercent(growth)
                .emergencyThisMonth(emergencyMonth)
                .build();
    }

    // ── UCR026 – Imaging Volume ───────────────────────────────────────────────

    @Override
    public ImagingVolumeDTO getImagingVolume(String period) {
        int months = periodToMonths(period);
        List<String> monthKeys  = aggRepo.getMonthKeys(months);
        List<String> labels     = monthKeys.stream()
                .map(this::monthKeyToLabel).collect(Collectors.toList());

        Map<String, Map<String, Long>> byModality = aggRepo.getModalityCountsByMonth(months);

        List<Long> xrayData       = extractMonthlyValues(byModality, "XRAY", monthKeys);
        List<Long> ctData         = extractMonthlyValues(byModality, "CT", monthKeys);
        List<Long> mriData        = extractMonthlyValues(byModality, "MRI", monthKeys);
        List<Long> ultrasoundData = extractMonthlyValues(byModality, "ULTRASOUND", monthKeys);

        long totalXray       = xrayData.stream().mapToLong(Long::longValue).sum();
        long totalCT         = ctData.stream().mapToLong(Long::longValue).sum();
        long totalMRI        = mriData.stream().mapToLong(Long::longValue).sum();
        long totalUltrasound = ultrasoundData.stream().mapToLong(Long::longValue).sum();
        long grandTotal      = totalXray + totalCT + totalMRI + totalUltrasound;

        // Growth vs previous period
        long prevPeriodTotal = getPrevPeriodTotal(months);
        double growth = prevPeriodTotal == 0 ? 0 :
                Math.round(((double)(grandTotal - prevPeriodTotal) / prevPeriodTotal) * 1000.0) / 10.0;

        return ImagingVolumeDTO.builder()
                .period(period)
                .labels(labels)
                .xrayData(xrayData)
                .ctData(ctData)
                .mriData(mriData)
                .ultrasoundData(ultrasoundData)
                .totalXray(totalXray)
                .totalCT(totalCT)
                .totalMRI(totalMRI)
                .totalUltrasound(totalUltrasound)
                .grandTotal(grandTotal)
                .growthPercent(growth)
                .build();
    }

    // ── UCR027 – Turnaround ───────────────────────────────────────────────────

    @Override
    public TurnaroundDTO getTurnaroundMetrics() {
        // Appointment turnaround: ImagingRequest.createdDate → Appointment.appointmentDate
        List<RadiologyAppointment> appts = appointmentRepo.findAll();
        List<Double> apptTurnarounds = new ArrayList<>();
        Map<String, List<Double>> apptByMonth = new TreeMap<>();

        for (RadiologyAppointment appt : appts) {
            if (appt.getRequestId() == null || appt.getAppointmentDate() == null) continue;
            Optional<ImagingRequest> reqOpt = imagingRequestRepo.findById(appt.getRequestId());
            if (!reqOpt.isPresent() || reqOpt.get().getCreatedDate() == null) continue;

            double days = msToDays(appt.getAppointmentDate().getTime()
                    - reqOpt.get().getCreatedDate().getTime());
            if (days >= 0) {
                apptTurnarounds.add(days);
                String mk = aggRepo.toMonthKey(appt.getCreatedDate() != null
                        ? appt.getCreatedDate() : appt.getAppointmentDate());
                apptByMonth.computeIfAbsent(mk, k -> new ArrayList<>()).add(days);
            }
        }

        // Report turnaround: ImagingRequest.createdDate → Report.createdDate
        List<Report> reports = reportRepo.findAll();
        List<Double> reportTurnarounds = new ArrayList<>();
        Map<String, List<Double>> reportByMonth = new TreeMap<>();

        for (Report report : reports) {
            if (report.getRequestId() == null || report.getCreatedDate() == null) continue;
            Optional<ImagingRequest> reqOpt = imagingRequestRepo.findById(report.getRequestId());
            if (!reqOpt.isPresent() || reqOpt.get().getCreatedDate() == null) continue;

            double days = msToDays(report.getCreatedDate().getTime()
                    - reqOpt.get().getCreatedDate().getTime());
            if (days >= 0) {
                reportTurnarounds.add(days);
                String mk = aggRepo.toMonthKey(report.getCreatedDate());
                reportByMonth.computeIfAbsent(mk, k -> new ArrayList<>()).add(days);
            }
        }

        List<String> monthKeys   = aggRepo.getMonthKeys(6);
        List<String> monthLabels = monthKeys.stream().map(this::monthKeyToLabel).collect(Collectors.toList());

        List<Double> apptByMonthAvg   = monthKeys.stream()
                .map(k -> avg(apptByMonth.getOrDefault(k, Collections.emptyList())))
                .collect(Collectors.toList());
        List<Double> reportByMonthAvg = monthKeys.stream()
                .map(k -> avg(reportByMonth.getOrDefault(k, Collections.emptyList())))
                .collect(Collectors.toList());

        return TurnaroundDTO.builder()
                .avgAppointmentTurnaroundDays(round1(avg(apptTurnarounds)))
                .medianAppointmentTurnaroundDays(round1(median(apptTurnarounds)))
                .fastestAppointmentTurnaroundDays(round1(min(apptTurnarounds)))
                .slowestAppointmentTurnaroundDays(round1(max(apptTurnarounds)))
                .avgReportTurnaroundDays(round1(avg(reportTurnarounds)))
                .medianReportTurnaroundDays(round1(median(reportTurnarounds)))
                .fastestReportTurnaroundDays(round1(min(reportTurnarounds)))
                .slowestReportTurnaroundDays(round1(max(reportTurnarounds)))
                .monthLabels(monthLabels)
                .appointmentTurnaroundByMonth(apptByMonthAvg)
                .reportTurnaroundByMonth(reportByMonthAvg)
                .build();
    }

    // ── UCR028 – Emergency Analytics ──────────────────────────────────────────

    @Override
    public EmergencyAnalyticsDTO getEmergencyAnalytics() {
        List<ImagingRequest> emergencies = imagingRequestRepo.findByUrgencyLevel(UrgencyLevel.EMERGENCY);
        long total     = emergencies.size();
        long completed = emergencies.stream()
                .filter(r -> r.getStatus() == RequestStatus.COMPLETED).count();
        long pending   = total - completed;

        // Monthly counts (last 6 months)
        Map<String, Long> monthly = aggRepo.getMonthlyEmergencyCounts(6);
        List<String> monthKeys    = aggRepo.getMonthKeys(6);
        List<String> monthLabels  = monthKeys.stream().map(this::monthKeyToLabel).collect(Collectors.toList());
        List<Long>   monthCounts  = monthKeys.stream()
                .map(k -> monthly.getOrDefault(k, 0L)).collect(Collectors.toList());

        // Weekly counts this month
        Map<String, Long> weekly = aggRepo.getWeeklyEmergencyCountsThisMonth();
        List<String> weekLabels  = Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4");
        List<Long>   weekCounts  = weekLabels.stream()
                .map(w -> weekly.getOrDefault(w, 0L)).collect(Collectors.toList());

        // Placeholder weekly response times (hours) — derived from appointment-request gap for EMERGENCY
        List<Double> weeklyResponseTimes = computeWeeklyResponseTimes(emergencies);

        double avgResponseHours = computeAvgResponseHours(emergencies);

        return EmergencyAnalyticsDTO.builder()
                .totalEmergencyRequests(total)
                .completedEmergencyCases(completed)
                .pendingEmergencyCases(pending)
                .avgResponseTimeHours(round1(avgResponseHours))
                .monthLabels(monthLabels)
                .monthlyEmergencyCounts(monthCounts)
                .weekLabels(weekLabels)
                .weeklyEmergencyCounts(weekCounts)
                .weeklyResponseTimes(weeklyResponseTimes)
                .build();
    }

    // ── UCR029 – Trends ───────────────────────────────────────────────────────

    @Override
    public List<TrendDTO> getTrends(String period) {
        int months = periodToMonths(period);
        List<String> monthKeys  = aggRepo.getMonthKeys(months);
        List<String> labels     = monthKeys.stream().map(this::monthKeyToLabel).collect(Collectors.toList());

        Map<String, Long> reqCounts  = aggRepo.getMonthlyRequestCounts(months);
        Map<String, Long> apptCounts = aggRepo.getMonthlyAppointmentCounts(months);
        Map<String, Long> rptCounts  = aggRepo.getMonthlyReportCounts(months);
        Map<String, Long> emgCounts  = aggRepo.getMonthlyEmergencyCounts(months);

        List<TrendDTO> trends = new ArrayList<>();
        trends.add(buildTrend(TrendDTO.TrendType.IMAGING_REQUESTS, "Imaging Requests",
                labels, monthKeys, reqCounts));
        trends.add(buildTrend(TrendDTO.TrendType.APPOINTMENTS, "Appointments",
                labels, monthKeys, apptCounts));
        trends.add(buildTrend(TrendDTO.TrendType.REPORTS, "Reports Completed",
                labels, monthKeys, rptCounts));
        trends.add(buildTrend(TrendDTO.TrendType.EMERGENCY, "Emergency Cases",
                labels, monthKeys, emgCounts));
        return trends;
    }

    // ── Radiologist Performance ───────────────────────────────────────────────

    @Override
    public Map<String, Object> getRadiologistPerformance() {
        Map<String, Long> reportCounts = aggRepo.getRadiologistReportCounts();
        long totalReports = reportCounts.values().stream().mapToLong(Long::longValue).sum();
        long numRads = Math.max(1, reportCounts.size());

        List<Map<String, Object>> rows = new ArrayList<>();
        reportCounts.forEach((radId, count) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("radiologistId", radId);
            row.put("reportsCompleted", count);
            double rate = totalReports == 0 ? 0 : round1((double) count / totalReports * 100);
            row.put("completionRate", rate);
            double score = Math.min(100, round1((double) count / Math.max(1, totalReports / numRads) * 100));
            row.put("productivityScore", score);
            rows.add(row);
        });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("totalReports", totalReports);
        result.put("avgReportsPerRadiologist", numRads == 0 ? 0 : round1((double) totalReports / numRads));
        return result;
    }

    // ── Radiographer Performance ──────────────────────────────────────────────

    @Override
    public Map<String, Object> getRadiographerPerformance() {
        Map<String, Long> imageCounts  = aggRepo.getRadiographerImageCounts();
        Map<String, Long> apptCounts   = aggRepo.getRadiographerAppointmentCounts();

        Set<String> allIds = new LinkedHashSet<>();
        allIds.addAll(imageCounts.keySet());
        allIds.addAll(apptCounts.keySet());

        long totalImages = imageCounts.values().stream().mapToLong(Long::longValue).sum();
        long numRads = Math.max(1, allIds.size());

        List<Map<String, Object>> rows = new ArrayList<>();
        for (String id : allIds) {
            long images = imageCounts.getOrDefault(id, 0L);
            long appts  = apptCounts.getOrDefault(id, 0L);
            double score = Math.min(100, round1(
                    (images * 0.6 + appts * 0.4) / Math.max(1,
                            (totalImages * 0.6 / numRads + appts * 0.4)) * 100));

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("radiographerId", id);
            row.put("imagesUploaded", images);
            row.put("appointmentsScheduled", appts);
            row.put("productivityScore", score);
            rows.add(row);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", rows);
        result.put("totalImages", totalImages);
        return result;
    }

    // ── Patient Demographics ──────────────────────────────────────────────────

    @Override
    public Map<String, Object> getPatientDemographics() {
        // Build from distinct patientIds in ImagingRequests
        List<ImagingRequest> reqs = imagingRequestRepo.findAll();
        long totalPatients = reqs.stream()
                .map(ImagingRequest::getPatientId)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        // Demographics from User collection (gender field may or may not exist)
        long male   = aggRepo.countUsersByRole("__male__placeholder__");   // placeholder
        long female = totalPatients;                                        // show all as base

        // Age groups — placeholder data until dateOfBirth is available
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalPatients", totalPatients);
        result.put("genderLabels", Arrays.asList("Male", "Female", "Other"));
        result.put("genderCounts", Arrays.asList(0L, 0L, totalPatients));
        result.put("ageGroupLabels", Arrays.asList("Under 18", "18-30", "31-45", "46-60", "Above 60"));
        result.put("ageGroupCounts", Arrays.asList(0L, 0L, 0L, 0L, 0L));
        return result;
    }

    // ── Modality Utilization ──────────────────────────────────────────────────

    @Override
    public Map<String, Object> getModalityUtilization(String period) {
        int months = periodToMonths(period);
        Map<String, Long> counts = aggRepo.getModalityCountsForLastMonths(months);

        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("labels", Arrays.asList("X-Ray", "CT", "MRI", "Ultrasound"));
        result.put("counts", Arrays.asList(
                counts.getOrDefault("XRAY", 0L),
                counts.getOrDefault("CT", 0L),
                counts.getOrDefault("MRI", 0L),
                counts.getOrDefault("ULTRASOUND", 0L)));
        result.put("total", total);
        return result;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private int periodToMonths(String period) {
        if (period == null) return 6;
        switch (period.toLowerCase()) {
            case "daily":   return 1;
            case "weekly":  return 1;
            case "yearly":  return 12;
            default:        return 6;   // monthly → show 6 months
        }
    }

    private String monthKeyToLabel(String key) {
        if (key == null || !key.contains("-")) return key;
        String[] parts = key.split("-");
        int month = Integer.parseInt(parts[1]) - 1;
        return MONTH_NAMES[month];
    }

    private List<Long> extractMonthlyValues(Map<String, Map<String, Long>> byModality,
                                             String modalityKey, List<String> monthKeys) {
        Map<String, Long> monthMap = byModality.getOrDefault(modalityKey, Collections.emptyMap());
        return monthKeys.stream()
                .map(k -> monthMap.getOrDefault(k, 0L))
                .collect(Collectors.toList());
    }

    private long getPrevPeriodTotal(int months) {
        Calendar start = Calendar.getInstance();
        start.add(Calendar.MONTH, -(months * 2 - 1));
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        Date prevStart = start.getTime();

        Calendar end = Calendar.getInstance();
        end.add(Calendar.MONTH, -months);
        end.add(Calendar.MONTH, 1);
        end.set(Calendar.DAY_OF_MONTH, 1);
        Date prevEnd = end.getTime();

        return imagingRequestRepo.findAll().stream()
                .filter(r -> r.getCreatedDate() != null
                        && !r.getCreatedDate().before(prevStart)
                        && r.getCreatedDate().before(prevEnd))
                .count();
    }

    private TrendDTO buildTrend(TrendDTO.TrendType type, String label,
                                List<String> labels, List<String> monthKeys,
                                Map<String, Long> countMap) {
        List<Long> values = monthKeys.stream()
                .map(k -> countMap.getOrDefault(k, 0L)).collect(Collectors.toList());

        long current  = values.isEmpty() ? 0 : values.get(values.size() - 1);
        long previous = values.size() < 2 ? 0 : values.get(values.size() - 2);
        double change = previous == 0 ? 0 :
                Math.round(((double)(current - previous) / previous) * 1000.0) / 10.0;
        String direction = change > 0 ? "UP" : change < 0 ? "DOWN" : "STABLE";

        return TrendDTO.builder()
                .trendType(type).label(label).labels(labels).values(values)
                .changePercent(change).changeDirection(direction).build();
    }

    private double computeAvgResponseHours(List<ImagingRequest> emergencies) {
        List<Double> times = new ArrayList<>();
        for (ImagingRequest req : emergencies) {
            if (req.getRequestId() == null || req.getCreatedDate() == null) continue;
            Optional<RadiologyAppointment> apptOpt = appointmentRepo.findByRequestId(req.getRequestId());
            if (!apptOpt.isPresent() || apptOpt.get().getCreatedDate() == null) continue;
            double hours = (apptOpt.get().getCreatedDate().getTime() - req.getCreatedDate().getTime())
                    / (1000.0 * 60 * 60);
            if (hours >= 0) times.add(hours);
        }
        return avg(times);
    }

    private List<Double> computeWeeklyResponseTimes(List<ImagingRequest> emergencies) {
        Map<String, List<Double>> weekTimes = new LinkedHashMap<>();
        weekTimes.put("Week 1", new ArrayList<>());
        weekTimes.put("Week 2", new ArrayList<>());
        weekTimes.put("Week 3", new ArrayList<>());
        weekTimes.put("Week 4", new ArrayList<>());

        Calendar monthStart = Calendar.getInstance();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);

        for (ImagingRequest req : emergencies) {
            if (req.getCreatedDate() == null) continue;
            Calendar c = Calendar.getInstance();
            c.setTime(req.getCreatedDate());
            if (c.get(Calendar.YEAR) != monthStart.get(Calendar.YEAR)
                    || c.get(Calendar.MONTH) != monthStart.get(Calendar.MONTH)) continue;

            int dom = c.get(Calendar.DAY_OF_MONTH);
            String week = dom <= 7 ? "Week 1" : dom <= 14 ? "Week 2" : dom <= 21 ? "Week 3" : "Week 4";

            if (req.getRequestId() == null) continue;
            Optional<RadiologyAppointment> apptOpt = appointmentRepo.findByRequestId(req.getRequestId());
            if (!apptOpt.isPresent() || apptOpt.get().getCreatedDate() == null) continue;
            double hours = (apptOpt.get().getCreatedDate().getTime() - req.getCreatedDate().getTime())
                    / (1000.0 * 60 * 60);
            if (hours >= 0) weekTimes.get(week).add(hours);
        }

        return weekTimes.values().stream()
                .map(list -> round1(avg(list)))
                .collect(Collectors.toList());
    }

    private double msToDays(long ms) {
        return ms / (1000.0 * 60 * 60 * 24);
    }

    private double avg(List<Double> list) {
        if (list == null || list.isEmpty()) return 0.0;
        return list.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double median(List<Double> list) {
        if (list == null || list.isEmpty()) return 0.0;
        List<Double> sorted = new ArrayList<>(list);
        Collections.sort(sorted);
        int mid = sorted.size() / 2;
        return sorted.size() % 2 == 0
                ? (sorted.get(mid - 1) + sorted.get(mid)) / 2.0
                : sorted.get(mid);
    }

    private double min(List<Double> list) {
        if (list == null || list.isEmpty()) return 0.0;
        return list.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    }

    private double max(List<Double> list) {
        if (list == null || list.isEmpty()) return 0.0;
        return list.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    private double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
