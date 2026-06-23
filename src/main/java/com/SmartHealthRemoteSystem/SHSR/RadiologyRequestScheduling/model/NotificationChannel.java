package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

/**
 * Delivery channel used for a Notification record.
 * EMAIL   — sent via JavaMailSender (MailService)
 * SMS     — reserved for future SMS gateway integration
 * SYSTEM  — in-app notification stored in MongoDB only
 */
public enum NotificationChannel {
    EMAIL,
    SMS,
    SYSTEM
}
