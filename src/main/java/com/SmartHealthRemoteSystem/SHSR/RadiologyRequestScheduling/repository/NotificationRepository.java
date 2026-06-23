package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for Notification documents.
 * Supports the in-app notification center (UCR012).
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    // UCR012 — Fetch all notifications for a specific user (notification center)
    List<Notification> findByUserIdOrderByTimestampDesc(String userId);

    // UCR012 — Fetch unread notifications for a user
    List<Notification> findByUserIdAndIsReadFalseOrderByTimestampDesc(String userId);

    // UCR012 — Count unread notifications (badge counter)
    long countByUserIdAndIsReadFalse(String userId);

    // Mark all for a user as read (bulk update via custom logic in service)
    List<Notification> findByUserId(String userId);
}
