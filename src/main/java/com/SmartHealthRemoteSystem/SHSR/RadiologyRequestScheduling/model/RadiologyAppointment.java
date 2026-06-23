package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * MongoDB document representing a scheduled radiology appointment.
 * Collection: RadiologyAppointment
 *
 * UCR010 — created by Radiologist / Radiographer after request APPROVED
 * UCR015 — cancelled by Radiologist / Radiographer with mandatory reason
 */
@Document(collection = "RadiologyAppointment")
@TypeAlias("com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.RadiologyAppointment")
public class RadiologyAppointment {

    @Id
    private String appointmentId;

    @Indexed
    private String requestId;

    private Date              appointmentDate;
    private String            timeSlot;
    private String            equipment;

    @Indexed
    private String            radiographerId;

    @Indexed
    private AppointmentStatus status;

    private String            cancellationReason;
    private String            cancelledBy;
    private Date              createdDate;
    private Date              updatedDate;

    public RadiologyAppointment() {}

    public RadiologyAppointment(String appointmentId, String requestId,
                                Date appointmentDate, String timeSlot,
                                String equipment, String radiographerId,
                                AppointmentStatus status,
                                String cancellationReason, String cancelledBy,
                                Date createdDate, Date updatedDate) {
        this.appointmentId      = appointmentId;
        this.requestId          = requestId;
        this.appointmentDate    = appointmentDate;
        this.timeSlot           = timeSlot;
        this.equipment          = equipment;
        this.radiographerId     = radiographerId;
        this.status             = status;
        this.cancellationReason = cancellationReason;
        this.cancelledBy        = cancelledBy;
        this.createdDate        = createdDate;
        this.updatedDate        = updatedDate;
    }

    // --- Getters ---
    public String            getAppointmentId()      { return appointmentId; }
    public String            getRequestId()          { return requestId; }
    public Date              getAppointmentDate()    { return appointmentDate; }
    public String            getTimeSlot()           { return timeSlot; }
    public String            getEquipment()          { return equipment; }
    public String            getRadiographerId()     { return radiographerId; }
    public AppointmentStatus getStatus()             { return status; }
    public String            getCancellationReason() { return cancellationReason; }
    public String            getCancelledBy()        { return cancelledBy; }
    public Date              getCreatedDate()        { return createdDate; }
    public Date              getUpdatedDate()        { return updatedDate; }

    // --- Setters ---
    public void setAppointmentId(String v)           { this.appointmentId = v; }
    public void setRequestId(String v)               { this.requestId = v; }
    public void setAppointmentDate(Date v)           { this.appointmentDate = v; }
    public void setTimeSlot(String v)                { this.timeSlot = v; }
    public void setEquipment(String v)               { this.equipment = v; }
    public void setRadiographerId(String v)          { this.radiographerId = v; }
    public void setStatus(AppointmentStatus v)       { this.status = v; }
    public void setCancellationReason(String v)      { this.cancellationReason = v; }
    public void setCancelledBy(String v)             { this.cancelledBy = v; }
    public void setCreatedDate(Date v)               { this.createdDate = v; }
    public void setUpdatedDate(Date v)               { this.updatedDate = v; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String            appointmentId;
        private String            requestId;
        private Date              appointmentDate;
        private String            timeSlot;
        private String            equipment;
        private String            radiographerId;
        private AppointmentStatus status;
        private String            cancellationReason;
        private String            cancelledBy;
        private Date              createdDate;
        private Date              updatedDate;

        public Builder appointmentId(String v)       { this.appointmentId = v;      return this; }
        public Builder requestId(String v)           { this.requestId = v;          return this; }
        public Builder appointmentDate(Date v)       { this.appointmentDate = v;    return this; }
        public Builder timeSlot(String v)            { this.timeSlot = v;           return this; }
        public Builder equipment(String v)           { this.equipment = v;          return this; }
        public Builder radiographerId(String v)      { this.radiographerId = v;     return this; }
        public Builder status(AppointmentStatus v)   { this.status = v;             return this; }
        public Builder cancellationReason(String v)  { this.cancellationReason = v; return this; }
        public Builder cancelledBy(String v)         { this.cancelledBy = v;        return this; }
        public Builder createdDate(Date v)           { this.createdDate = v;        return this; }
        public Builder updatedDate(Date v)           { this.updatedDate = v;        return this; }

        public RadiologyAppointment build() {
            return new RadiologyAppointment(appointmentId, requestId, appointmentDate,
                    timeSlot, equipment, radiographerId, status,
                    cancellationReason, cancelledBy, createdDate, updatedDate);
        }
    }
}
