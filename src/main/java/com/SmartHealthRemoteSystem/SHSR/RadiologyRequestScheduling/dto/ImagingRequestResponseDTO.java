package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.ImagingRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.Modality;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.UrgencyLevel;

import java.util.Date;

/**
 * Read-only response DTO for ImagingRequest.
 * Exposed by REST endpoints and Thymeleaf model attributes.
 */
public class ImagingRequestResponseDTO {

    private String        requestId;
    private String        patientId;
    private String        doctorId;
    private Modality      modality;
    private String        bodyPart;
    private UrgencyLevel  urgencyLevel;
    private String        clinicalNotes;
    private String        referringPhysician;
    private Integer       priorityScore;
    private RequestStatus status;
    private String        rejectionReason;
    private String        cancellationReason;
    private String        radiologistId;
    private Date          createdDate;
    private Date          updatedDate;

    public ImagingRequestResponseDTO() {}

    public ImagingRequestResponseDTO(String requestId, String patientId, String doctorId,
                                     Modality modality, String bodyPart,
                                     UrgencyLevel urgencyLevel, String clinicalNotes,
                                     String referringPhysician, Integer priorityScore,
                                     RequestStatus status, String rejectionReason,
                                     String cancellationReason, String radiologistId,
                                     Date createdDate, Date updatedDate) {
        this.requestId          = requestId;
        this.patientId          = patientId;
        this.doctorId           = doctorId;
        this.modality           = modality;
        this.bodyPart           = bodyPart;
        this.urgencyLevel       = urgencyLevel;
        this.clinicalNotes      = clinicalNotes;
        this.referringPhysician = referringPhysician;
        this.priorityScore      = priorityScore;
        this.status             = status;
        this.rejectionReason    = rejectionReason;
        this.cancellationReason = cancellationReason;
        this.radiologistId      = radiologistId;
        this.createdDate        = createdDate;
        this.updatedDate        = updatedDate;
    }

    // --- Getters ---
    public String        getRequestId()           { return requestId; }
    public String        getPatientId()           { return patientId; }
    public String        getDoctorId()            { return doctorId; }
    public Modality      getModality()            { return modality; }
    public String        getBodyPart()            { return bodyPart; }
    public UrgencyLevel  getUrgencyLevel()        { return urgencyLevel; }
    public String        getClinicalNotes()       { return clinicalNotes; }
    public String        getReferringPhysician()  { return referringPhysician; }
    public Integer       getPriorityScore()       { return priorityScore; }
    public RequestStatus getStatus()              { return status; }
    public String        getRejectionReason()     { return rejectionReason; }
    public String        getCancellationReason()  { return cancellationReason; }
    public String        getRadiologistId()       { return radiologistId; }
    public Date          getCreatedDate()         { return createdDate; }
    public Date          getUpdatedDate()         { return updatedDate; }

    // --- Setters ---
    public void setRequestId(String v)            { this.requestId = v; }
    public void setPatientId(String v)            { this.patientId = v; }
    public void setDoctorId(String v)             { this.doctorId = v; }
    public void setModality(Modality v)           { this.modality = v; }
    public void setBodyPart(String v)             { this.bodyPart = v; }
    public void setUrgencyLevel(UrgencyLevel v)   { this.urgencyLevel = v; }
    public void setClinicalNotes(String v)        { this.clinicalNotes = v; }
    public void setReferringPhysician(String v)   { this.referringPhysician = v; }
    public void setPriorityScore(Integer v)       { this.priorityScore = v; }
    public void setStatus(RequestStatus v)        { this.status = v; }
    public void setRejectionReason(String v)      { this.rejectionReason = v; }
    public void setCancellationReason(String v)   { this.cancellationReason = v; }
    public void setRadiologistId(String v)        { this.radiologistId = v; }
    public void setCreatedDate(Date v)            { this.createdDate = v; }
    public void setUpdatedDate(Date v)            { this.updatedDate = v; }

    // --- Static factory from entity ---
    public static ImagingRequestResponseDTO fromEntity(ImagingRequest r) {
        return new ImagingRequestResponseDTO(
                r.getRequestId(), r.getPatientId(), r.getDoctorId(),
                r.getModality(), r.getBodyPart(), r.getUrgencyLevel(),
                r.getClinicalNotes(), r.getReferringPhysician(),
                r.getPriorityScore(), r.getStatus(), r.getRejectionReason(),
                r.getCancellationReason(), r.getRadiologistId(),
                r.getCreatedDate(), r.getUpdatedDate());
    }
}
