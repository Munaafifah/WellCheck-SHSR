package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.ImagingRequestResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.ManageRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.SubmitImagingRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.exception.ImagingRequestNotFoundException;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.ImagingRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestReview;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.ImagingRequestRepository;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.RequestReviewRepository;

/**
 * Core service for the imaging request lifecycle.
 * UCR008 — submit request (Doctor only)
 * UCR009 — approve / reject request (Radiologist / Radiographer)
 * UCR013 — view request status (Admin / Doctor)
 * UCR014 — cancel PENDING request (Doctor: own requests only) or PENDING/APPROVED (Radiologist / Radiographer)
 */
@Service
public class ImagingRequestService {

    private static final Logger logger = LoggerFactory.getLogger(ImagingRequestService.class);

    private final ImagingRequestRepository      requestRepository;
    private final RequestReviewRepository       reviewRepository;
    private final PrioritizationService         prioritizationService;
    private final SchedulingNotificationService notificationService;
    private final RequestSequenceService        sequenceService;
    private final AuditLogService               auditLogService;

    @Autowired
    public ImagingRequestService(ImagingRequestRepository requestRepository,
                                 RequestReviewRepository reviewRepository,
                                 PrioritizationService prioritizationService,
                                 SchedulingNotificationService notificationService,
                                 RequestSequenceService sequenceService,
                                 AuditLogService auditLogService) {
        this.requestRepository    = requestRepository;
        this.reviewRepository     = reviewRepository;
        this.prioritizationService = prioritizationService;
        this.notificationService  = notificationService;
        this.sequenceService      = sequenceService;
        this.auditLogService      = auditLogService;
    }

    // UCR008 — Doctor submits a new imaging request
    public ImagingRequestResponseDTO submitRequest(SubmitImagingRequestDTO dto, String submitterId) {
        int score = prioritizationService.computeScore(dto.getUrgencyLevel(), dto.getClinicalNotes());

        ImagingRequest request = ImagingRequest.builder()
                .requestId(sequenceService.nextRequestId())
                .patientId(dto.getPatientId())
                .doctorId(submitterId)
                .modality(dto.getModality())
                .bodyPart(dto.getBodyPart())
                .urgencyLevel(dto.getUrgencyLevel())
                .clinicalNotes(dto.getClinicalNotes())
                .referringPhysician(dto.getReferringPhysician())
                .priorityScore(score)
                .status(RequestStatus.PENDING)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        ImagingRequest saved = requestRepository.save(request);
        logger.info("Imaging request {} submitted by {}", saved.getRequestId(), submitterId);

        auditLogService.log("IMAGING_REQUEST", saved.getRequestId(), "SUBMITTED",
                submitterId, "DOCTOR", null, "PENDING", null);

        notificationService.notifyRequestSubmitted(
                dto.getPatientId(), submitterId, saved.getRequestId(),
                score, dto.getUrgencyLevel().name());

        return ImagingRequestResponseDTO.fromEntity(saved);
    }

    // UCR009 — Radiologist / Radiographer approves or rejects a request
    public ImagingRequestResponseDTO manageRequest(String requestId, ManageRequestDTO dto,
                                                   String staffId, String staffRole) {
        ImagingRequest request = findOrThrow(requestId);

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING requests can be approved or rejected. Current status: " + request.getStatus());
        }

        if (dto.getStatus() == RequestStatus.REJECTED
                && (dto.getRejectionReason() == null || dto.getRejectionReason().isBlank())) {
            throw new IllegalArgumentException("A rejection reason is required when rejecting a request.");
        }

        String oldStatus = request.getStatus().name();

        request.setStatus(dto.getStatus());
        request.setRejectionReason(dto.getRejectionReason());
        if (dto.getStatus() == RequestStatus.APPROVED) {
            request.setRadiologistId(staffId);
        }
        request.setUpdatedDate(new Date());
        ImagingRequest updated = requestRepository.save(request);

        // Persist the review record for UCR009 audit trail
        RequestReview review = new RequestReview(
                UUID.randomUUID().toString(),
                requestId, staffId, staffRole,
                dto.getStatus().name(),
                null, dto.getRejectionReason(),
                new Date());
        reviewRepository.save(review);

        auditLogService.log("IMAGING_REQUEST", requestId, dto.getStatus().name(),
                staffId, staffRole, oldStatus, dto.getStatus().name(),
                dto.getRejectionReason());

