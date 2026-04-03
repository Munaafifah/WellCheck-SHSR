// package com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis;

// import javax.persistence.Entity;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;

// @Entity // This tells Hibernate to make a table out of this class
// public class Diagnosis {
 
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private String id; // primary key
//     private String patientId;
//     private String diagnosis;

//     // Default Constructor
//     public Diagnosis() {
//     }

    
//     // Parameterized Constructor
//     public Diagnosis(String patientId, String diagnosis) {
//         this.patientId = patientId;
//         this.diagnosis = diagnosis;
//     }

//     // Getters and setters...
//     public String getId() {
//         return id;
//     }

//     public void setId(String documentId) {
//         this.id = documentId;
//     }

//     public String getPatientId() {
//         return patientId;
//     }

//     public void setPatientId(String patientId) {
//         this.patientId = patientId;
//     }

//     public String getDiagnosis() {
//         return diagnosis;
//     }

//     public void setDiagnosis(String diagnosis) {
//         this.diagnosis = diagnosis;
//     }

//     // toString method
//     @Override
//     public String toString() {
//         return "Diagnosis{" +
//                 "id=" + id +
//                 ", patientId='" + patientId + '\'' +
//                 ", diagnosis='" + diagnosis + '\'' +
//                 '}';
//     }
// }


//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis;

import java.time.Instant;

public class Diagnosis {

    private String diagnosisId; // Unique ID for each diagnosis
    private String patientId;   // Redundant but useful for clarity
    private String doctorId;    // Doctor who added it
    private String diagnosis;   // Disease/Condition name
    private String diagnosisConfirmation; // e.g., "confirmed", "pending"
    private String remarks;     // Doctor’s remarks
    private String timestamp;   // ISO timestamp (Instant.toString())

    // Default constructor
    public Diagnosis() {}

    // Constructor without ID
    public Diagnosis(String patientId, String doctorId, String diagnosis,
                     String diagnosisConfirmation, String remarks) {
        this.diagnosisId = null; // To be set when saved
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
        this.diagnosisConfirmation = diagnosisConfirmation;
        this.remarks = remarks;
        this.timestamp = Instant.now().toString();
    }

    // Getters and setters
    public String getDiagnosisId() {
        return diagnosisId;
    }

    public void setDiagnosisId(String diagnosisId) {
        this.diagnosisId = diagnosisId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDiagnosisConfirmation() {
        return diagnosisConfirmation;
    }

    public void setDiagnosisConfirmation(String diagnosisConfirmation) {
        this.diagnosisConfirmation = diagnosisConfirmation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
