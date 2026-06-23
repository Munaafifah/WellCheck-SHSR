package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Request body DTO for UCR010 — Schedule an Appointment.
 * Radiographer fills this form after an ImagingRequest is APPROVED.
 */
public class ScheduleAppointmentDTO {

    @NotBlank(message = "requestId must not be blank")
    private String requestId;

    @NotNull(message = "appointmentDate must not be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    @NotBlank(message = "timeSlot must not be blank")
    private String timeSlot;

    @NotBlank(message = "equipment must not be blank")
    private String equipment;

    @NotBlank(message = "radiographerId must not be blank")
    private String radiographerId;

    // --- Getters ---
    public String getRequestId()      { return requestId; }
    public Date   getAppointmentDate(){ return appointmentDate; }
    public String getTimeSlot()       { return timeSlot; }
    public String getEquipment()      { return equipment; }
    public String getRadiographerId() { return radiographerId; }

    // --- Setters ---
    public void setRequestId(String requestId)          { this.requestId = requestId; }
    public void setAppointmentDate(Date appointmentDate){ this.appointmentDate = appointmentDate; }
    public void setTimeSlot(String timeSlot)            { this.timeSlot = timeSlot; }
    public void setEquipment(String equipment)          { this.equipment = equipment; }
    public void setRadiographerId(String radiographerId){ this.radiographerId = radiographerId; }
}
