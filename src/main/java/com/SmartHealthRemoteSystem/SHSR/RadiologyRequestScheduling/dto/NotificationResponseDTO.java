package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.Notification;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.NotificationChannel;

import java.util.Date;

/**
 * Read-only response DTO for Notification.
 * Used by the notification center page and REST endpoint.
 */
public class NotificationResponseDTO {

    private String             notificationId;
    private String             userId;
    private String             message;
    private NotificationChannel channel;
    private Date               timestamp;
    private Boolean            isRead;

    public NotificationResponseDTO() {}

    public NotificationResponseDTO(String notificationId, String userId, String message,
                                   NotificationChannel channel, Date timestamp, Boolean isRead) {
        this.notificationId = notificationId;
        this.userId         = userId;
        this.message        = message;
        this.channel        = channel;
        this.timestamp      = timestamp;
        this.isRead         = isRead;
    }

    // --- Getters ---
    public String              getNotificationId() { return notificationId; }
    public String              getUserId()         { return userId; }
    public String              getMessage()        { return message; }
    public NotificationChannel getChannel()        { return channel; }
    public Date                getTimestamp()      { return timestamp; }
    public Boolean             getIsRead()         { return isRead; }

    // --- Setters ---
    public void setNotificationId(String v)           { this.notificationId = v; }
    public void setUserId(String v)                   { this.userId = v; }
    public void setMessage(String v)                  { this.message = v; }
    public void setChannel(NotificationChannel v)     { this.channel = v; }
    public void setTimestamp(Date v)                  { this.timestamp = v; }
    public void setIsRead(Boolean v)                  { this.isRead = v; }

    // --- Static factory ---
    public static NotificationResponseDTO fromEntity(Notification n) {
        return new NotificationResponseDTO(
                n.getNotificationId(), n.getUserId(), n.getMessage(),
                n.getChannel(), n.getTimestamp(), n.getIsRead());
    }
}
