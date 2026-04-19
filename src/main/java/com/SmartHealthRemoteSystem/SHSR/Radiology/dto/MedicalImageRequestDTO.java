package com.SmartHealthRemoteSystem.SHSR.Radiology.dto;

import com.SmartHealthRemoteSystem.SHSR.Radiology.model.Modality;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class MedicalImageRequestDTO {

    @NotBlank(message = "patientId must not be blank")
    private String patientId;

    // Optional — links image to a clinical request/referral
    private String requestId;

    @NotBlank(message = "uploadedBy must not be blank")
    private String uploadedBy;

    @NotNull(message = "modality must not be null")
    private Modality modality;

    // Optional — defaults to upload time if not provided
    private Date captureDate;

    public String getPatientId()       { return patientId; }
    public String getRequestId()       { return requestId; }
    public String getUploadedBy()      { return uploadedBy; }
    public Modality getModality()      { return modality; }
    public Date getCaptureDate()       { return captureDate; }

    public void setPatientId(String patientId)       { this.patientId = patientId; }
    public void setRequestId(String requestId)       { this.requestId = requestId; }
    public void setUploadedBy(String uploadedBy)     { this.uploadedBy = uploadedBy; }
    public void setModality(Modality modality)       { this.modality = modality; }
    public void setCaptureDate(Date captureDate)     { this.captureDate = captureDate; }
}
