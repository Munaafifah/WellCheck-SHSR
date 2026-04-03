//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.User.Patient;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.Diagnosis;
import com.SmartHealthRemoteSystem.SHSR.Symptoms.Symptom;


@Document(collection = "Patient")  // Store in same collection as Patient       
public class Patient extends User {

    private String address;
    private String emergencyContact;
    private String sensorDataId;
    private String assigned_doctor;
    private String status;
      private Map<String, Prediction> prediction = new HashMap<>();
      private Map<String, Prescription> prescription = new HashMap<>();
      private Map<String, Diagnosis> diagnosis = new HashMap<>();
    private String profilePicture;
    private String profilePictureType; // e.g., "image/png", "image/jpeg"
    private boolean needsManualDiagnosis = false;
    private Map<String, List<Symptom>> symptomsList = new HashMap<>();
    

    public Patient() {
        super();
    }

    public Patient(String userId, String name, String password, String contact, String role, String email,
                   String address, String emergencyContact, String sensorDataId, String assigned_doctor, String status) {
        super(userId, name, password, contact, role, email);
        this.address = address;
        this.emergencyContact = emergencyContact;
        this.sensorDataId = sensorDataId;
        this.assigned_doctor = assigned_doctor;
        this.status = status;
    }

   

    // Getters and Setters

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getSensorDataId() {
        return sensorDataId;
    }

    public String getProfilePicture() {
    return profilePicture;
}

    public void setSensorDataId(String sensorDataId) {
        this.sensorDataId = sensorDataId;
    }

    public String getAssigned_doctor() {
        return assigned_doctor;
    }

    public void setAssigned_doctor(String assigned_doctor) {
        this.assigned_doctor = assigned_doctor;
    }

    public String getStatus() {
        return status;
    }

       public Map<String, Prediction> getPrediction() {
        return prediction;
    }

    public Map<String, Prescription> getPrescription() {
         return prescription;
     }

     public Map<String, Diagnosis> getDiagnosis() {
    return diagnosis;
     }

    public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
    }



    public void setStatus(String status) {
        this.status = status;
    }
    
     public void setPrediction(Map<String, Prediction> prediction) {
        this.prediction = prediction;
    }

     public void setPrescription(Map<String, Prescription> prescription) {
         this.prescription = prescription;
     }

     public void setDiagnosis(Map<String, Diagnosis> diagnosis) {
    this.diagnosis = diagnosis;
     }

     public String getProfilePictureType() {
    return profilePictureType;
}

public void setProfilePictureType(String profilePictureType) {
    this.profilePictureType = profilePictureType;
}

public boolean isNeedsManualDiagnosis() {
    return needsManualDiagnosis;
}

public void setNeedsManualDiagnosis(boolean needsManualDiagnosis) {
    this.needsManualDiagnosis = needsManualDiagnosis;
}

public Map<String, List<Symptom>> getSymptomsList() {
    return symptomsList;
}

public void setSymptomsList(Map<String, List<Symptom>> symptomsList) {
    this.symptomsList = symptomsList;
}

    

     
}
