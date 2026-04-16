//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.ViewPatientHealthStatus;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatus;
import com.SmartHealthRemoteSystem.SHSR.Service.DiseasePrecautionService;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.HealthStatusService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/viewPatientHealthStatus")
public class ViewPatientHealthStatusController {

    private final HealthStatusService healthStatusService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DiseasePrecautionService diseasePrecautionService = new DiseasePrecautionService();

    @Autowired
    public ViewPatientHealthStatusController(HealthStatusService healthStatusService,
            DoctorService doctorService,
            PatientService patientService) {
        this.healthStatusService = healthStatusService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    @PostMapping("/a")
    public String getPredictionSymptomHistoryForDoctor(@RequestParam("patientId") String patientId,
            @RequestParam("doctorId") String doctorId,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            Model model) throws ExecutionException, InterruptedException {
        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctor(doctorId);

        Map<String, Prediction> predictionMap = patient.getPrediction();

        List<Map.Entry<String, Prediction>> sortedPredictions = new ArrayList<>(predictionMap.entrySet());
        sortedPredictions.sort((a, b) -> b.getValue().getTimestamp().compareTo(a.getValue().getTimestamp()));

        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) sortedPredictions.size() / pageSize);
        int start = Math.min((pageNo - 1) * pageSize, sortedPredictions.size());
        int end = Math.min(start + pageSize, sortedPredictions.size());
        List<Map.Entry<String, Prediction>> displayList = sortedPredictions.subList(start, end);

        // Build precautions map: predictionId -> List<List<String>>
        Map<String, List<List<String>>> precautionsMap = new HashMap<>();
        for (Map.Entry<String, Prediction> entry : displayList) {
            List<List<String>> tips = new ArrayList<>();
            for (String disease : entry.getValue().getDiagnosisList()) {
                tips.add(diseasePrecautionService.getPrecautionsForDisease(disease));
            }
            precautionsMap.put(entry.getKey(), tips);
        }

        model.addAttribute("healthStatusList", displayList);
        model.addAttribute("precautionsMap", precautionsMap);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", totalPages);
        model.addAttribute("nextPage", Math.min(pageNo + 1, totalPages));
        model.addAttribute("prevPage", Math.max(pageNo - 1, 1));
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
        model.addAttribute("totalPages", totalPages); // add this
        model.addAttribute("totalPage", totalPages); // keep this too

        return "viewPatientHealthStatus"; // ← change from viewDailyHealthSymptom
    }

    @GetMapping("/b")
    public String getPredictionSymptomHistory(@RequestParam("patientId") String patientId,
            @RequestParam("doctorId") String doctorId,
            @RequestParam("pageNo") int pageNo,
            Model model) throws ExecutionException, InterruptedException {

        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctor(doctorId);

        Map<String, Prediction> predictionMap = patient.getPrediction();

        List<Map.Entry<String, Prediction>> sortedPredictions = new ArrayList<>(predictionMap.entrySet());
        sortedPredictions.sort((a, b) -> b.getValue().getTimestamp().compareTo(a.getValue().getTimestamp()));

        int pageSize = 5;
        int totalPages = (int) Math.ceil((double) sortedPredictions.size() / pageSize);
        int start = Math.min((pageNo - 1) * pageSize, sortedPredictions.size());
        int end = Math.min(start + pageSize, sortedPredictions.size());
        List<Map.Entry<String, Prediction>> displayList = sortedPredictions.subList(start, end);

        // Build precautions map: predictionId -> List<List<String>>
        Map<String, List<List<String>>> precautionsMap = new HashMap<>();
        for (Map.Entry<String, Prediction> entry : displayList) {
            List<List<String>> tips = new ArrayList<>();
            for (String disease : entry.getValue().getDiagnosisList()) {
                tips.add(diseasePrecautionService.getPrecautionsForDisease(disease));
            }
            precautionsMap.put(entry.getKey(), tips);
        }

        model.addAttribute("healthStatusList", displayList);
        model.addAttribute("precautionsMap", precautionsMap);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", totalPages);
        model.addAttribute("nextPage", Math.min(pageNo + 1, totalPages));
        model.addAttribute("prevPage", Math.max(pageNo - 1, 1));
        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);

        return "viewDailyHealthSymptom";
    }

    @PostMapping("/deletesymptom")
    public String deletesymptom(@RequestParam("patientId") String patientId,
            @RequestParam("doctorId") String doctorId,
            @RequestParam("healthstatus") String healthstatusId) {
        healthStatusService.deleteHealthStatus(healthstatusId, patientId);
        return "redirect:/viewPatientHealthStatus/b?patientId=" + patientId + "&doctorId=" + doctorId + "&pageNo=1";
    }
}