package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.controller;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.CancelRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.ImagingRequestResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.ManageRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.SubmitImagingRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service.ImagingRequestService;
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
 * REST controller for the imaging request lifecycle.
 * UCR008 — POST   /api/imaging-requests                  (DOCTOR)
 * UCR009 — PATCH  /api/imaging-requests/{id}/manage      (RADIOLOGIST / RADIOGRAPHER)
 * UCR013 — GET    /api/imaging-requests/{id}             (ADMIN / DOCTOR)
 * UCR014 — PATCH  /api/imaging-requests/{id}/cancel      (RADIOLOGIST / RADIOGRAPHER)
 *          PATCH  /api/imaging-requests/{id}/in-progress (RADIOLOGIST / RADIOGRAPHER)
 *          PATCH  /api/imaging-requests/{id}/report-pending (RADIOLOGIST / RADIOGRAPHER)
 *          PATCH  /api/imaging-requests/{id}/report-ready   (RADIOLOGIST)
 */
@RestController
@RequestMapping("/api/imaging-requests")
public class ImagingRequestController {

    private final ImagingRequestService requestService;

    @Autowired
    public ImagingRequestController(ImagingRequestService requestService) {
        this.requestService = requestService;
    }

    // UCR008 — Doctor submits a new imaging request
    @PostMapping
    public ResponseEntity<ImagingRequestResponseDTO> submit(
            @Valid @RequestBody SubmitImagingRequestDTO dto) {
        ImagingRequestResponseDTO response = requestService.submitRequest(dto, currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UCR009 — Radiologist / Radiographer approves or rejects
    @PatchMapping("/{requestId}/manage")
    public ResponseEntity<ImagingRequestResponseDTO> manage(
            @PathVariable String requestId,
            @Valid @RequestBody ManageRequestDTO dto) {
        return ResponseEntity.ok(
                requestService.manageRequest(requestId, dto, currentUserId(), currentRole()));
    }

    // UCR014 — Cancel a PENDING or APPROVED request
@PatchMapping("/{requestId}/cancel")
public ResponseEntity<ImagingRequestResponseDTO> cancel(
        @PathVariable String requestId,
        @Valid @RequestBody CancelRequestDTO dto) {
    String userId = currentUserId();
    String role   = currentRole();
    System.out.println(">>> CANCEL DEBUG: userId=" + userId + " role=" + role + " requestId=" + requestId);
    return ResponseEntity.ok(
            requestService.cancelRequest(requestId, userId, role,
                    dto.getCancellationReason()));
}

    // Mark the linked request as IN_PROGRESS (called when appointment imaging begins)
    @PatchMapping("/{requestId}/in-progress")
    public ResponseEntity<ImagingRequestResponseDTO> inProgress(@PathVariable String requestId) {
        requestService.markInProgress(requestId);
        return ResponseEntity.ok(requestService.getRequest(requestId));
    }

    // Mark the request as REPORT_PENDING (imaging scan completed, awaiting radiology report)
    @PatchMapping("/{requestId}/report-pending")
    public ResponseEntity<ImagingRequestResponseDTO> reportPending(@PathVariable String requestId) {
        requestService.markReportPending(requestId);
        return ResponseEntity.ok(requestService.getRequest(requestId));
    }

    // Mark the request as REPORT_READY (radiologist has finalised the report)
    @PatchMapping("/{requestId}/report-ready")
    public ResponseEntity<ImagingRequestResponseDTO> reportReady(@PathVariable String requestId) {
        requestService.markReportReady(requestId);
        return ResponseEntity.ok(requestService.getRequest(requestId));
    }

    // UCR013 — single request detail
    @GetMapping("/{requestId}")
    public ResponseEntity<ImagingRequestResponseDTO> getRequest(@PathVariable String requestId) {
        return ResponseEntity.ok(requestService.getRequest(requestId));
    }

    // All requests submitted by the authenticated doctor
    @GetMapping("/my")
    public ResponseEntity<List<ImagingRequestResponseDTO>> myRequests() {
        return ResponseEntity.ok(requestService.getRequestsByDoctor(currentUserId()));
    }

    // All requests for a specific patient (Doctor / Radiologist view)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ImagingRequestResponseDTO>> byPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(requestService.getRequestsByPatient(patientId));
    }

    // All PENDING requests ordered by priority (manage requests page)
    @GetMapping("/pending")
    public ResponseEntity<List<ImagingRequestResponseDTO>> pendingByPriority() {
        return ResponseEntity.ok(requestService.getPendingByPriority());
    }

    // UCR016 — Requests whose imaging is done and awaiting a radiology report
    // (used to populate the Request ID dropdown on Create Report)
    @GetMapping("/awaiting-report")
    public ResponseEntity<List<ImagingRequestResponseDTO>> awaitingReport() {
        return ResponseEntity.ok(requestService.getRequestsAwaitingReport());
    }

    // Requests with a scheduled appointment, ready for the radiographer to upload images against
    // (used to populate the Request dropdown on Upload Medical Image)
    @GetMapping("/awaiting-imaging")
    public ResponseEntity<List<ImagingRequestResponseDTO>> awaitingImaging() {
        return ResponseEntity.ok(requestService.getRequestsAwaitingImaging());
    }

    // All requests — admin / radiologist overview
    @GetMapping
    public ResponseEntity<List<ImagingRequestResponseDTO>> all() {
        return ResponseEntity.ok(requestService.getAllRequests());
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