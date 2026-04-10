//MONGODB//
//V2//
package com.SmartHealthRemoteSystem.SHSR.User.Patient;

import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

@Controller
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    public PatientController(PatientService patientService, DoctorService doctorService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    // After login → go here
    @GetMapping
    public String getPatientDashboard(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();

        String patientId = myUserDetails.getUsername();

        // Fetch patient from MongoDB
        Patient patient = patientService.getPatientById(patientId);

        System.out.println("🖼️ Profile Picture Length: "
                + (patient.getProfilePicture() != null ? patient.getProfilePicture().length() : "null"));
        System.out.println("🖼️ Profile Picture Type: " + patient.getProfilePictureType());

        System.out.println("🔍 Checking Patient: " + patient);
        if (patient == null) {
            model.addAttribute("error", "Patient not found.");
            return "error"; // Show error.html
        }

        Doctor doctor = null;
        if (patient.getAssigned_doctor() != null && !patient.getAssigned_doctor().isEmpty()) {
            doctor = doctorService.getDoctor(patient.getAssigned_doctor());
        }

        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);

        return "patientDashboard";
    }

    @GetMapping("/editProfile")
    public String editProfile(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();

        Patient patient = patientService.getPatientById(userDetails.getUsername());
        model.addAttribute("patient", patient);
        return "editPatientProfile";
    }

    @PostMapping("/editProfile/submit")
    public String updatePatientProfile(@ModelAttribute Patient updatedPatient,
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model) throws Exception {
        // ✅ Fetch the original patient from DB
        Patient existingPatient = patientService.getPatientById(updatedPatient.getUserId());

        if (existingPatient == null) {
            model.addAttribute("error", "Patient not found.");
            return "editPatientProfile";
        }

        // ✅ Overwrite fields that are allowed to change
        existingPatient.setName(updatedPatient.getName());
        existingPatient.setContact(updatedPatient.getContact());
        existingPatient.setAddress(updatedPatient.getAddress());
        existingPatient.setEmergencyContact(updatedPatient.getEmergencyContact());

        // ✅ Handle image update
        if (!imageFile.isEmpty()) {
            String fileType = imageFile.getContentType();
            if (!fileType.startsWith("image/")) {
                model.addAttribute("error", "Only image files are allowed.");
                return "editPatientProfile";
            }

            System.out.println("📸 File name: " + imageFile.getOriginalFilename());
            System.out.println("📸 File type: " + imageFile.getContentType());
            System.out.println("📸 File size: " + imageFile.getSize());

            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            existingPatient.setProfilePicture(base64Image);
            existingPatient.setProfilePictureType(fileType); // ✅ SAVE TYPE

        }

        // ✅ Save updated patient
        patientService.updatePatient(existingPatient);

        return "redirect:/patient";
    }

    // Pagination//
    @GetMapping("/list")
    public String getAllPatientsWithPagination(Model model,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        String doctorId = myUserDetails.getUsername();

        List<Patient> allPatients = patientService.getAllPatients();

        List<Patient> assignedPatients = allPatients.stream()
                .filter(p -> doctorId.equals(p.getAssigned_doctor()))
                .collect(Collectors.toList());

        // Server-side search
        if (!searchQuery.isEmpty()) {
            assignedPatients = assignedPatients.stream()
                    .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase())
                            || p.getUserId().toLowerCase().contains(searchQuery.toLowerCase())
                            || (p.getEmail() != null && p.getEmail().toLowerCase().contains(searchQuery.toLowerCase())))
                    .collect(Collectors.toList());
        }

        int total = assignedPatients.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;

        List<Patient> patientList = assignedPatients.subList(start, end);

        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("patientList", patientList);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("doctor", patientService.getDoctor(doctorId));

        return "listAssignedPatient";
    }

    // ViewPrescription//
    @GetMapping("/viewPrescription")
public String viewLatestPrescription(@RequestParam(defaultValue = "0") int pageNo,
        Model model) throws Exception {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
    String patientId = userDetails.getUsername();

    Patient patient = patientService.getPatientById(patientId);
    Map<String, Prescription> prescriptions = patient.getPrescription();

    model.addAttribute("patient", patient);

    if (prescriptions == null || prescriptions.isEmpty()) {
        model.addAttribute("error", "No prescription found.");
        return "viewPrescription";
    }

    // Sort all prescriptions newest first
    List<Prescription> sortedList = prescriptions.values().stream()
            .sorted(Comparator.comparing(Prescription::getTimestamp).reversed())
            .collect(Collectors.toList());

    int totalPages = sortedList.size(); // 1 prescription per page
    int safePage = Math.max(0, Math.min(pageNo, totalPages - 1));

    Prescription currentPrescription = sortedList.get(safePage);
    Doctor doctor = null;
    try {
        if (currentPrescription.getDoctorId() != null) {
            doctor = doctorService.getDoctor(currentPrescription.getDoctorId());
        }
    } catch (Exception e) {
        System.out.println("⚠️ Could not fetch doctor: " + e.getMessage());
    }

    model.addAttribute("doctor", doctor);
    model.addAttribute("prescription", currentPrescription);
    model.addAttribute("currentPage", safePage);
    model.addAttribute("totalPages", totalPages);

    return "viewPrescription";
}

    // BackController//
    @PostMapping("/backDashboard")
    public String backToDashboard(@RequestParam("patientId") String patientId, Model model) throws Exception {
        Patient patient = patientService.getPatientById(patientId);

        Doctor doctor = null;
        if (patient.getAssigned_doctor() != null && !patient.getAssigned_doctor().isEmpty()) {
            doctor = doctorService.getDoctor(patient.getAssigned_doctor());
        }

        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);

        return "patientDashboard";
    }

    // //Request Manual Diagnosis//
    @PostMapping("/requestManualDiagnosis")
    public String requestManualDiagnosis(@RequestParam String patientId,
            @RequestParam String doctorId,
            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        Patient patient = patientService.getPatientById(patientId);
        patient.setNeedsManualDiagnosis(true); // ✅ mark the request

        // ✅ ✅ ✅ INSERT DEBUG PRINTS HERE ✅ ✅ ✅
        System.out.println("✅ Manual Diagnosis request set for: " + patient.getUserId());
        System.out.println("🩺 Request status: " + patient.isNeedsManualDiagnosis());

        patientService.updatePatient(patient); // ✅ save update

        redirectAttributes.addFlashAttribute("success", "Request sent to doctor.");
        return "redirect:/predictionHistory?patientId=" + patientId;
    }

}
