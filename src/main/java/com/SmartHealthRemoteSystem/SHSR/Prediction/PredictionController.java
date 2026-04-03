package com.SmartHealthRemoteSystem.SHSR.Prediction;

import com.SmartHealthRemoteSystem.SHSR.Service.*;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
public class PredictionController {

    @Autowired private PredictionService predictionService;
    @Autowired private PredictionRestController predictionRestController;
    @Autowired private PatientService patientService;
    @Autowired private DoctorService doctorService;
    @Autowired private MailService mailService;

    // ✅ New precaution service
    private final DiseasePrecautionService diseasePrecautionService = new DiseasePrecautionService();

    @PostMapping("/DiagnosisResult")
    public String makePrediction(@RequestParam("symptom[]") List<String> symptoms,
                                 @RequestParam("patientId") String patientId,
                                 Model model) throws ExecutionException, InterruptedException {

        ResponseEntity<String> response = predictionRestController.callDjangoAPI(symptoms);
        String predictionResult = response.getBody();
        System.out.println("🔥 Django full response: " + predictionResult);

        JsonObject jsonObject = new Gson().fromJson(predictionResult, JsonObject.class);
        JsonArray topDiseasesArray = jsonObject.getAsJsonArray("top_diseases");

        List<String> diseases = new ArrayList<>();
        List<Float> probabilities = new ArrayList<>();
        List<List<String>> precautions = new ArrayList<>();

        for (int i = 0; i < topDiseasesArray.size(); i++) {
            String diseaseWithProbability = topDiseasesArray.get(i).getAsString();
            String[] parts = diseaseWithProbability.split(": ");
            String diseaseName = parts[0];
            diseases.add(diseaseName);
            probabilities.add(Float.parseFloat(parts[1].replace("%", "")));

            List<String> precautionTips = diseasePrecautionService.getPrecautionsForDisease(diseaseName);
            precautions.add(precautionTips);
        }

        Prediction prediction = new Prediction();
        prediction.setDiagnosisList(diseases);
        prediction.setProbabilityList(probabilities);
        prediction.setSymptomsList(symptoms);
        prediction.setApproved(false);
        prediction.setRejected(false);

        String timeCreated = predictionService.createPrediction(prediction, patientId);

        Patient patient = patientService.getPatientById(patientId);
        // ✅ Add this line to verify assigned doctor
System.out.println("🩺 Assigned doctor ID: " + patient.getAssigned_doctor());
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
        System.out.println("📨 Doctor email: " + (doctor != null ? doctor.getEmail() : "Doctor not found"));

        // ✅ Send email notification to doctor
        if (doctor != null && doctor.getEmail() != null && !doctor.getEmail().isEmpty()) {
    String subject = "New Symptoms Submitted by " + patient.getName();
    String message = "Dear Dr. " + doctor.getName() + ",\n\n"
            + "Your patient " + patient.getName() + " has submitted new symptoms for diagnosis.\n"
            + "Symptoms: " + String.join(", ", symptoms) + "\n\n"
            + "Please review and approve the pending diagnosis in your dashboard.\n\n"
            + "Regards,\nWellCheck System";

    System.out.println("📧 Sending email to doctor: " + doctor.getEmail()); // 👈 ADD
    mailService.sendMail(doctor.getEmail(), subject, message);
    System.out.println("✅ Email sent to doctor: " + doctor.getEmail());   // 👈 ADD
}


        model.addAttribute("notice", "Your diagnosis has been submitted and is pending approval by your doctor.");
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
        model.addAttribute("symptoms", symptoms);
        model.addAttribute("diseases", diseases);
        model.addAttribute("probabilities", probabilities);
        model.addAttribute("precautions", precautions);
        model.addAttribute("timeCreated", timeCreated);

        return "DiagnosisResult";
    }

    @GetMapping("/predictionHistory")
    public String showPredictionHistory(@RequestParam("patientId") String patientId, Model model)
            throws ExecutionException, InterruptedException {
        
        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());

        

        List<Prediction> approvedList = predictionService.getApprovedPredictions(patientId);
        List<Prediction> rejectedList = predictionService.getRejectedPredictions(patientId);

        if (approvedList == null) approvedList = new ArrayList<>();
        if (rejectedList == null) rejectedList = new ArrayList<>();

        model.addAttribute("approvedList", approvedList);
        model.addAttribute("rejectedList", rejectedList);
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);

        return "Diagnosis";
    }
}