        if (dto.getStatus() == RequestStatus.APPROVED) {
            notificationService.notifyRequestApproved(request.getDoctorId(), request.getPatientId(), requestId, staffId);
        } else if (dto.getStatus() == RequestStatus.REJECTED) {
            notificationService.notifyRequestRejected(
                    request.getDoctorId(), request.getPatientId(), requestId, dto.getRejectionReason());
        }

        logger.info("Request {} status changed to {} by {} (role={})",
                requestId, dto.getStatus(), staffId, staffRole);
        return ImagingRequestResponseDTO.fromEntity(updated);
    }

    // UCR014 — Cancel a PENDING request (Doctor: own requests only) or PENDING/APPROVED (Radiologist / Radiographer)
    public ImagingRequestResponseDTO cancelRequest(String requestId, String cancelledById,
                                                   String cancelledByRole, String cancellationReason) {
        ImagingRequest request = findOrThrow(requestId);

        // Guard: already cancelled
        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new IllegalStateException("This request has already been cancelled.");
        }

        boolean isRadiologyStaff = "RADIOLOGIST".equals(cancelledByRole) || "RADIOGRAPHER".equals(cancelledByRole);
        boolean isDoctor         = "DOCTOR".equals(cancelledByRole);

        // Guard: unknown role
        if (!isRadiologyStaff && !isDoctor) {
            throw new IllegalArgumentException("You do not have permission to cancel imaging requests.");
        }

        // Doctor rules: only own PENDING requests
        if (isDoctor) {
            if (!cancelledById.equals(request.getDoctorId())) {
                throw new IllegalArgumentException("You can only cancel your own imaging requests.");
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new IllegalStateException(
                        "Doctors can only cancel PENDING requests. Current status: " + request.getStatus());
            }
        }

        // Radiology staff rules: PENDING or APPROVED only (not SCHEDULED or beyond)
        if (isRadiologyStaff) {
            if (request.getStatus() == RequestStatus.SCHEDULED) {
                throw new IllegalStateException(
                        "This request cannot be cancelled because an appointment has already been scheduled.");
            }
            if (request.getStatus() != RequestStatus.PENDING && request.getStatus() != RequestStatus.APPROVED) {
                throw new IllegalStateException(
                        "Only PENDING or APPROVED requests can be cancelled. Current status: " + request.getStatus());
            }
        }

        String oldStatus = request.getStatus().name();

        request.setStatus(RequestStatus.CANCELLED);
        request.setCancellationReason(cancellationReason);
        request.setCancelledBy(cancelledById);
        request.setCancelledByRole(cancelledByRole);
        request.setUpdatedDate(new Date());
        ImagingRequest updated = requestRepository.save(request);

        auditLogService.log("IMAGING_REQUEST", requestId, "CANCELLED",
                cancelledById, cancelledByRole, oldStatus, "CANCELLED", cancellationReason);

        notificationService.notifyRequestCancelled(
                requestId, request.getDoctorId(), cancelledById);

        logger.info("Request {} cancelled by {} (role={}). Reason: {}",
                requestId, cancelledById, cancelledByRole, cancellationReason);

        return ImagingRequestResponseDTO.fromEntity(updated);
    }

    // UCR013 — View a single request
    public ImagingRequestResponseDTO getRequest(String requestId) {
        return ImagingRequestResponseDTO.fromEntity(findOrThrow(requestId));
    }

    // Retrieve all requests submitted by a given Doctor
    public List<ImagingRequestResponseDTO> getRequestsByDoctor(String submitterId) {
        return requestRepository.findByDoctorId(submitterId).stream()
                .map(ImagingRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Retrieve all requests for a patient
    public List<ImagingRequestResponseDTO> getRequestsByPatient(String patientId) {
        return requestRepository.findByPatientId(patientId).stream()
                .map(ImagingRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Retrieve all PENDING requests ordered by priority (manage requests page)
    public List<ImagingRequestResponseDTO> getPendingByPriority() {
    List<ImagingRequest> results = requestRepository
            .findByStatusOrderByPriorityScoreDesc(RequestStatus.PENDING);

    // DEBUG — remove after fixing
    System.out.println(">>> PENDING COUNT: " + results.size());
    results.forEach(r -> System.out.println(
            ">>> PENDING FOUND: " + r.getRequestId() + " | status=" + r.getStatus() + " | urgency=" + r.getUrgencyLevel()));

    return results.stream()
            .map(ImagingRequestResponseDTO::fromEntity)
            .collect(Collectors.toList());
}

    // Retrieve all requests (admin / radiologist overview), most recently updated first
    public List<ImagingRequestResponseDTO> getAllRequests() {
        return requestRepository.findAll().stream()
                .sorted(Comparator.comparing(ImagingRequest::getUpdatedDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(ImagingRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // UCR016 — Requests whose imaging is done and awaiting a radiology report
    // (REPORT_PENDING = imaging completed, no report authored yet)
    public List<ImagingRequestResponseDTO> getRequestsAwaitingReport() {
        return requestRepository.findByStatus(RequestStatus.REPORT_PENDING).stream()
                .map(ImagingRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Requests with a SCHEDULED or IN_PROGRESS appointment — eligible for image upload
    // (used to populate the Request dropdown on Upload Medical Image)
    public List<ImagingRequestResponseDTO> getRequestsAwaitingImaging() {
        List<ImagingRequest> scheduled  = requestRepository.findByStatus(RequestStatus.SCHEDULED);
        List<ImagingRequest> inProgress = requestRepository.findByStatus(RequestStatus.IN_PROGRESS);
        return java.util.stream.Stream.concat(scheduled.stream(), inProgress.stream())
                .map(ImagingRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Package-visible helpers used by AppointmentService -----------------------

    ImagingRequest findOrThrow(String requestId) {
    return requestRepository.findByRequestId(requestId)
            .orElseThrow(() -> new ImagingRequestNotFoundException(
                    "ImagingRequest not found: " + requestId));
}

    void markScheduled(String requestId) {
        ImagingRequest request = findOrThrow(requestId);
        String old = request.getStatus().name();
        request.setStatus(RequestStatus.SCHEDULED);
        request.setUpdatedDate(new Date());
        requestRepository.save(request);
        auditLogService.log("IMAGING_REQUEST", requestId, "SCHEDULED",
                "SYSTEM", "SYSTEM", old, "SCHEDULED", null);
    }

    void markApprovedAgain(String requestId) {
        ImagingRequest request = findOrThrow(requestId);
        if (request.getStatus() == RequestStatus.SCHEDULED) {
            request.setStatus(RequestStatus.APPROVED);
            request.setUpdatedDate(new Date());
            requestRepository.save(request);
            auditLogService.log("IMAGING_REQUEST", requestId, "REVERTED_TO_APPROVED",
                    "SYSTEM", "SYSTEM", "SCHEDULED", "APPROVED", "Appointment cancelled");
        }
    }

    public void markInProgress(String requestId) {
        ImagingRequest request = findOrThrow(requestId);
        if (request.getStatus() != RequestStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Request must be SCHEDULED before it can move to IN_PROGRESS. Current: "
                    + request.getStatus());
        }
        String old = request.getStatus().name();
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setUpdatedDate(new Date());
        requestRepository.save(request);
        auditLogService.log("IMAGING_REQUEST", requestId, "IN_PROGRESS",
                "SYSTEM", "SYSTEM", old, "IN_PROGRESS", null);
    }

    public void markReportPending(String requestId) {
        ImagingRequest request = findOrThrow(requestId);
        if (request.getStatus() != RequestStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Request must be IN_PROGRESS before it can move to REPORT_PENDING. Current: "
                    + request.getStatus());
        }
        String old = request.getStatus().name();
        request.setStatus(RequestStatus.REPORT_PENDING);
        request.setUpdatedDate(new Date());
        requestRepository.save(request);
        auditLogService.log("IMAGING_REQUEST", requestId, "REPORT_PENDING",
                "SYSTEM", "SYSTEM", old, "REPORT_PENDING", null);
    }

   public void markReportReady(String requestId) {
        ImagingRequest request = findOrThrow(requestId);
        if (request.getStatus() != RequestStatus.REPORT_PENDING) {
            throw new IllegalStateException(
                    "Request must be REPORT_PENDING before it can move to REPORT_READY. Current: "
                    + request.getStatus());
        }
        String old = request.getStatus().name();
        request.setStatus(RequestStatus.REPORT_READY);
        request.setUpdatedDate(new Date());
        requestRepository.save(request);
        auditLogService.log("IMAGING_REQUEST", requestId, "REPORT_READY",
                "SYSTEM", "SYSTEM", old, "REPORT_READY", null);
    }

    void markCompleted(String requestId) {
        ImagingRequest request = findOrThrow(requestId);
        String old = request.getStatus().name();
        request.setStatus(RequestStatus.COMPLETED);
        request.setUpdatedDate(new Date());
        requestRepository.save(request);
        auditLogService.log("IMAGING_REQUEST", requestId, "COMPLETED",
                "SYSTEM", "SYSTEM", old, "COMPLETED", null);
    }
}