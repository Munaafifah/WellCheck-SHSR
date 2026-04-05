package com.SmartHealthRemoteSystem.SHSR.User.ClinicAssistant;

import com.SmartHealthRemoteSystem.SHSR.Service.ClinicAssistantService;
import com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Model.Appointment;
import com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Service.AppointmentHandler;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/clinicassistant")
public class ClinicAssistantController {

    @Autowired
    private ClinicAssistantService clinicAssistantService;

    @Autowired
    private AppointmentHandler appointmentHandler;

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

        model.addAttribute("clinicAssistant", ca);
        model.addAttribute("appointments", allAppointments);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("expiredCount", expiredCount);

        return "ClinicAssistantDashboard";
    }

    // ── Approve / Cancel appointment ─────────────────────────────────
    @ResponseBody
    @PostMapping("/api/appointments/updateStatus")
    public Map<String, Object> updateStatus(@RequestBody Map<String, String> request) {
        String appointmentId = request.get("appointmentId");
        String newStatus     = request.get("newStatus");
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
        String appointmentId   = (String) request.get("appointmentId");
        double consultationCost = Double.parseDouble(request.get("consultationCost").toString());
        double equipmentCost    = Double.parseDouble(request.get("equipmentCost").toString());
        return appointmentHandler.updateAppointmentCost(appointmentId, consultationCost, equipmentCost);
    }
}