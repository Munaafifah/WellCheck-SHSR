package com.SmartHealthRemoteSystem.SHSR.Radiology.dto;

import com.SmartHealthRemoteSystem.SHSR.Radiology.model.MedicalImage;
import com.SmartHealthRemoteSystem.SHSR.Radiology.model.Modality;

import java.util.Date;
import java.util.Map;

public class MedicalImageResponseDTO {

    private String id;
    private String requestId;
    private String patientId;
    private String uploadedBy;
    private Modality modality;
    private String fileUrl;
    private Date captureDate;
    private Boolean anonymized;
    private Map<String, Object> metadata;
    private Date createdAt;

    public MedicalImageResponseDTO() {}

    public MedicalImageResponseDTO(String id, String requestId, String patientId, String uploadedBy,
                                   Modality modality, String fileUrl, Date captureDate,
                                   Boolean anonymized, Map<String, Object> metadata, Date createdAt) {
        this.id = id;
        this.requestId = requestId;
        this.patientId = patientId;
        this.uploadedBy = uploadedBy;
        this.modality = modality;
        this.fileUrl = fileUrl;
        this.captureDate = captureDate;
        this.anonymized = anonymized;
        this.metadata = metadata;
        this.createdAt = createdAt;
    }

    // --- Getters ---
    public String getId()                     { return id; }
    public String getRequestId()              { return requestId; }
    public String getPatientId()              { return patientId; }
    public String getUploadedBy()             { return uploadedBy; }
    public Modality getModality()             { return modality; }
    public String getFileUrl()                { return fileUrl; }
    public Date getCaptureDate()              { return captureDate; }
    public Boolean getAnonymized()            { return anonymized; }
    public Map<String, Object> getMetadata()  { return metadata; }
    public Date getCreatedAt()                { return createdAt; }

    // --- Setters ---
    public void setId(String id)                           { this.id = id; }
    public void setRequestId(String requestId)             { this.requestId = requestId; }
    public void setPatientId(String patientId)             { this.patientId = patientId; }
    public void setUploadedBy(String uploadedBy)           { this.uploadedBy = uploadedBy; }
    public void setModality(Modality modality)             { this.modality = modality; }
    public void setFileUrl(String fileUrl)                 { this.fileUrl = fileUrl; }
    public void setCaptureDate(Date captureDate)           { this.captureDate = captureDate; }
    public void setAnonymized(Boolean anonymized)          { this.anonymized = anonymized; }
    public void setMetadata(Map<String, Object> metadata)  { this.metadata = metadata; }
    public void setCreatedAt(Date createdAt)               { this.createdAt = createdAt; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String requestId;
        private String patientId;
        private String uploadedBy;
        private Modality modality;
        private String fileUrl;
        private Date captureDate;
        private Boolean anonymized;
        private Map<String, Object> metadata;
        private Date createdAt;

        public Builder id(String id)                           { this.id = id; return this; }
        public Builder requestId(String requestId)             { this.requestId = requestId; return this; }
        public Builder patientId(String patientId)             { this.patientId = patientId; return this; }
        public Builder uploadedBy(String uploadedBy)           { this.uploadedBy = uploadedBy; return this; }
        public Builder modality(Modality modality)             { this.modality = modality; return this; }
        public Builder fileUrl(String fileUrl)                 { this.fileUrl = fileUrl; return this; }
        public Builder captureDate(Date captureDate)           { this.captureDate = captureDate; return this; }
        public Builder anonymized(Boolean anonymized)          { this.anonymized = anonymized; return this; }
        public Builder metadata(Map<String, Object> metadata)  { this.metadata = metadata; return this; }
        public Builder createdAt(Date createdAt)               { this.createdAt = createdAt; return this; }

        public MedicalImageResponseDTO build() {
            return new MedicalImageResponseDTO(id, requestId, patientId, uploadedBy,
                    modality, fileUrl, captureDate, anonymized, metadata, createdAt);
        }
    }

    public static MedicalImageResponseDTO fromEntity(MedicalImage image) {
        return MedicalImageResponseDTO.builder()
                .id(image.getId())
                .requestId(image.getRequestId())
                .patientId(image.getPatientId())
                .uploadedBy(image.getUploadedBy())
                .modality(image.getModality())
                .fileUrl(image.getFileUrl())
                .captureDate(image.getCaptureDate())
                .anonymized(image.getAnonymized())
                .metadata(image.getMetadata())
                .createdAt(image.getCreatedAt())
                .build();
    }
}
