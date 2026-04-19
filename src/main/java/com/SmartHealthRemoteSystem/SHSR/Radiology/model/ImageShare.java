package com.SmartHealthRemoteSystem.SHSR.Radiology.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "ImageShares")
@TypeAlias("com.SmartHealthRemoteSystem.SHSR.Radiology.ImageShare")
public class ImageShare {

    @Id
    private String id;
    private String imageId;
    private String sharedBy;
    private String accessToken;
    private Date expiresAt;

    public ImageShare() {}

    public ImageShare(String id, String imageId, String sharedBy,
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

        public ImageShare build() {
            return new ImageShare(id, imageId, sharedBy, accessToken, expiresAt);
        }
    }
}
