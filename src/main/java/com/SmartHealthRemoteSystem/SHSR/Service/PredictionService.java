
//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    

    public String createPrediction(Prediction prediction, String patientId) throws ExecutionException, InterruptedException {
        Patient patient = patientRepository.get(patientId);
        if (patient == null) {
            throw new RuntimeException("Patient not found");
        }

        prediction.setApproved(false); // doctor must approve first


        String predictionId = UUID.randomUUID().toString();
        prediction.setPredictionID(predictionId);
        prediction.setTimestamp(Instant.now());

        if (patient.getPrediction() == null) {
            patient.setPrediction(new HashMap<>());
        }
        patient.getPrediction().put(predictionId, prediction);
       
        System.out.println("📌 Storing prediction ID: " + predictionId);
        System.out.println("📝 Prediction data: " + prediction);
        System.out.println("🧑‍⚕️ Patient ID: " + patientId);
        System.out.println("🧠 Current prediction map size: " + patient.getPrediction().size());

        patientRepository.save(patient);
        return prediction.getTimestamp().toString();
    }

    public List<Prediction> getPatientPredictions(String patientId) throws ExecutionException, InterruptedException {
        Patient patient = patientRepository.get(patientId);
        if (patient == null || patient.getPrediction() == null) {
            return Collections.emptyList();
        }

        List<Prediction> predictions = new ArrayList<>(patient.getPrediction().values());
        predictions.sort(Comparator.comparing(Prediction::getTimestamp).reversed());
        return predictions;
    }

    public Optional<Prediction> getRecentPrediction(String patientId) throws ExecutionException, InterruptedException {
        List<Prediction> predictions = getPatientPredictions(patientId);
        return predictions.stream().max(Comparator.comparing(Prediction::getTimestamp));
    }



public List<Prediction> getApprovedPredictions(String patientId) throws ExecutionException, InterruptedException {
    Patient patient = patientRepository.get(patientId);
    if (patient == null || patient.getPrediction() == null) {
        return Collections.emptyList();
    }

    return patient.getPrediction().values().stream()
            .filter(p -> p.isApproved() && !p.isRejected()) // ✅ Only approved and not rejected
            .sorted(Comparator.comparing(Prediction::getTimestamp).reversed())
            .collect(java.util.stream.Collectors.toList());
}

public List<Prediction> getRejectedPredictions(String patientId) throws ExecutionException, InterruptedException {
    Patient patient = patientRepository.get(patientId);
    if (patient == null || patient.getPrediction() == null) {
        return Collections.emptyList();
    }

    return patient.getPrediction().values().stream()
            .filter(p -> p.isRejected())
            .sorted(Comparator.comparing(Prediction::getTimestamp).reversed())
            .collect(Collectors.toList());
}




}
