package com.SmartHealthRemoteSystem.SHSR.Sensor.Controller;

import com.SmartHealthRemoteSystem.SHSR.Sensor.RegistrationResult;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Service.SensorRegistrationHandler;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.PatientSensorStatus;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.SensorRequest;
import com.SmartHealthRemoteSystem.SHSR.Sensor.Repository.SensorRequestRepository;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Controller
public class SensorController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private SensorRegistrationHandler sensorRegistrationHandler;

    @Autowired
    private SensorRequestRepository sensorRequestRepository;

    // ─────────────────────────────────────────────────────────────
    // PATIENT: Show registration page with correct state
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/register")
    public String showRegistrationPage(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatientById(myUserDetails.getUsername());

        model.addAttribute("patient", patient);

        Optional<SensorRequest> existingRequest = sensorRequestRepository
                .findByPatientId(patient.getUserId());
        if (existingRequest.isPresent()) {
            model.addAttribute("sensorRequest", existingRequest.get());
        }

        return "registrationPage";
    }

    // ─────────────────────────────────────────────────────────────
    // PATIENT: Show agreement page
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/sensor/agreement")
    public String showAgreementPage(Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatientById(myUserDetails.getUsername());
        model.addAttribute("patient", patient);
        return "sensorAgreementPage";
    }

    // ─────────────────────────────────────────────────────────────
    // PATIENT: Submit sensor request after agreeing
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/sensor/request")
    public String submitSensorRequest(RedirectAttributes redirectAttributes) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatientById(myUserDetails.getUsername());

        Optional<SensorRequest> existing = sensorRequestRepository
                .findByPatientId(patient.getUserId());
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You already have a pending or approved request.");
            return "redirect:/register";
        }

        SensorRequest newRequest = new SensorRequest(
                patient.getUserId(),
                patient.getName(),
                patient.getEmail());
        sensorRequestRepository.save(newRequest);

        redirectAttributes.addFlashAttribute("successMessage",
                "Your sensor request has been submitted. Please wait for admin approval.");
        return "redirect:/register";
    }

    // ─────────────────────────────────────────────────────────────
    // PATIENT: Register sensor using unique key (after approval)
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/api/sensors/register")
    public String registerSensor(@RequestParam Map<String, String> request, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatientById(myUserDetails.getUsername());

        if (patient.getSensorDataId() != null && !patient.getSensorDataId().isEmpty()) {
            model.addAttribute("errorMessage",
                    "You already have a registered sensor (ID: " + patient.getSensorDataId() + ")");
            model.addAttribute("patient", patient);
            return "registrationPage";
        }

        RegistrationResult result = sensorRegistrationHandler.registerSensor(
                patient.getUserId(),
                patient.getName(),
                request.get("sensorID"),
                request.get("uniqueKey"));

        if (result.isSuccess()) {
            patient.setSensorDataId(request.get("sensorID"));
            patientService.updatePatient(patient);

            Optional<SensorRequest> existingRequest = sensorRequestRepository
                    .findByPatientId(patient.getUserId());
            existingRequest.ifPresent(r -> {
                r.setStatus("APPROVED");
                sensorRequestRepository.save(r);
            });

            model.addAttribute("successMessage", "Sensor registered successfully!");
        } else {
            model.addAttribute("errorMessage", result.getErrorMessage());
        }

        model.addAttribute("patient", patient);
        return "registrationPage";
    }

    // ─────────────────────────────────────────────────────────────
    // ADMIN: View all sensor requests (Assign Sensor page)
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/admin/assign-sensor")
    public String viewAssignSensorPage(Model model) {
        List<SensorRequest> pendingRequests = sensorRequestRepository.findByStatus("PENDING");
        List<SensorRequest> approvedRequests = sensorRequestRepository.findByStatus("APPROVED");
        List<SensorRequest> rejectedRequests = sensorRequestRepository.findByStatus("REJECTED");

        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("approvedRequests", approvedRequests);
        model.addAttribute("rejectedRequests", rejectedRequests);

        return "assignSensorPage";
    }

    // ─────────────────────────────────────────────────────────────
    // ADMIN: Approve sensor request
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/admin/assign-sensor/approve/{requestId}")
    public String approveSensorRequest(@PathVariable String requestId,
            RedirectAttributes redirectAttributes) {
        try {
            sensorRegistrationHandler.approveSensorRequest(requestId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Request approved and unique key sent to patient email.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to approve request: " + e.getMessage());
        }
        return "redirect:/admin/assign-sensor";
    }

    // ─────────────────────────────────────────────────────────────
    // ADMIN: Reject sensor request
    // ─────────────────────────────────────────────────────────────
    @PostMapping("/admin/assign-sensor/reject/{requestId}")
    public String rejectSensorRequest(@PathVariable String requestId,
            RedirectAttributes redirectAttributes) {
        Optional<SensorRequest> request = sensorRequestRepository.findById(requestId);
        if (request.isPresent()) {
            SensorRequest sr = request.get();
            sr.setStatus("REJECTED");
            sensorRequestRepository.save(sr);
            redirectAttributes.addFlashAttribute("successMessage", "Request rejected.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Request not found.");
        }
        return "redirect:/admin/assign-sensor";
    }

    // ─────────────────────────────────────────────────────────────
    // ADMIN: View sensor status page
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/admin/sensor-status")
    public String viewSensorStatus(Model model) throws ExecutionException, InterruptedException {
        List<PatientSensorStatus> sensorStatuses = sensorRegistrationHandler.getAllPatientSensorStatus();
        model.addAttribute("sensorStatuses", sensorStatuses);
        return "sensorStatusPage";
    }
}