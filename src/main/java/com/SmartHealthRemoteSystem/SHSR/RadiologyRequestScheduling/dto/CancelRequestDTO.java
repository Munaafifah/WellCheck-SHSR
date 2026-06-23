package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto;

import javax.validation.constraints.NotBlank;

/**
 * Request body for UCR014 — Cancel an ImagingRequest.
 * Cancellation reason is mandatory per the new spec (Radiologist / Radiographer actor).
 */
public class CancelRequestDTO {

    @NotBlank(message = "Cancellation reason is required.")
    private String cancellationReason;

    public CancelRequestDTO() {}

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String v) { this.cancellationReason = v; }
}
