package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.controller;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.NotificationResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.Notification;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.NotificationRepository;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for notification read-state management.
 * UCR012 — mark individual or all notifications as read.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get all notifications for the current user
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications() {
        String userId = currentUserId();
        List<NotificationResponseDTO> list =
                notificationRepository.findByUserIdOrderByTimestampDesc(userId).stream()
                        .map(NotificationResponseDTO::fromEntity)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // Mark a single notification as read
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markRead(@PathVariable String notificationId) {
        String userId = currentUserId();
        Optional<Notification> opt = notificationRepository.findById(notificationId);
        if (opt.isPresent() && opt.get().getUserId().equals(userId)) {
            Notification n = opt.get();
            n.setIsRead(true);
            notificationRepository.save(n);
        }
        return ResponseEntity.ok().build();
    }

    // Mark all notifications as read for the current user
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        String userId = currentUserId();
        List<Notification> unread =
                notificationRepository.findByUserIdAndIsReadFalseOrderByTimestampDesc(userId);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
        return ResponseEntity.ok().build();
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((MyUserDetails) auth.getPrincipal()).getUser().getUserId();
    }
}
