package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.AppointmentResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.ScheduleAppointmentDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.exception.AppointmentNotFoundException;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.exception.SlotUnavailableException;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.AppointmentStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.ImagingRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RadiologyAppointment;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.RadiologyAppointmentRepository;
import com.SmartHealthRemoteSystem.SHSR.Radiology.repository.MedicalImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UCR010 — Schedule a radiology appointment for an approved imaging request.
 * UCR015 — Cancel a scheduled appointment with mandatory reason (Radiologist / Radiographer).
 * Also handles: confirm, in-progress, no-show transitions (extended lifecycle).
 */
@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final RadiologyAppointmentRepository appointmentRepository;
    private final ImagingRequestService          requestService;
    private final SchedulingNotificationService  notificationService;
    private final AuditLogService                auditLogService;
    private final MedicalImageRepository         medicalImageRepository;

    @Autowired
    public AppointmentService(RadiologyAppointmentRepository appointmentRepository,
                              ImagingRequestService requestService,
                              SchedulingNotificationService notificationService,
                              AuditLogService auditLogService,
                              MedicalImageRepository medicalImageRepository) {
        this.appointmentRepository = appointmentRepository;
        this.requestService        = requestService;
        this.notificationService   = notificationService;
        this.auditLogService       = auditLogService;
        this.medicalImageRepository = medicalImageRepository;
    }

    // UCR010 — Radiographer creates a new appointment for an APPROVED request
    public AppointmentResponseDTO scheduleAppointment(ScheduleAppointmentDTO dto) {
        ImagingRequest request = requestService.findOrThrow(dto.getRequestId());

        if (request.getStatus() != RequestStatus.APPROVED) {
            throw new IllegalStateException(
                    "Only APPROVED requests can be scheduled. Current status: " + request.getStatus());
        }

        List<RadiologyAppointment> conflicts = appointmentRepository
                .findByAppointmentDateAndTimeSlotAndEquipment(
                        dto.getAppointmentDate(), dto.getTimeSlot(), dto.getEquipment());

        if (!conflicts.isEmpty()) {
            throw new SlotUnavailableException(
                    "The slot " + dto.getTimeSlot() + " on equipment '" + dto.getEquipment()
                    + "' is already booked for that date.");
        }

        RadiologyAppointment appointment = RadiologyAppointment.builder()
                .appointmentId(UUID.randomUUID().toString())
                .requestId(dto.getRequestId())
                .appointmentDate(dto.getAppointmentDate())
                .timeSlot(dto.getTimeSlot())
                .equipment(dto.getEquipment())
                .radiographerId(dto.getRadiographerId())
                .status(AppointmentStatus.SCHEDULED)
                .createdDate(new Date())
                .updatedDate(new Date())
                .build();

        RadiologyAppointment saved = appointmentRepository.save(appointment);
        requestService.markScheduled(dto.getRequestId());

        auditLogService.log("APPOINTMENT", saved.getAppointmentId(), "SCHEDULED",
                dto.getRadiographerId(), "RADIOGRAPHER", null, "SCHEDULED", null);

        String dateStr = formatDate(dto.getAppointmentDate());
        notificationService.notifyAppointmentScheduled(
                request.getPatientId(), request.getDoctorId(), dto.getRequestId(),
                saved.getAppointmentId(), dateStr, dto.getTimeSlot(), dto.getRadiographerId());

        logger.info("Appointment {} scheduled for request {}", saved.getAppointmentId(), dto.getRequestId());
        return AppointmentResponseDTO.fromEntity(saved);
    }

    // Confirm a SCHEDULED appointment (Radiologist / Radiographer)
    public AppointmentResponseDTO confirmAppointment(String appointmentId, String staffId, String staffRole) {
        RadiologyAppointment appointment = findOrThrow(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Only SCHEDULED appointments can be confirmed. Current: " + appointment.getStatus());
        }

        String oldStatus = appointment.getStatus().name();
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedDate(new Date());
        RadiologyAppointment updated = appointmentRepository.save(appointment);

        auditLogService.log("APPOINTMENT", appointmentId, "CONFIRMED",
                staffId, staffRole, oldStatus, "CONFIRMED", null);

        logger.info("Appointment {} confirmed by {} (role={})", appointmentId, staffId, staffRole);
        return AppointmentResponseDTO.fromEntity(updated);
    }

    // Mark a CONFIRMED appointment as IN_PROGRESS; request moves to IN_PROGRESS
    public AppointmentResponseDTO markInProgress(String appointmentId, String staffId, String staffRole) {
        RadiologyAppointment appointment = findOrThrow(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.CONFIRMED
                && appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Appointment must be SCHEDULED or CONFIRMED to mark IN_PROGRESS. Current: "
                    + appointment.getStatus());
        }

        String oldStatus = appointment.getStatus().name();
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        appointment.setUpdatedDate(new Date());
        RadiologyAppointment updated = appointmentRepository.save(appointment);

        requestService.markInProgress(appointment.getRequestId());

        auditLogService.log("APPOINTMENT", appointmentId, "IN_PROGRESS",
                staffId, staffRole, oldStatus, "IN_PROGRESS", null);

        logger.info("Appointment {} marked IN_PROGRESS by {} (role={})", appointmentId, staffId, staffRole);
        return AppointmentResponseDTO.fromEntity(updated);
    }

    // Mark an IN_PROGRESS appointment as COMPLETED; request moves through REPORT_PENDING → REPORT_READY
    public AppointmentResponseDTO completeAppointment(String appointmentId, String staffId, String staffRole) {
        RadiologyAppointment appointment = findOrThrow(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.IN_PROGRESS
                && appointment.getStatus() != AppointmentStatus.SCHEDULED
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Appointment must be IN_PROGRESS (or SCHEDULED/CONFIRMED) to complete. Current: "
                    + appointment.getStatus());
        }

        ImagingRequest request = requestService.findOrThrow(appointment.getRequestId());

        if (medicalImageRepository.findByRequestId(appointment.getRequestId()).isEmpty()) {
            throw new IllegalStateException(
                    "Cannot complete this appointment — no medical images have been uploaded for request "
                    + appointment.getRequestId() + ". Upload the imaging results before marking it complete.");
        }

        String oldStatus = appointment.getStatus().name();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setUpdatedDate(new Date());
        RadiologyAppointment updated = appointmentRepository.save(appointment);

        // The appointment may have skipped an explicit "in progress" step (radiographer
        // went straight from SCHEDULED/CONFIRMED to COMPLETED). markReportPending() requires
        // the request to already be IN_PROGRESS, so drive that transition here if needed.
        if (request.getStatus() == RequestStatus.SCHEDULED) {
            requestService.markInProgress(appointment.getRequestId());
        }
        requestService.markReportPending(appointment.getRequestId());

        auditLogService.log("APPOINTMENT", appointmentId, "COMPLETED",
                staffId, staffRole, oldStatus, "COMPLETED", null);

        notificationService.notifyAppointmentCompleted(
                request.getPatientId(), request.getDoctorId(), appointment.getRequestId(), appointmentId);

        logger.info("Appointment {} completed; request {} moved to REPORT_PENDING",
                appointmentId, appointment.getRequestId());

        return AppointmentResponseDTO.fromEntity(updated);
    }

    // Mark an appointment as NO_SHOW; request reverts to APPROVED
    public AppointmentResponseDTO markNoShow(String appointmentId, String staffId, String staffRole) {
        RadiologyAppointment appointment = findOrThrow(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only SCHEDULED or CONFIRMED appointments can be marked NO_SHOW. Current: "
                    + appointment.getStatus());
        }

        String oldStatus = appointment.getStatus().name();
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointment.setUpdatedDate(new Date());
        RadiologyAppointment updated = appointmentRepository.save(appointment);

        requestService.markApprovedAgain(appointment.getRequestId());

        auditLogService.log("APPOINTMENT", appointmentId, "NO_SHOW",
                staffId, staffRole, oldStatus, "NO_SHOW", null);

        logger.info("Appointment {} marked NO_SHOW by {} (role={})", appointmentId, staffId, staffRole);
        return AppointmentResponseDTO.fromEntity(updated);
    }

    // UCR015 — Cancel a SCHEDULED/CONFIRMED appointment; mandatory cancellation reason (BRQ043)
    public AppointmentResponseDTO cancelAppointment(String appointmentId,
                                                    String cancellationReason,
                                                    String cancelledById,
                                                    String cancelledByRole) {
        if (cancellationReason == null || cancellationReason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason is required (BRQ043).");
        }

        RadiologyAppointment appointment = findOrThrow(appointmentId);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment " + appointmentId + " is already cancelled.");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Completed appointments cannot be cancelled.");
        }

        ImagingRequest request = requestService.findOrThrow(appointment.getRequestId());

        String oldStatus = appointment.getStatus().name();
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(cancellationReason);
        appointment.setCancelledBy(cancelledById);
        appointment.setUpdatedDate(new Date());
        RadiologyAppointment updated = appointmentRepository.save(appointment);

        requestService.markApprovedAgain(appointment.getRequestId());

        auditLogService.log("APPOINTMENT", appointmentId, "CANCELLED",
                cancelledById, cancelledByRole, oldStatus, "CANCELLED", cancellationReason);

        notificationService.notifyAppointmentCancelled(
                request.getPatientId(), request.getDoctorId(), appointmentId);

        logger.info("Appointment {} cancelled by {} (role={}). Reason: {}",
                appointmentId, cancelledById, cancelledByRole, cancellationReason);

        return AppointmentResponseDTO.fromEntity(updated);
    }

    // Get a single appointment by its ID
    public AppointmentResponseDTO getAppointment(String appointmentId) {
        return AppointmentResponseDTO.fromEntity(findOrThrow(appointmentId));
    }

    // Get the appointment linked to a specific imaging request
    public AppointmentResponseDTO getAppointmentByRequestId(String requestId) {
        RadiologyAppointment appointment = appointmentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "No appointment found for request: " + requestId));
        return AppointmentResponseDTO.fromEntity(appointment);
    }

    // Get all appointments assigned to a radiographer
    public List<AppointmentResponseDTO> getAppointmentsByRadiographer(String radiographerId) {
        return appointmentRepository.findByRadiographerId(radiographerId).stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get all appointments (admin / radiologist view)
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private RadiologyAppointment findOrThrow(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException(
                        "Appointment not found: " + appointmentId));
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}