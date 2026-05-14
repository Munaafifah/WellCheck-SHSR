package com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto;

import javax.validation.constraints.NotBlank;

public class CreateReportRequest {

    @NotBlank(message = "requestId must not be blank")
    private String requestId;

    @NotBlank(message = "radiologistId must not be blank")
    private String radiologistId;

    // Optional — capturing the requesting doctor enables automatic chat linking on finalization
    private String doctorId;

    private String findings;
    private String impression;

    // --- Getters ---
    public String getRequestId()     { return requestId; }
    public String getRadiologistId() { return radiologistId; }
    public String getDoctorId()      { return doctorId; }
    public String getFindings()      { return findings; }
    public String getImpression()    { return impression; }

    // --- Setters ---
    public void setRequestId(String requestId)         { this.requestId = requestId; }
    public void setRadiologistId(String radiologistId) { this.radiologistId = radiologistId; }
    public void setDoctorId(String doctorId)           { this.doctorId = doctorId; }
    public void setFindings(String findings)           { this.findings = findings; }
    public void setImpression(String impression)       { this.impression = impression; }
}
