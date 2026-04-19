package com.SmartHealthRemoteSystem.SHSR.Radiology.repository;

import com.SmartHealthRemoteSystem.SHSR.Radiology.model.ImageAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageAuditLogRepository extends MongoRepository<ImageAuditLog, String> {

    // UCR007 — Retrieve all audit entries for a specific image
    List<ImageAuditLog> findByImageId(String imageId);

    // UCR007 — Retrieve all deletions performed by a specific user
    List<ImageAuditLog> findByDeletedBy(String deletedBy);
}
