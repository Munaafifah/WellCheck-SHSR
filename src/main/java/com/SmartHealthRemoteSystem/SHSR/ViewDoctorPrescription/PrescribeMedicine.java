

//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription;

public class PrescribeMedicine {

    private String medName;
    private int prescribedQuantity;

    public PrescribeMedicine() {
    }

    public PrescribeMedicine(String medName, int prescribedQuantity) {
        this.medName = medName;
        this.prescribedQuantity = prescribedQuantity;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public int getPrescribedQuantity() {
        return prescribedQuantity;
    }

    public void setPrescribedQuantity(int prescribedQuantity) {
        this.prescribedQuantity = prescribedQuantity;
    }
}
