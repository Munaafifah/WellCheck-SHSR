package com.SmartHealthRemoteSystem.SHSR.SendDailyHealth;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "HealthStatus")
public class HealthStatusDocumentWrapper {

    @Id
    private String id;

    private String patientId;
    private HealthStatus healthStatus;

    public HealthStatusDocumentWrapper() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public HealthStatus getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }
}
