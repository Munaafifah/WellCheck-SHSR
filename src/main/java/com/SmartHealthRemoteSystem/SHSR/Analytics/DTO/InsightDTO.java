package com.SmartHealthRemoteSystem.SHSR.Analytics.DTO;

public class InsightDTO {

    public enum InsightType { SUCCESS, WARNING, CRITICAL, INFO }

    private InsightType type;
    private String      icon;
    private String      title;
    private String      message;
    private String      metric;
    private String      recommendation;

    public InsightDTO() {}

    public InsightDTO(InsightType type, String icon, String title,
                      String message, String metric, String recommendation) {
        this.type           = type;
        this.icon           = icon;
        this.title          = title;
        this.message        = message;
        this.metric         = metric;
        this.recommendation = recommendation;
    }

    // --- Getters ---
    public InsightType getType()           { return type; }
    public String      getIcon()           { return icon; }
    public String      getTitle()          { return title; }
    public String      getMessage()        { return message; }
    public String      getMetric()         { return metric; }
    public String      getRecommendation() { return recommendation; }

    // Thymeleaf helper — maps type to Bootstrap alert class
    public String getAlertClass() {
        if (type == null) return "alert-info";
        switch (type) {
            case SUCCESS:  return "alert-success";
            case WARNING:  return "alert-warning";
            case CRITICAL: return "alert-danger";
            default:       return "alert-info";
        }
    }

    // Thymeleaf helper — maps type to badge class
    public String getBadgeClass() {
        if (type == null) return "badge-info";
        switch (type) {
            case SUCCESS:  return "badge-success";
            case WARNING:  return "badge-warning";
            case CRITICAL: return "badge-danger";
            default:       return "badge-info";
        }
    }

    // --- Setters ---
    public void setType(InsightType v)          { this.type = v; }
    public void setIcon(String v)               { this.icon = v; }
    public void setTitle(String v)              { this.title = v; }
    public void setMessage(String v)            { this.message = v; }
    public void setMetric(String v)             { this.metric = v; }
    public void setRecommendation(String v)     { this.recommendation = v; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final InsightDTO dto = new InsightDTO();
        public Builder type(InsightType v)          { dto.type = v;           return this; }
        public Builder icon(String v)               { dto.icon = v;           return this; }
        public Builder title(String v)              { dto.title = v;          return this; }
        public Builder message(String v)            { dto.message = v;        return this; }
        public Builder metric(String v)             { dto.metric = v;         return this; }
        public Builder recommendation(String v)     { dto.recommendation = v; return this; }
        public InsightDTO build()                   { return dto; }
    }
}
