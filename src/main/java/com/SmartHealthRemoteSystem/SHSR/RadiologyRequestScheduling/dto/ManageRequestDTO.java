package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestStatus;

import javax.validation.constraints.NotNull;

/**
 * Request body DTO for UCR009 — Manage (approve / reject) an ImagingRequest.
 * Used by Radiologist / Radiographer staff.
 * rejectionReason is required only when status == REJECTED.
 */
public class ManageRequestDTO {

    @NotNull(message = "status must not be null")
    private RequestStatus status;

    // Mandatory when status = REJECTED; optional otherwise
    private String rejectionReason;

    // --- Getters ---
    public RequestStatus getStatus()          { return status; }
    public String        getRejectionReason() { return rejectionReason; }

    // --- Setters ---
    public void setStatus(RequestStatus status)       { this.status = status; }
    public void setRejectionReason(String reason)     { this.rejectionReason = reason; }
}
