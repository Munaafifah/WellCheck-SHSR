package com.SmartHealthRemoteSystem.SHSR.Medicine;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Medicine") // Optional: you can name the collection
public class Medicine {

    @Id
    private String medId;

    private String medName;
    private int quantity;
    private String medType;     // You may want to include this if your UI uses it
    private String patientId;

    // --- Constructors ---

    public Medicine() {
    }

    public Medicine(String medName) {
        this.medName = medName;
    }

    public Medicine(String medName, int quantity) {
        this.medName = medName;
        this.quantity = quantity;
    }

    public Medicine(String medName, String medId, int quantity) {
        this.medName = medName;
        this.medId = medId;
        this.quantity = quantity;
    }

    // --- Getters & Setters ---

    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMedType() {
        return medType;
    }

    public void setMedType(String medType) {
        this.medType = medType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
