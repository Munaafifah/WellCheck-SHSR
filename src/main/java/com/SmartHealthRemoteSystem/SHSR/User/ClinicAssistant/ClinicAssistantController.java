package com.SmartHealthRemoteSystem.SHSR.User.ClinicAssistant;

import com.SmartHealthRemoteSystem.SHSR.Service.ClinicAssistantService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Model.Appointment;
import com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Service.AppointmentHandler;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.Base64;

@Controller
@RequestMapping("/clinicassistant")
public class ClinicAssistantController {

    @Autowired
    private ClinicAssistantService clinicAssistantService;

    @Autowired
    private AppointmentHandler appointmentHandler;

    @Autowired
    private DoctorService doctorService;

    // ── Dashboard (appointment list) ──────────────────────────────────
    @GetMapping
    public String dashboard(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();

        ClinicAssistant ca = clinicAssistantService.getClinicAssistant(userDetails.getUsername());
        List<Appointment> allAppointments = appointmentHandler.getAllAppointments();

        long activeCount = allAppointments.stream()
                .filter(a -> !"Expired".equals(a.getStatusAppointment())).count();
        long expiredCount = allAppointments.stream()
                .filter(a -> "Expired".equals(a.getStatusAppointment())).count();
        long pendingCount = allAppointments.stream()
                .filter(a -> "Not Approved".equals(a.getStatusAppointment())).count();

        model.addAttribute("clinicAssistant", ca);
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("expiredCount", expiredCount);
        model.addAttribute("pendingCount", pendingCount); // ✅ ADD THIS

        return "ClinicAssistantDashboard";
    }

    // ── Approve / Cancel appointment ─────────────────────────────────
    @ResponseBody
    @PostMapping("/api/appointments/updateStatus")
    public Map<String, Object> updateStatus(@RequestBody Map<String, String> request) {
        String appointmentId = request.get("appointmentId");
        String newStatus = request.get("newStatus");
        return appointmentHandler.validateAndUpdateStatus(appointmentId, newStatus);
    }

    // ── Delete appointment ────────────────────────────────────────────
    @ResponseBody
    @DeleteMapping("/api/appointments/{appointmentId}")
    public Map<String, Object> deleteAppointment(@PathVariable String appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            appointmentHandler.deleteAppointment(appointmentId);
            response.put("success", true);
            response.put("message", "Appointment deleted successfully.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete appointment: " + e.getMessage());
        }
        return response;
    }

    // ── Add consultation & equipment cost ────────────────────────────
    @ResponseBody
    @PostMapping("/api/appointments/updateCost")
    public Map<String, Object> updateCost(@RequestBody Map<String, Object> request) {
        String appointmentId = (String) request.get("appointmentId");
        double consultationCost = Double.parseDouble(request.get("consultationCost").toString());
        double equipmentCost = Double.parseDouble(request.get("equipmentCost").toString());
        return appointmentHandler.updateAppointmentCost(appointmentId, consultationCost, equipmentCost);
    }

    // ── Appointment page (for Doctor view) ──────────────────────────────
    @GetMapping("/appointments")
    public String appointmentsPage(Model model,
            @RequestParam(defaultValue = "0") int activePageNo,
            @RequestParam(defaultValue = "0") int expiredPageNo,
            @RequestParam(defaultValue = "5") int pageSize) throws ExecutionException, InterruptedException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(userDetails.getUsername());

        List<Appointment> allAppointments = appointmentHandler.getAllAppointments();

        // Split active and expired
        List<Appointment> activeAppointments = allAppointments.stream()
                .filter(a -> !"Expired".equals(a.getStatusAppointment()))
                .collect(Collectors.toList());

        List<Appointment> expiredAppointments = allAppointments.stream()
                .filter(a -> "Expired".equals(a.getStatusAppointment()))
                .collect(Collectors.toList());

        // Paginate active
        int activeTotal = activeAppointments.size();
        int activeStart = Math.min(activePageNo * pageSize, activeTotal);
        int activeEnd = Math.min(activeStart + pageSize, activeTotal);

        // Paginate expired
        int expiredTotal = expiredAppointments.size();
        int expiredStart = Math.min(expiredPageNo * pageSize, expiredTotal);
        int expiredEnd = Math.min(expiredStart + pageSize, expiredTotal);

        model.addAttribute("doctor", doctor);
        model.addAttribute("activeAppointments", activeAppointments.subList(activeStart, activeEnd));
        model.addAttribute("expiredAppointments", expiredAppointments.subList(expiredStart, expiredEnd));
        model.addAttribute("activeCount", activeTotal);
        model.addAttribute("expiredCount", expiredTotal);
        model.addAttribute("activePageNo", activePageNo);
        model.addAttribute("expiredPageNo", expiredPageNo);
        model.addAttribute("activeTotalPages", (int) Math.ceil((double) activeTotal / pageSize));
        model.addAttribute("expiredTotalPages", (int) Math.ceil((double) expiredTotal / pageSize));
        model.addAttribute("pageSize", pageSize);

        return "updateStatusAppointment";
    }

    // ── Edit Profile ──────────────────────────────────────────────────────
    @GetMapping("/updateProfile")
    public String showEditProfile(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        ClinicAssistant ca = clinicAssistantService.getClinicAssistant(userDetails.getUsername());
        model.addAttribute("clinicAssistant", ca);
        return "editProfileClinicAssistant";
    }

    @PostMapping("/updateProfile/profile")
    public String saveUpdatedProfile(@ModelAttribute ClinicAssistant updated,
            @RequestParam("imageFile") MultipartFile imageFile) throws Exception {
        ClinicAssistant existing = clinicAssistantService.getClinicAssistant(updated.getUserId());
        existing.setName(updated.getName());
        existing.setContact(updated.getContact());
        existing.setClinic(updated.getClinic());
        existing.setPosition(updated.getPosition());

        if (!imageFile.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
            existing.setProfilePicture(base64);
            existing.setProfilePictureType(imageFile.getContentType());
        }

        clinicAssistantService.updateClinicAssistant(existing);
        return "redirect:/clinicassistant";
    }
}