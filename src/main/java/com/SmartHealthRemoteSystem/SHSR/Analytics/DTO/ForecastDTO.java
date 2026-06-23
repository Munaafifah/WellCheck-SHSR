package com.SmartHealthRemoteSystem.SHSR.Analytics.DTO;

import java.util.List;

public class ForecastDTO {

    private long   forecastedNextMonthVolume;
    private long   forecastedNextMonthEmergency;
    private long   forecastedNextMonthReports;

    // Chart data: last 3 months actual + next month forecast
    private List<String> chartLabels;
    private List<Long>   historicalVolumes;
    private long         forecastedVolume;

    private double confidenceInterval;
    private String forecastMethod;
    private String nextMonthLabel;

    public ForecastDTO() {}

    // --- Getters ---
    public long         getForecastedNextMonthVolume()    { return forecastedNextMonthVolume; }
    public long         getForecastedNextMonthEmergency() { return forecastedNextMonthEmergency; }
    public long         getForecastedNextMonthReports()   { return forecastedNextMonthReports; }
    public List<String> getChartLabels()                  { return chartLabels; }
    public List<Long>   getHistoricalVolumes()            { return historicalVolumes; }
    public long         getForecastedVolume()             { return forecastedVolume; }
    public double       getConfidenceInterval()           { return confidenceInterval; }
    public String       getForecastMethod()               { return forecastMethod; }
    public String       getNextMonthLabel()               { return nextMonthLabel; }

    // --- Setters ---
    public void setForecastedNextMonthVolume(long v)    { this.forecastedNextMonthVolume = v; }
    public void setForecastedNextMonthEmergency(long v) { this.forecastedNextMonthEmergency = v; }
    public void setForecastedNextMonthReports(long v)   { this.forecastedNextMonthReports = v; }
    public void setChartLabels(List<String> v)          { this.chartLabels = v; }
    public void setHistoricalVolumes(List<Long> v)      { this.historicalVolumes = v; }
    public void setForecastedVolume(long v)             { this.forecastedVolume = v; }
    public void setConfidenceInterval(double v)         { this.confidenceInterval = v; }
    public void setForecastMethod(String v)             { this.forecastMethod = v; }
    public void setNextMonthLabel(String v)             { this.nextMonthLabel = v; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ForecastDTO dto = new ForecastDTO();
        public Builder forecastedNextMonthVolume(long v)    { dto.forecastedNextMonthVolume = v;    return this; }
        public Builder forecastedNextMonthEmergency(long v) { dto.forecastedNextMonthEmergency = v; return this; }
        public Builder forecastedNextMonthReports(long v)   { dto.forecastedNextMonthReports = v;   return this; }
        public Builder chartLabels(List<String> v)          { dto.chartLabels = v;                  return this; }
        public Builder historicalVolumes(List<Long> v)      { dto.historicalVolumes = v;            return this; }
        public Builder forecastedVolume(long v)             { dto.forecastedVolume = v;             return this; }
        public Builder confidenceInterval(double v)         { dto.confidenceInterval = v;           return this; }
        public Builder forecastMethod(String v)             { dto.forecastMethod = v;               return this; }
        public Builder nextMonthLabel(String v)             { dto.nextMonthLabel = v;               return this; }
        public ForecastDTO build()                          { return dto; }
    }
}
