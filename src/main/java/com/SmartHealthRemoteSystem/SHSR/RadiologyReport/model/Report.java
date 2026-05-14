package com.SmartHealthRemoteSystem.SHSR.RadiologyReport.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "RadiologyReport")
@TypeAlias("com.SmartHealthRemoteSystem.SHSR.RadiologyReport.Report")
public class Report {

    @Id
    private String reportId;
    private String requestId;
    private String radiologistId;
    // Optional — captured at creation time so finalization can link report to doctor's chat
    private String doctorId;
    private String findings;
    private String impression;
    private String status;
    private Date createdDate;

    public Report() {}

    public Report(String reportId, String requestId, String radiologistId, String doctorId,
                  String findings, String impression, String status, Date createdDate) {
        this.reportId      = reportId;
        this.requestId     = requestId;
        this.radiologistId = radiologistId;
        this.doctorId      = doctorId;
        this.findings      = findings;
        this.impression    = impression;
        this.status        = status;
        this.createdDate   = createdDate;
    }

    // --- Getters ---
    public String getReportId()        { return reportId; }
    public String getRequestId()       { return requestId; }
    public String getRadiologistId()   { return radiologistId; }
    public String getDoctorId()        { return doctorId; }
    public String getFindings()        { return findings; }
    public String getImpression()      { return impression; }
    public String getStatus()          { return status; }
    public Date   getCreatedDate()     { return createdDate; }

    // --- Setters ---
    public void setReportId(String reportId)             { this.reportId = reportId; }
    public void setRequestId(String requestId)           { this.requestId = requestId; }
    public void setRadiologistId(String radiologistId)   { this.radiologistId = radiologistId; }
    public void setDoctorId(String doctorId)             { this.doctorId = doctorId; }
    public void setFindings(String findings)             { this.findings = findings; }
    public void setImpression(String impression)         { this.impression = impression; }
    public void setStatus(String status)                 { this.status = status; }
    public void setCreatedDate(Date createdDate)         { this.createdDate = createdDate; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String reportId;
        private String requestId;
        private String radiologistId;
        private String doctorId;
        private String findings;
        private String impression;
        private String status;
        private Date   createdDate;

        public Builder reportId(String reportId)             { this.reportId = reportId; return this; }
        public Builder requestId(String requestId)           { this.requestId = requestId; return this; }
        public Builder radiologistId(String radiologistId)   { this.radiologistId = radiologistId; return this; }
        public Builder doctorId(String doctorId)             { this.doctorId = doctorId; return this; }
        public Builder findings(String findings)             { this.findings = findings; return this; }
        public Builder impression(String impression)         { this.impression = impression; return this; }
        public Builder status(String status)                 { this.status = status; return this; }
        public Builder createdDate(Date createdDate)         { this.createdDate = createdDate; return this; }

        public Report build() {
            return new Report(reportId, requestId, radiologistId, doctorId,
                    findings, impression, status, createdDate);
        }
    }
}
