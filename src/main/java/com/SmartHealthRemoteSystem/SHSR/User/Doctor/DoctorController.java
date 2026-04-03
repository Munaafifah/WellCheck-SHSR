package com.SmartHealthRemoteSystem.SHSR.User.Doctor;

import com.SmartHealthRemoteSystem.SHSR.Prediction.Prediction;
import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Controller.SensorDashboardController;
import com.SmartHealthRemoteSystem.SHSR.Service.DiseaseDescriptionService;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PredictionService;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    @Autowired private SensorDataService sensorDataService;
    @Autowired private PredictionService predictionService;

    @Autowired
    public DoctorController(DoctorService doctorService, PatientService patientService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    @Autowired
    private DiseaseDescriptionService descriptionService;

    @Autowired
     private SensorDashboardController dashboardController;


    @GetMapping
    public String getDoctorDashboard(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(myUserDetails.getUsername());
        List<Patient> patientList = doctorService.getListPatient();

        model.addAttribute("doctor", doctor);
        model.addAttribute("patientList", patientList);
        return "doctorDashboard";
    }

    @GetMapping("myPatient")
    public String getPatientListThatAssignedToDoctor(Model model,
                                                     @RequestParam(defaultValue = "0") int pageNo,
                                                     @RequestParam(defaultValue = "5") int pageSize,
                                                     @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {
        model.addAttribute("pageSize", pageSize);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(myUserDetails.getUsername());

        List<Patient> allPatients = doctorService.findAllPatientAssignToDoctor(doctor.getUserId());

        if (!searchQuery.isEmpty()) {
            allPatients = allPatients.stream()
                    .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase()) || p.getUserId().contains(searchQuery))
                    .collect(Collectors.toList());
        }

        int total = allPatients.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;

        List<Patient> patientList = allPatients.subList(start, end);

        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("patientList", patientList);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("doctor", doctor);
        return "myPatient";
    }

    @GetMapping("updateProfile")
    public String showEditProfile(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(userDetails.getUsername());
        model.addAttribute("doctor", doctor);
        return "editProfileDoctor";
    }

   @PostMapping("updateProfile/profile")
public String updateProfile(@ModelAttribute Doctor updatedDoctor,
                            @RequestParam("profileImage") MultipartFile imageFile,
                            Model model) throws Exception {
    Doctor existingDoctor = doctorService.getDoctor(updatedDoctor.getUserId());
    existingDoctor.setName(updatedDoctor.getName());
    existingDoctor.setContact(updatedDoctor.getContact());

    if (!imageFile.isEmpty()) {
        String fileType = imageFile.getContentType();
        if (fileType != null && fileType.startsWith("image/")) {
            byte[] imageBytes = imageFile.getBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            existingDoctor.setProfilePicture(base64);
            existingDoctor.setProfilePictureType(fileType);
        }
    }

    doctorService.updateDoctor(existingDoctor);
    return "redirect:/doctor";
}


     /* ======  SENSOR  DASHBOARD  ================================================= */
    @GetMapping("/sensorDashboard")
    public String viewSensorDashboard(@RequestParam("patientId") String patientId,
                                      @RequestParam(value = "page",       defaultValue = "1") int    page,
                                      @RequestParam(value = "filterDate", required  = false)  String filterDate,
                                      Model model) throws Exception {

        /* Delegate to the shared helper:
             patientId   → record to show
             role        → "doctor"  (back-button in the view relies on this)
             page        → current page number (history pagination)
             filterDate  → optional YYYY-MM-DD filter
        */
        return dashboardController.buildDashboard(patientId,
                                                  "doctor",
                                                  page,
                                                  filterDate,
                                                  model);
    }

    @GetMapping("manualDiagnosisRequests")
    public String getManualDiagnosisRequests(Model model) throws ExecutionException, InterruptedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        String doctorId = userDetails.getUsername();

        List<Patient> allPatients = patientService.getAllPatients();
        List<Patient> requests = allPatients.stream()
            .filter(p -> p.isNeedsManualDiagnosis() && doctorId.equals(p.getAssigned_doctor()))
            .collect(Collectors.toList());

        model.addAttribute("requests", requests);
        return "manualDiagnosisRequests";
    }

   
    @GetMapping("predictionReview")
    public String viewPredictions(
        @RequestParam(defaultValue = "") String searchQuery,
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize,    
         Model model) throws ExecutionException, InterruptedException {
   
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            String doctorId = userDetails.getUsername();

    List<Patient> allPatients = patientService.getAllPatients();
    List<Patient> assignedPatients = allPatients.stream()
            .filter(p -> doctorId.equals(p.getAssigned_doctor()))
            .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(searchQuery.toLowerCase())) // 👈 Filter by name
            .collect(Collectors.toList());

    List<Map.Entry<Patient, Prediction>> filteredEntries = new ArrayList<>();
    // Map<Patient, Prediction> latestPredictions = new LinkedHashMap<>();
    Map<String, String> descriptionMap = new HashMap<>();

    for (Patient patient : assignedPatients) {
        Optional<Prediction> latestPrediction = predictionService.getRecentPrediction(patient.getUserId());
        if (latestPrediction.isPresent()) {
            Prediction pred = latestPrediction.get();
            filteredEntries.add(new AbstractMap.SimpleEntry<>(patient, pred));
            // latestPredictions.put(patient, pred);

            for (String disease : pred.getDiagnosisList()) {
                descriptionMap.put(disease, descriptionService.getDescription(disease));
            }
        }
    }

    // 🔄 Pagination
    int total = filteredEntries.size();
    int start = Math.min(pageNo * pageSize, total);
    int end = Math.min(start + pageSize, total);
    List<Map.Entry<Patient, Prediction>> paginatedEntries = filteredEntries.subList(start, end);


    Map<Patient, Prediction> latestPredictions = paginatedEntries.stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
   
            model.addAttribute("latestPredictions", latestPredictions);
            model.addAttribute("descriptionMap", descriptionMap);
            model.addAttribute("doctor", doctorService.getDoctor(doctorId));
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalPages", (int) Math.ceil((double) total / pageSize));
    
            return "predictionReview";
}

@GetMapping("/predictionHistory")
public String viewPredictionHistory(
        @RequestParam("patientId") String patientId,
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize,
        Model model) throws ExecutionException, InterruptedException {

    Patient patient = patientService.getPatientById(patientId);
    List<Prediction> predictions = predictionService.getPatientPredictions(patientId);

    int total = predictions.size();
    int start = Math.min(pageNo * pageSize, total);
    int end = Math.min((pageNo + 1) * pageSize, total);
    int totalPages = (total + pageSize - 1) / pageSize;

    List<Prediction> pagedPredictions = predictions.subList(start, end);

    model.addAttribute("patient", patient);
    model.addAttribute("predictions", pagedPredictions);
    model.addAttribute("currentPage", pageNo);
    model.addAttribute("totalPages", totalPages);
    model.addAttribute("patientId", patientId);
    return "predictionHistory";
}



}

