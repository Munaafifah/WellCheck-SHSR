package com.SmartHealthRemoteSystem.SHSR.Radiology.dto;

import com.SmartHealthRemoteSystem.SHSR.Radiology.model.ImageShare;

import java.util.Date;

public class ImageShareResponseDTO {

    private String id;
    private String imageId;
    private String sharedBy;
    private String accessToken;
    private Date expiresAt;

    public ImageShareResponseDTO() {}

    public ImageShareResponseDTO(String id, String imageId, String sharedBy,
                                 String accessToken, Date expiresAt) {
        this.id = id;
        this.imageId = imageId;
        this.sharedBy = sharedBy;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }

    // --- Getters ---
    public String getId()           { return id; }
    public String getImageId()      { return imageId; }
    public String getSharedBy()     { return sharedBy; }
    public String getAccessToken()  { return accessToken; }
    public Date getExpiresAt()      { return expiresAt; }

    // --- Setters ---
    public void setId(String id)                    { this.id = id; }
    public void setImageId(String imageId)          { this.imageId = imageId; }
    public void setSharedBy(String sharedBy)        { this.sharedBy = sharedBy; }
    public void setAccessToken(String accessToken)  { this.accessToken = accessToken; }
    public void setExpiresAt(Date expiresAt)        { this.expiresAt = expiresAt; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String imageId;
        private String sharedBy;
        private String accessToken;
        private Date expiresAt;

        public Builder id(String id)                    { this.id = id; return this; }
        public Builder imageId(String imageId)          { this.imageId = imageId; return this; }
        public Builder sharedBy(String sharedBy)        { this.sharedBy = sharedBy; return this; }
        public Builder accessToken(String accessToken)  { this.accessToken = accessToken; return this; }
        public Builder expiresAt(Date expiresAt)        { this.expiresAt = expiresAt; return this; }

        public ImageShareResponseDTO build() {
            return new ImageShareResponseDTO(id, imageId, sharedBy, accessToken, expiresAt);
        }
    }

    public static ImageShareResponseDTO fromEntity(ImageShare share) {
        return ImageShareResponseDTO.builder()
                .id(share.getId())
                .imageId(share.getImageId())
                .sharedBy(share.getSharedBy())
                .accessToken(share.getAccessToken())
                .expiresAt(share.getExpiresAt())
                .build();
    }
}
