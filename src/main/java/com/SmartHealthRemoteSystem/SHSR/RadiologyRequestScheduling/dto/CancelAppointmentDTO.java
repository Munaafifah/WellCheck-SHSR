package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto;

import javax.validation.constraints.NotBlank;

/**
 * Request body for UCR015 — Cancel Appointment.
 * Cancellation reason is mandatory per business rule BRQ043.
 */
public class CancelAppointmentDTO {

    @NotBlank(message = "Cancellation reason is required.")
    private String cancellationReason;

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String v) { this.cancellationReason = v; }
}
