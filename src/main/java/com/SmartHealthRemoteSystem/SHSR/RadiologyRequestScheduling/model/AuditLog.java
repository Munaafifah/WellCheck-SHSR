package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * System-wide audit trail for the Radiology Request & Scheduling module.
 * Every significant state transition is recorded here.
 * Collection: AuditLog
 */
@Document(collection = "AuditLog")
public class AuditLog {

    @Id
    private String logId;

    /** "IMAGING_REQUEST" or "APPOINTMENT" */
    @Indexed
    private String entityType;

    /** requestId or appointmentId */
    @Indexed
    private String entityId;

    /**
     * Action performed: SUBMITTED, APPROVED, REJECTED, SCHEDULED, CONFIRMED,
     * IN_PROGRESS, COMPLETED, REPORT_PENDING, REPORT_READY, CANCELLED, NO_SHOW
     */
    private String action;

    @Indexed
    private String performedBy;

    private String performedByRole;

    @Indexed
    private Date   performedAt;

    private String oldStatus;
    private String newStatus;

    /** Free-text context — reason, notes, or description of the action. */
    private String details;

    public AuditLog() {}

    public AuditLog(String logId, String entityType, String entityId,
                    String action, String performedBy, String performedByRole,
                    Date performedAt, String oldStatus, String newStatus, String details) {
        this.logId           = logId;
        this.entityType      = entityType;
        this.entityId        = entityId;
        this.action          = action;
        this.performedBy     = performedBy;
        this.performedByRole = performedByRole;
        this.performedAt     = performedAt;
        this.oldStatus       = oldStatus;
        this.newStatus       = newStatus;
        this.details         = details;
    }

    // --- Getters ---
    public String getLogId()           { return logId; }
    public String getEntityType()      { return entityType; }
    public String getEntityId()        { return entityId; }
    public String getAction()          { return action; }
    public String getPerformedBy()     { return performedBy; }
    public String getPerformedByRole() { return performedByRole; }
    public Date   getPerformedAt()     { return performedAt; }
    public String getOldStatus()       { return oldStatus; }
    public String getNewStatus()       { return newStatus; }
    public String getDetails()         { return details; }

    // --- Setters ---
    public void setLogId(String v)           { this.logId = v; }
    public void setEntityType(String v)      { this.entityType = v; }
    public void setEntityId(String v)        { this.entityId = v; }
    public void setAction(String v)          { this.action = v; }
    public void setPerformedBy(String v)     { this.performedBy = v; }
    public void setPerformedByRole(String v) { this.performedByRole = v; }
    public void setPerformedAt(Date v)       { this.performedAt = v; }
    public void setOldStatus(String v)       { this.oldStatus = v; }
    public void setNewStatus(String v)       { this.newStatus = v; }
    public void setDetails(String v)         { this.details = v; }
}
