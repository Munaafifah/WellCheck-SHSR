package com.SmartHealthRemoteSystem.SHSR.Analytics.DTO;

public class DashboardStatsDTO {

    private long   totalImagingRequests;
    private double avgTurnaroundDays;
    private long   totalEmergencyCases;
    private double avgEmergencyResponseMinutes;
    private long   activeRadiologists;
    private long   pendingReports;

    private double requestGrowthPercent;
    private double turnaroundImprovementPercent;
    private long   emergencyThisMonth;
    private double responseTimeFasterMinutes;

    public DashboardStatsDTO() {}

    // --- Getters ---
    public long   getTotalImagingRequests()          { return totalImagingRequests; }
    public double getAvgTurnaroundDays()             { return avgTurnaroundDays; }
    public long   getTotalEmergencyCases()           { return totalEmergencyCases; }
    public double getAvgEmergencyResponseMinutes()   { return avgEmergencyResponseMinutes; }
    public long   getActiveRadiologists()            { return activeRadiologists; }
    public long   getPendingReports()                { return pendingReports; }
    public double getRequestGrowthPercent()          { return requestGrowthPercent; }
    public double getTurnaroundImprovementPercent()  { return turnaroundImprovementPercent; }
    public long   getEmergencyThisMonth()            { return emergencyThisMonth; }
    public double getResponseTimeFasterMinutes()     { return responseTimeFasterMinutes; }

    // --- Setters ---
    public void setTotalImagingRequests(long v)         { this.totalImagingRequests = v; }
    public void setAvgTurnaroundDays(double v)          { this.avgTurnaroundDays = v; }
    public void setTotalEmergencyCases(long v)          { this.totalEmergencyCases = v; }
    public void setAvgEmergencyResponseMinutes(double v){ this.avgEmergencyResponseMinutes = v; }
    public void setActiveRadiologists(long v)           { this.activeRadiologists = v; }
    public void setPendingReports(long v)               { this.pendingReports = v; }
    public void setRequestGrowthPercent(double v)       { this.requestGrowthPercent = v; }
    public void setTurnaroundImprovementPercent(double v){ this.turnaroundImprovementPercent = v; }
    public void setEmergencyThisMonth(long v)           { this.emergencyThisMonth = v; }
    public void setResponseTimeFasterMinutes(double v)  { this.responseTimeFasterMinutes = v; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DashboardStatsDTO dto = new DashboardStatsDTO();
        public Builder totalImagingRequests(long v)        { dto.totalImagingRequests = v;        return this; }
        public Builder avgTurnaroundDays(double v)         { dto.avgTurnaroundDays = v;           return this; }
        public Builder totalEmergencyCases(long v)         { dto.totalEmergencyCases = v;         return this; }
        public Builder avgEmergencyResponseMinutes(double v){ dto.avgEmergencyResponseMinutes = v; return this; }
        public Builder activeRadiologists(long v)          { dto.activeRadiologists = v;          return this; }
        public Builder pendingReports(long v)              { dto.pendingReports = v;              return this; }
        public Builder requestGrowthPercent(double v)      { dto.requestGrowthPercent = v;        return this; }
        public Builder turnaroundImprovementPercent(double v){ dto.turnaroundImprovementPercent = v; return this; }
        public Builder emergencyThisMonth(long v)          { dto.emergencyThisMonth = v;          return this; }
        public Builder responseTimeFasterMinutes(double v) { dto.responseTimeFasterMinutes = v;   return this; }
        public DashboardStatsDTO build()                   { return dto; }
    }
}
