package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.Modality;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.UrgencyLevel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Request body DTO for UCR008 — Submit Imaging Request.
 * Doctor / Clinic Assistant supplies patient, modality, body part,
 * urgency, clinical indication, and referring physician.
 */
public class SubmitImagingRequestDTO {

    @NotBlank(message = "patientId must not be blank")
    private String patientId;

    // submitterId is injected from the authenticated session — not from the request body
    private String doctorId;

    @NotNull(message = "modality must not be null")
    private Modality modality;

   private String bodyPart;

    @NotNull(message = "urgencyLevel must not be null")
    private UrgencyLevel urgencyLevel;

    private String clinicalNotes;

    private String referringPhysician;

    // --- Getters ---
    public String       getPatientId()          { return patientId; }
    public String       getDoctorId()           { return doctorId; }
    public Modality     getModality()           { return modality; }
    public String       getBodyPart()           { return bodyPart; }
    public UrgencyLevel getUrgencyLevel()       { return urgencyLevel; }
    public String       getClinicalNotes()      { return clinicalNotes; }
    public String       getReferringPhysician() { return referringPhysician; }

    // --- Setters ---
    public void setPatientId(String patientId)              { this.patientId = patientId; }
    public void setDoctorId(String doctorId)                { this.doctorId = doctorId; }
    public void setModality(Modality modality)              { this.modality = modality; }
    public void setBodyPart(String bodyPart)                { this.bodyPart = bodyPart; }
    public void setUrgencyLevel(UrgencyLevel level)         { this.urgencyLevel = level; }
    public void setClinicalNotes(String clinicalNotes)      { this.clinicalNotes = clinicalNotes; }
    public void setReferringPhysician(String physician)     { this.referringPhysician = physician; }
}
