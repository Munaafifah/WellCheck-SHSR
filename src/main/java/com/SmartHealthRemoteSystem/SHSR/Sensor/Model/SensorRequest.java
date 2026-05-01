package com.SmartHealthRemoteSystem.SHSR.Sensor.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "SensorRequest")
public class SensorRequest {

    @Id
    private String requestId;
    private String patientId;
    private String patientName;
    private String patientEmail;
    private String status; // PENDING, APPROVED, REJECTED
    private Instant requestedAt;

    public SensorRequest() {
        this.requestedAt = Instant.now();
        this.status = "PENDING";
    }

    public SensorRequest(String patientId, String patientName, String patientEmail) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.status = "PENDING";
        this.requestedAt = Instant.now();
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
}