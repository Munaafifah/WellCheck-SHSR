package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * In-app notification record stored in MongoDB.
 * Also used as audit log for notification dispatch events.
 * Collection: SchedulingNotification
 *
 * UCR012 — created automatically by SchedulingNotificationService
 */
@Document(collection = "SchedulingNotification")
@TypeAlias("com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.Notification")
public class Notification {

    @Id
    private String             notificationId;

    @Indexed
    private String             userId;

    private String             message;
    private NotificationChannel channel;
    private Date               timestamp;
    private Boolean            isRead;

    public Notification() {}

    public Notification(String notificationId, String userId, String message,
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
    public void setNotificationId(String notificationId)     { this.notificationId = notificationId; }
    public void setUserId(String userId)                     { this.userId = userId; }
    public void setMessage(String message)                   { this.message = message; }
    public void setChannel(NotificationChannel channel)      { this.channel = channel; }
    public void setTimestamp(Date timestamp)                 { this.timestamp = timestamp; }
    public void setIsRead(Boolean isRead)                    { this.isRead = isRead; }

    // --- Builder ---
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String             notificationId;
        private String             userId;
        private String             message;
        private NotificationChannel channel;
        private Date               timestamp;
        private Boolean            isRead;

        public Builder notificationId(String v)           { this.notificationId = v; return this; }
        public Builder userId(String v)                   { this.userId = v;         return this; }
        public Builder message(String v)                  { this.message = v;        return this; }
        public Builder channel(NotificationChannel v)     { this.channel = v;        return this; }
        public Builder timestamp(Date v)                  { this.timestamp = v;      return this; }
        public Builder isRead(Boolean v)                  { this.isRead = v;         return this; }

        public Notification build() {
            return new Notification(notificationId, userId, message, channel, timestamp, isRead);
        }
    }
}
