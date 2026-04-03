// package com.SmartHealthRemoteSystem.SHSR.Service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.Diagnosis;
// import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.DiagnosisRepository;

// import java.util.List;
// import java.util.concurrent.ExecutionException;

// @Service
// public class DiagnosisService {

//     private final DiagnosisRepository diagnosisRepository;

//     @Autowired
//     public DiagnosisService(DiagnosisRepository diagnosisRepository) {
//         this.diagnosisRepository = diagnosisRepository;
//     }
 
//     // Save diagnosis
//     public Diagnosis saveDiagnosis(Diagnosis diagnosis) throws ExecutionException, InterruptedException {
//         return diagnosisRepository.save(diagnosis);
//     }

//     // Get all diagnoses
//     public List<Diagnosis> getAllDiagnoses() throws ExecutionException, InterruptedException {
//         return diagnosisRepository.findAll();

        
//     }}


//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.Diagnosis;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.MongoPatientRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class DiagnosisService {

    @Autowired
    private MongoPatientRepository patientRepository;

    public String saveDiagnosis(String patientId, Diagnosis diagnosis) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (!optionalPatient.isPresent()) return "Patient not found.";

        Patient patient = optionalPatient.get();
        Map<String, Diagnosis> diagnoses = patient.getDiagnosis();
        if (diagnoses == null) diagnoses = new HashMap<>();

        String diagnosisId = UUID.randomUUID().toString();
        diagnosis.setDiagnosisId(diagnosisId);
        diagnosis.setTimestamp(Instant.now().toString());

        diagnoses.put(diagnosisId, diagnosis);
        patient.setDiagnosis(diagnoses);
        patientRepository.save(patient);

        return diagnosisId;
    }

    public List<Diagnosis> getAllDiagnoses(String patientId) {
    return patientRepository.findById(patientId)
            .map(p -> {
                Map<String, Diagnosis> map = p.getDiagnosis();
                return map != null ? new ArrayList<>(map.values()) : new ArrayList<Diagnosis>();
            })
            .orElse(new ArrayList<>());
}


    public Diagnosis getDiagnosis(String patientId, String diagnosisId) {
        return patientRepository.findById(patientId)
                .map(Patient::getDiagnosis)
                .map(d -> d.get(diagnosisId))
                .orElse(null);
    }

    public String updateDiagnosis(String patientId, Diagnosis updatedDiagnosis) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (!optionalPatient.isPresent()) return "Patient not found.";

        Patient patient = optionalPatient.get();
        Map<String, Diagnosis> diagnoses = patient.getDiagnosis();

        if (diagnoses == null || !diagnoses.containsKey(updatedDiagnosis.getDiagnosisId())) {
            return "Diagnosis ID not found.";
        }

        updatedDiagnosis.setTimestamp(Instant.now().toString());
        diagnoses.put(updatedDiagnosis.getDiagnosisId(), updatedDiagnosis);
        patient.setDiagnosis(diagnoses);
        patientRepository.save(patient);

        return "Diagnosis updated.";
    }

    public String deleteDiagnosis(String patientId, String diagnosisId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (!optionalPatient.isPresent()) return "Patient not found.";

        Patient patient = optionalPatient.get();
        Map<String, Diagnosis> diagnoses = patient.getDiagnosis();

        if (diagnoses != null && diagnoses.remove(diagnosisId) != null) {
            patient.setDiagnosis(diagnoses);
            patientRepository.save(patient);
            return "Diagnosis deleted.";
        }

        return "Diagnosis not found.";
    }
}
