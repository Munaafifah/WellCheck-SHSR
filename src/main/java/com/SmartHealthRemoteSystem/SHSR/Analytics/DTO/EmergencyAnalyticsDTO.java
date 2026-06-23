package com.SmartHealthRemoteSystem.SHSR.Analytics.DTO;

import java.util.List;

public class EmergencyAnalyticsDTO {

    private long   totalEmergencyRequests;
    private long   completedEmergencyCases;
    private long   pendingEmergencyCases;
    private double avgResponseTimeHours;

    // Monthly trend (last 6 months)
    private List<String> monthLabels;
    private List<Long>   monthlyEmergencyCounts;

    // Weekly trend (current month broken into weeks)
    private List<String> weekLabels;
    private List<Long>   weeklyEmergencyCounts;
    private List<Double> weeklyResponseTimes;

    public EmergencyAnalyticsDTO() {}

    // --- Getters ---
    public long         getTotalEmergencyRequests()   { return totalEmergencyRequests; }
    public long         getCompletedEmergencyCases()  { return completedEmergencyCases; }
    public long         getPendingEmergencyCases()    { return pendingEmergencyCases; }
    public double       getAvgResponseTimeHours()     { return avgResponseTimeHours; }
    public List<String> getMonthLabels()              { return monthLabels; }
    public List<Long>   getMonthlyEmergencyCounts()   { return monthlyEmergencyCounts; }
    public List<String> getWeekLabels()               { return weekLabels; }
    public List<Long>   getWeeklyEmergencyCounts()    { return weeklyEmergencyCounts; }
    public List<Double> getWeeklyResponseTimes()      { return weeklyResponseTimes; }

    // --- Setters ---
    public void setTotalEmergencyRequests(long v)          { this.totalEmergencyRequests = v; }
    public void setCompletedEmergencyCases(long v)         { this.completedEmergencyCases = v; }
    public void setPendingEmergencyCases(long v)           { this.pendingEmergencyCases = v; }
    public void setAvgResponseTimeHours(double v)          { this.avgResponseTimeHours = v; }
    public void setMonthLabels(List<String> v)             { this.monthLabels = v; }
    public void setMonthlyEmergencyCounts(List<Long> v)    { this.monthlyEmergencyCounts = v; }
    public void setWeekLabels(List<String> v)              { this.weekLabels = v; }
    public void setWeeklyEmergencyCounts(List<Long> v)     { this.weeklyEmergencyCounts = v; }
    public void setWeeklyResponseTimes(List<Double> v)     { this.weeklyResponseTimes = v; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final EmergencyAnalyticsDTO dto = new EmergencyAnalyticsDTO();
        public Builder totalEmergencyRequests(long v)         { dto.totalEmergencyRequests = v;  return this; }
        public Builder completedEmergencyCases(long v)        { dto.completedEmergencyCases = v; return this; }
        public Builder pendingEmergencyCases(long v)          { dto.pendingEmergencyCases = v;   return this; }
        public Builder avgResponseTimeHours(double v)         { dto.avgResponseTimeHours = v;    return this; }
        public Builder monthLabels(List<String> v)            { dto.monthLabels = v;             return this; }
        public Builder monthlyEmergencyCounts(List<Long> v)   { dto.monthlyEmergencyCounts = v;  return this; }
        public Builder weekLabels(List<String> v)             { dto.weekLabels = v;              return this; }
        public Builder weeklyEmergencyCounts(List<Long> v)    { dto.weeklyEmergencyCounts = v;   return this; }
        public Builder weeklyResponseTimes(List<Double> v)    { dto.weeklyResponseTimes = v;     return this; }
        public EmergencyAnalyticsDTO build()                  { return dto; }
    }
}
