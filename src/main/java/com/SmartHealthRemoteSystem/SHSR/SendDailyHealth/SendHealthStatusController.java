// MongoDB
package com.SmartHealthRemoteSystem.SHSR.SendDailyHealth;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.ProvideDiagnosis.Diagnosis;
import com.SmartHealthRemoteSystem.SHSR.Service.*;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/Health-status")
public class SendHealthStatusController {

    @Autowired private HealthStatusService healthStatusService;
    @Autowired private PatientService patientService;
    @Autowired private DoctorService doctorService;
    @Autowired private PredictionService predictionService;
    @Autowired private MailService mailService;   // ✅ Mail service injected

    /* ──────────────────────────────────────────────────────────────
       1. Patient submits daily health status / symptom
       ────────────────────────────────────────────────────────────── */
   @PostMapping("/sendHealthStatus")
public String sendHealthStatus(@RequestParam("symptom") List<String> symptoms,
                               @RequestParam("patientId") String patientId,
                               @RequestParam("doctorId") String doctorId,
                               Model model) throws ExecutionException, InterruptedException {

    // Save health-status record
    HealthStatus hs = new HealthStatus();
    hs.setHealthStatusId(UUID.randomUUID().toString());
    hs.setAdditionalNotes(String.join(", ", symptoms)); // ✅ fixed
    hs.setDoctorId(doctorId);
    hs.setTimestamp(Instant.now());

    healthStatusService.createHealthStatus(hs, patientId);

    // Fetch patient & doctor
    Patient patient = patientService.getPatientById(patientId);
    Doctor  doctor  = doctorService.getDoctor(doctorId);

    // ✅ Notify doctor by email
    if (doctor != null && doctor.getEmail() != null && !doctor.getEmail().isEmpty()) {
    System.out.println("⚠️ Sending symptom email to doctor: " + doctor.getEmail());

    String subject = "Daily Health Symptom from " + patient.getName();
    String message = "Dear Dr. " + doctor.getName() + ",\n\n"
                   + "Your patient " + patient.getName() + " has submitted a new daily health status.\n"
                   + "Symptom / Note: " + String.join(", ", symptoms) + "\n\n"
                   + "Please log in to WellCheck to review the details.\n\n"
                   + "Regards,\nWellCheck System";

    mailService.sendMail(doctor.getEmail(), subject, message);
    System.out.println("✅ Email method called for: " + doctor.getEmail());
}


    model.addAttribute("patient", patient);
    model.addAttribute("doctor", doctor);

    return "patientDashBoard";
}


    /* ──────────────────────────────────────────────────────────────
       2. Display form for patient to enter daily health status
       ────────────────────────────────────────────────────────────── */
    @PostMapping("/viewHealthStatusForm")
    public String healthStatusForm(@RequestParam("patientId") String patientId,
                                   Model model) throws ExecutionException, InterruptedException {

        Patient patient = patientService.getPatientById(patientId);
        model.addAttribute("patient", patient);

        /* Load assigned doctor (if any) */
        if (patient.getAssigned_doctor() != null && !patient.getAssigned_doctor().isEmpty()) {
            Doctor doctor = doctorService.getDoctor(patient.getAssigned_doctor());
            model.addAttribute("doctor", doctor);
        } else {
            model.addAttribute("doctor", null);
        }

        /* (Optional) show recent prediction + formatted symptoms */
        Optional<Prediction> predictions = predictionService.getRecentPrediction(patientId);
        model.addAttribute("predictions", predictions.orElse(null));

        List<String> formattedSymptoms = new ArrayList<>();
        predictions.ifPresent(pred -> formattedSymptoms.addAll(formatSymptoms(pred.getSymptomsList())));
        model.addAttribute("formattedSymptoms", formattedSymptoms);

        /* placeholder for any sensor-based symptoms you might add */
        model.addAttribute("sensorBasedSymptoms", new ArrayList<String>());

        return "sendDailyHealthSymptom";
    }

    /* ──────────────────────────────────────────────────────────────
       3. Doctor view – diagnosis page with approved / rejected lists
       ────────────────────────────────────────────────────────────── */
  @GetMapping("/Diagnosis")
public String showDiagnosisPage(@RequestParam("patientId") String patientId,
                                @RequestParam("doctorId") String doctorId,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size,
                                Model model) throws ExecutionException, InterruptedException {

    Patient patient = patientService.getPatientById(patientId);
    Doctor doctor = doctorService.getDoctor(doctorId);

    Map<String, Prescription> prescriptionMap = patient.getPrescription();
    Map<String, Diagnosis> diagnosisMap = patient.getDiagnosis();

    List<Map<String, Object>> historyList = new ArrayList<>();

    for (Map.Entry<String, Prescription> entry : prescriptionMap.entrySet()) {
        Prescription prescription = entry.getValue();
        Diagnosis matchedDiagnosis = null;

        String prescriptionTimestamp = prescription.getTimestamp().toString();

        for (Diagnosis diag : diagnosisMap.values()) {
            if (diag.getTimestamp().toString().equals(prescriptionTimestamp)) {
                matchedDiagnosis = diag;
                break;
            }
        }

        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("timestamp", prescription.getTimestamp());
        historyEntry.put("prescription", prescription);
        historyEntry.put("diagnosis", matchedDiagnosis);
        historyList.add(historyEntry);
    }

    // Sort newest first
   historyList.sort((a, b) ->
    ((Instant) b.get("timestamp")).compareTo((Instant) a.get("timestamp"))
);


    int start = Math.min(page * size, historyList.size());
    int end = Math.min(start + size, historyList.size());
    List<Map<String, Object>> pagedList = historyList.subList(start, end);

    int totalPages = (int) Math.ceil((double) historyList.size() / size);

    model.addAttribute("patient", patient);
    model.addAttribute("doctor", doctor);
    model.addAttribute("historyList", pagedList);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", totalPages);
    model.addAttribute("pageSize", size);

    return "Diagnosis";
}


    /* ──────────────────────────────────────────────────────────────
       Utility: Capitalize & format symptom strings
       ────────────────────────────────────────────────────────────── */
    private List<String> formatSymptoms(List<String> symptoms) {
        List<String> formatted = new ArrayList<>();
        for (String s : symptoms) {
            formatted.add(capitalizeWords(s.replace("_", " ")));
        }
        return formatted;
    }

    private String capitalizeWords(String str) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char ch : str.toCharArray()) {
            if (Character.isWhitespace(ch)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                ch = Character.toTitleCase(ch);
                capitalizeNext = false;
            }
            result.append(ch);
        }
        return result.toString();
    }
}
