package com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto;

import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.model.Report;

import java.util.Date;

public class ReportResponseDTO {

    private String reportId;
    private String requestId;
    private String radiologistId;
    private String doctorId;
    private String findings;
    private String impression;
    private String status;
    private Date   createdDate;

    public ReportResponseDTO() {}

    public ReportResponseDTO(String reportId, String requestId, String radiologistId,
                             String doctorId, String findings, String impression,
                             String status, Date createdDate) {
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

        public ReportResponseDTO build() {
            return new ReportResponseDTO(reportId, requestId, radiologistId, doctorId,
                    findings, impression, status, createdDate);
        }
    }

    public static ReportResponseDTO fromEntity(Report report) {
        return ReportResponseDTO.builder()
                .reportId(report.getReportId())
                .requestId(report.getRequestId())
                .radiologistId(report.getRadiologistId())
                .doctorId(report.getDoctorId())
                .findings(report.getFindings())
                .impression(report.getImpression())
                .status(report.getStatus())
                .createdDate(report.getCreatedDate())
                .build();
    }
}
