package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.controller;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.AppointmentResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.CancelAppointmentDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.ScheduleAppointmentDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service.AppointmentService;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for radiology appointment operations.
 * UCR010 — POST   /api/appointments                       (RADIOGRAPHER)
 *          PATCH  /api/appointments/{id}/confirm          (RADIOLOGIST / RADIOGRAPHER)
 *          PATCH  /api/appointments/{id}/in-progress      (RADIOGRAPHER)
 *          PATCH  /api/appointments/{id}/complete         (RADIOGRAPHER)
 *          PATCH  /api/appointments/{id}/no-show          (RADIOLOGIST / RADIOGRAPHER)
 * UCR015 — PATCH  /api/appointments/{id}/cancel          (RADIOLOGIST / RADIOGRAPHER)
 *          GET    /api/appointments/{id}
 *          GET    /api/appointments/by-request/{requestId}
 *          GET    /api/appointments/my                    (RADIOGRAPHER — own)
 *          GET    /api/appointments
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // UCR010 — schedule a new appointment
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> schedule(
            @Valid @RequestBody ScheduleAppointmentDTO dto) {
        AppointmentResponseDTO response = appointmentService.scheduleAppointment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Confirm a SCHEDULED appointment
    @PatchMapping("/{appointmentId}/confirm")
    public ResponseEntity<AppointmentResponseDTO> confirm(@PathVariable String appointmentId) {
        return ResponseEntity.ok(
                appointmentService.confirmAppointment(appointmentId, currentUserId(), currentRole()));
    }

    // Mark appointment as IN_PROGRESS (imaging session started)
    @PatchMapping("/{appointmentId}/in-progress")
    public ResponseEntity<AppointmentResponseDTO> inProgress(@PathVariable String appointmentId) {
        return ResponseEntity.ok(
                appointmentService.markInProgress(appointmentId, currentUserId(), currentRole()));
    }

    // Mark appointment as COMPLETED (imaging done, report pending)
    @PatchMapping("/{appointmentId}/complete")
    public ResponseEntity<AppointmentResponseDTO> complete(@PathVariable String appointmentId) {
        return ResponseEntity.ok(
                appointmentService.completeAppointment(appointmentId, currentUserId(), currentRole()));
    }

    // Mark appointment as NO_SHOW; request reverts to APPROVED
    @PatchMapping("/{appointmentId}/no-show")
    public ResponseEntity<AppointmentResponseDTO> noShow(@PathVariable String appointmentId) {
        return ResponseEntity.ok(
                appointmentService.markNoShow(appointmentId, currentUserId(), currentRole()));
    }

    // UCR015 — cancel appointment with mandatory reason
    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancel(
            @PathVariable String appointmentId,
            @Valid @RequestBody CancelAppointmentDTO dto) {
        return ResponseEntity.ok(
                appointmentService.cancelAppointment(
                        appointmentId, dto.getCancellationReason(),
                        currentUserId(), currentRole()));
    }

    // Get a single appointment by ID
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentResponseDTO> get(@PathVariable String appointmentId) {
        return ResponseEntity.ok(appointmentService.getAppointment(appointmentId));
    }

    // Get the appointment linked to a specific imaging request
    @GetMapping("/by-request/{requestId}")
    public ResponseEntity<AppointmentResponseDTO> byRequest(@PathVariable String requestId) {
        return ResponseEntity.ok(appointmentService.getAppointmentByRequestId(requestId));
    }

    // Get all appointments assigned to the authenticated radiographer
    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponseDTO>> myAppointments() {
        return ResponseEntity.ok(appointmentService.getAppointmentsByRadiographer(currentUserId()));
    }

    // Get all appointments — admin / radiologist overview
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> all() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        return userDetails.getUser().getUserId();
    }

    private String currentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        return userDetails.getUser().getRole();
    }
}
