
//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription;

import java.time.Instant;
import java.util.List;

public class Prescription {
    private String prescriptionId;
    private Instant timestamp;
    private String doctorId;
    private List<String> medicineList;
    private String prescriptionDescription;
    private String diagnosisAilmentDescription;
    private String appointmentId; // null if not from appointment
    private double drugCost; // 0 if not from appointment

    public Prescription() {
    }

    public Prescription(String doctorId, List<String> medicineList,
            String prescriptionDescription, String diagnosisAilmentDescription) {
        this.doctorId = doctorId;
        this.medicineList = medicineList;
        this.prescriptionDescription = prescriptionDescription;
        this.diagnosisAilmentDescription = diagnosisAilmentDescription;
        this.timestamp = Instant.now();
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public List<String> getMedicineList() {
        return medicineList;
    }

    public void setMedicineList(List<String> medicineList) {
        this.medicineList = medicineList;
    }

    public String getPrescriptionDescription() {
        return prescriptionDescription;
    }

    public void setPrescriptionDescription(String prescriptionDescription) {
        this.prescriptionDescription = prescriptionDescription;
    }

    public String getDiagnosisAilmentDescription() {
        return diagnosisAilmentDescription;
    }

    public void setDiagnosisAilmentDescription(String diagnosisAilmentDescription) {
        this.diagnosisAilmentDescription = diagnosisAilmentDescription;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public double getDrugCost() {
        return drugCost;
    }

    public void setDrugCost(double drugCost) {
        this.drugCost = drugCost;
    }
}
