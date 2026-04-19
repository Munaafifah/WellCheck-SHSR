package com.SmartHealthRemoteSystem.SHSR.Radiology.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "ImageAuditLogs")
@TypeAlias("com.SmartHealthRemoteSystem.SHSR.Radiology.ImageAuditLog")
public class ImageAuditLog {

    @Id
    private String id;
    private String imageId;
    private String deletedBy;
    private Date deletedAt = new Date();
    private String reason;

    public ImageAuditLog() {}

    public ImageAuditLog(String id, String imageId, String deletedBy,
                         Date deletedAt, String reason) {
        this.id = id;
        this.imageId = imageId;
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
        this.reason = reason;
    }

    // --- Getters ---
    public String getId()        { return id; }
    public String getImageId()   { return imageId; }
    public String getDeletedBy() { return deletedBy; }
    public Date getDeletedAt()   { return deletedAt; }
    public String getReason()    { return reason; }

    // --- Setters ---
    public void setId(String id)               { this.id = id; }
    public void setImageId(String imageId)     { this.imageId = imageId; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }
    public void setDeletedAt(Date deletedAt)   { this.deletedAt = deletedAt; }
    public void setReason(String reason)       { this.reason = reason; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String imageId;
        private String deletedBy;
        private Date deletedAt = new Date();
        private String reason;

        public Builder id(String id)               { this.id = id; return this; }
        public Builder imageId(String imageId)     { this.imageId = imageId; return this; }
        public Builder deletedBy(String deletedBy) { this.deletedBy = deletedBy; return this; }
        public Builder deletedAt(Date deletedAt)   { this.deletedAt = deletedAt; return this; }
        public Builder reason(String reason)       { this.reason = reason; return this; }

        public ImageAuditLog build() {
            return new ImageAuditLog(id, imageId, deletedBy, deletedAt, reason);
        }
    }
}
