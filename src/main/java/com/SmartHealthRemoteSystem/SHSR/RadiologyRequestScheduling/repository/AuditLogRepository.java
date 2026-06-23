package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the system audit trail.
 */
@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {

    List<AuditLog> findByEntityIdOrderByPerformedAtDesc(String entityId);

    List<AuditLog> findByEntityTypeOrderByPerformedAtDesc(String entityType);

    List<AuditLog> findByPerformedByOrderByPerformedAtDesc(String performedBy);
}
