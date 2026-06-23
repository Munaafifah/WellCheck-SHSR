package com.SmartHealthRemoteSystem.SHSR.Analytics.DTO;

import java.util.List;

public class TrendDTO {

    public enum TrendType {
        IMAGING_REQUESTS, APPOINTMENTS, REPORTS, MODALITY, DEMOGRAPHICS, EMERGENCY, WORKLOAD
    }

    private TrendType    trendType;
    private String       label;
    private List<String> labels;
    private List<Long>   values;
    private double       changePercent;
    private String       changeDirection; // UP, DOWN, STABLE

    public TrendDTO() {}

    // --- Getters ---
    public TrendType    getTrendType()       { return trendType; }
    public String       getLabel()           { return label; }
    public List<String> getLabels()          { return labels; }
    public List<Long>   getValues()          { return values; }
    public double       getChangePercent()   { return changePercent; }
    public String       getChangeDirection() { return changeDirection; }

    // --- Setters ---
    public void setTrendType(TrendType v)       { this.trendType = v; }
    public void setLabel(String v)              { this.label = v; }
    public void setLabels(List<String> v)       { this.labels = v; }
    public void setValues(List<Long> v)         { this.values = v; }
    public void setChangePercent(double v)      { this.changePercent = v; }
    public void setChangeDirection(String v)    { this.changeDirection = v; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final TrendDTO dto = new TrendDTO();
        public Builder trendType(TrendType v)       { dto.trendType = v;       return this; }
        public Builder label(String v)              { dto.label = v;           return this; }
        public Builder labels(List<String> v)       { dto.labels = v;          return this; }
        public Builder values(List<Long> v)         { dto.values = v;          return this; }
        public Builder changePercent(double v)      { dto.changePercent = v;   return this; }
        public Builder changeDirection(String v)    { dto.changeDirection = v; return this; }
        public TrendDTO build()                     { return dto; }
    }
}
