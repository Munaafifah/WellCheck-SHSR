package com.SmartHealthRemoteSystem.SHSR.Radiology.service;

import com.SmartHealthRemoteSystem.SHSR.Radiology.model.ImageAuditLog;
import com.SmartHealthRemoteSystem.SHSR.Radiology.repository.ImageAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ImageAuditLogService {

    private final ImageAuditLogRepository auditLogRepository;

    @Autowired
    public ImageAuditLogService(ImageAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // UCR007 — Record a deletion event; called internally by MedicalImageService
    public ImageAuditLog logDeletion(String imageId, String deletedBy, String reason) {
        ImageAuditLog log = ImageAuditLog.builder()
                .id(UUID.randomUUID().toString())
                .imageId(imageId)
                .deletedBy(deletedBy)
                .deletedAt(new Date())
                .reason(reason != null ? reason : "No reason provided")
                .build();
        return auditLogRepository.save(log);
    }

    // Retrieve full audit trail for a given image
    public List<ImageAuditLog> getLogsByImageId(String imageId) {
        if (imageId == null || imageId.isBlank()) {
            throw new IllegalArgumentException("imageId must not be blank.");
        }
        return auditLogRepository.findByImageId(imageId);
    }

    // Retrieve all deletions performed by a specific user
    public List<ImageAuditLog> getLogsByDeletedBy(String deletedBy) {
        if (deletedBy == null || deletedBy.isBlank()) {
            throw new IllegalArgumentException("deletedBy must not be blank.");
        }
        return auditLogRepository.findByDeletedBy(deletedBy);
    }
}
