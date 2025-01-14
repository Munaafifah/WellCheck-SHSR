package com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Controller;

import com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Model.Appointment;
import com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Service.AppointmentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doctor")
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentHandler appointmentHandler;


    @GetMapping("/appointments")
    public String appointmentsPage(Model model) {
        try {
            logger.info("Fetching all appointments");
            List<Appointment> allAppointments = appointmentHandler.getAllAppointments();
            
            if (allAppointments.isEmpty()) {
                logger.warn("No appointments found");
                model.addAttribute("message", "No appointments found in the system");
            } else {
                model.addAttribute("appointments", allAppointments);
                
                // Add counts for the tabs
                long activeCount = allAppointments.stream()
                    .filter(a -> !a.getStatusAppointment().equals("Expired"))
                    .count();
                long expiredCount = allAppointments.stream()
                    .filter(a -> a.getStatusAppointment().equals("Expired"))
                    .count();
                
                model.addAttribute("activeCount", activeCount);
                model.addAttribute("expiredCount", expiredCount);
            }
    
            return "updateStatusAppointment";
        } catch (Exception e) {
            logger.error("Error displaying appointments: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load appointments.");
            return "error";
        }
    }

    @GetMapping("/api/appointments/search")
    @ResponseBody
    public Map<String, Object> searchAppointments(@RequestParam(required = false) String type) {
        Map<String, Object> response = new HashMap<>();
        List<Appointment> allAppointments = appointmentHandler.getAllAppointments();
        
        if ("expired".equals(type)) {
            response.put("appointments", allAppointments.stream()
                .filter(a -> a.getStatusAppointment().equals("Expired"))
                .collect(Collectors.toList()));
        } else if ("active".equals(type)) {
            response.put("appointments", allAppointments.stream()
                .filter(a -> !a.getStatusAppointment().equals("Expired"))
                .collect(Collectors.toList()));
        } else {
            response.put("appointments", allAppointments);
        }
        
        return response;
    }

    @ResponseBody
    @PostMapping("/api/appointments/updateStatus")
    public Map<String, Object> updateAppointmentStatus(@RequestBody Map<String, String> request) {
        String appointmentId = request.get("appointmentId");
        String newStatus = request.get("newStatus");

        return appointmentHandler.validateAndUpdateStatus(appointmentId, newStatus);
    }

    @ResponseBody
    @PostMapping("/api/appointments/updateDateTime")
    public Map<String, Object> updateAppointmentDateTime(@RequestBody Map<String, String> request) {
        String appointmentId = request.get("appointmentId");
        String newDate = request.get("newDate");
        String newTime = request.get("newTime");
        
        logger.info("Updating appointment datetime: ID={}, newDate={}, newTime={}", 
            appointmentId, newDate, newTime);
        
        return appointmentHandler.validateAndUpdateDateTime(appointmentId, newDate, newTime);
    }

    @ResponseBody
    @GetMapping("/api/patient/{userId}/contact")
    public Map<String, Object> getPatientContact(@PathVariable String userId) {
        logger.info("Fetching contact info for patient: {}", userId);
        return appointmentHandler.getPatientContactInfo(userId);
    }
}

// @GetMapping("/appointments")
    // public String appointmentsPage(Model model) {
    //     try {
    //         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //         String userId = authentication.getName(); // Logged-in user ID
    
    //         logger.info("Fetching appointments for logged-in doctor (userId): {}", userId);
            
    //         String doctorId = userId;

    //         List<Appointment> appointments = appointmentHandler.getAppointmentsByDoctorId(doctorId);
    //         if (appointments.isEmpty()) {
    //             logger.warn("No appointments found for doctorId: {}", doctorId);
    //             model.addAttribute("message", "No appointments found for doctor ID: " + doctorId);
    //         } else {
    //             model.addAttribute("appointments", appointments);
    //         }
    
    //         return "updateStatusAppointment";
    //     } catch (Exception e) {
    //         logger.error("Error displaying appointments: {}", e.getMessage(), e);
    //         model.addAttribute("error", "Failed to load appointments.");
    //         return "error";
    //     }
    // }