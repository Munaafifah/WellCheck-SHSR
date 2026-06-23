package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.AuditLog;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Records every significant state transition across the Radiology module.
 * Called by ImagingRequestService and AppointmentService on every workflow action.
 */
@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Records a state transition. Never throws — audit failure must not block the main flow.
     *
     * @param entityType      "IMAGING_REQUEST" or "APPOINTMENT"
     * @param entityId        requestId or appointmentId
     * @param action          verb: SUBMITTED, APPROVED, REJECTED, SCHEDULED, CONFIRMED,
     *                        IN_PROGRESS, COMPLETED, REPORT_PENDING, REPORT_READY,
     *                        CANCELLED, NO_SHOW
     * @param performedBy     userId of the actor
     * @param performedByRole role name of the actor
     * @param oldStatus       previous status string (may be null on create)
     * @param newStatus       new status string
     * @param details         optional free-text (reason, notes, etc.)
     */
    public void log(String entityType, String entityId, String action,
                    String performedBy, String performedByRole,
                    String oldStatus, String newStatus, String details) {
        try {
            AuditLog entry = new AuditLog(
                    UUID.randomUUID().toString(),
                    entityType, entityId, action,
                    performedBy, performedByRole,
                    new Date(), oldStatus, newStatus, details);
            auditLogRepository.save(entry);
        } catch (Exception e) {
            logger.warn("Audit log write failed for entity {} action {}: {}", entityId, action, e.getMessage());
        }
    }

    public List<AuditLog> getHistoryForEntity(String entityId) {
        return auditLogRepository.findByEntityIdOrderByPerformedAtDesc(entityId);
    }

    public List<AuditLog> getHistoryByStaff(String staffId) {
        return auditLogRepository.findByPerformedByOrderByPerformedAtDesc(staffId);
    }
}
