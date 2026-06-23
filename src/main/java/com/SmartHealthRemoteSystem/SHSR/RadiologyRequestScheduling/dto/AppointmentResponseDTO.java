package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.AppointmentStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RadiologyAppointment;

import java.util.Date;

/**
 * Read-only response DTO for RadiologyAppointment.
 */
public class AppointmentResponseDTO {

    private String            appointmentId;
    private String            requestId;
    private Date              appointmentDate;
    private String            timeSlot;
    private String            equipment;
    private String            radiographerId;
    private AppointmentStatus status;
    private Date              createdDate;

    public AppointmentResponseDTO() {}

    public AppointmentResponseDTO(String appointmentId, String requestId,
                                  Date appointmentDate, String timeSlot,
                                  String equipment, String radiographerId,
                                  AppointmentStatus status, Date createdDate) {
        this.appointmentId  = appointmentId;
        this.requestId      = requestId;
        this.appointmentDate = appointmentDate;
        this.timeSlot       = timeSlot;
        this.equipment      = equipment;
        this.radiographerId = radiographerId;
        this.status         = status;
        this.createdDate    = createdDate;
    }

    // --- Getters ---
    public String            getAppointmentId()  { return appointmentId; }
    public String            getRequestId()      { return requestId; }
    public Date              getAppointmentDate(){ return appointmentDate; }
    public String            getTimeSlot()       { return timeSlot; }
    public String            getEquipment()      { return equipment; }
    public String            getRadiographerId() { return radiographerId; }
    public AppointmentStatus getStatus()         { return status; }
    public Date              getCreatedDate()    { return createdDate; }

    // --- Setters ---
    public void setAppointmentId(String v)       { this.appointmentId = v; }
    public void setRequestId(String v)           { this.requestId = v; }
    public void setAppointmentDate(Date v)       { this.appointmentDate = v; }
    public void setTimeSlot(String v)            { this.timeSlot = v; }
    public void setEquipment(String v)           { this.equipment = v; }
    public void setRadiographerId(String v)      { this.radiographerId = v; }
    public void setStatus(AppointmentStatus v)   { this.status = v; }
    public void setCreatedDate(Date v)           { this.createdDate = v; }

    // --- Static factory ---
    public static AppointmentResponseDTO fromEntity(RadiologyAppointment a) {
        return new AppointmentResponseDTO(
                a.getAppointmentId(), a.getRequestId(), a.getAppointmentDate(),
                a.getTimeSlot(), a.getEquipment(), a.getRadiographerId(),
                a.getStatus(), a.getCreatedDate());
    }
}
