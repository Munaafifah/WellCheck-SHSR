package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.ImagingRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.UrgencyLevel;

/**
 * Spring Data MongoDB repository for ImagingRequest documents.
 * All derived queries resolve against the RadiologyRequest collection.
 */
@Repository
public interface ImagingRequestRepository extends MongoRepository<ImagingRequest, String> {

    // UCR009 — Staff fetches requests by workflow status
    List<ImagingRequest> findByStatus(RequestStatus status);

    // UCR008/UCR013 — Doctor views own submitted requests
    List<ImagingRequest> findByDoctorId(String doctorId);

    // UCR013 — Patient tracks own requests
    List<ImagingRequest> findByPatientId(String patientId);

    // UCR011 — AI prioritization: all requests above a priority threshold
    List<ImagingRequest> findByPriorityScoreGreaterThan(int threshold);

    // UCR011 — AI prioritization: fetch all by urgency level
    List<ImagingRequest> findByUrgencyLevel(UrgencyLevel urgencyLevel);

    // UCR009 — Staff filters by doctor and status
    List<ImagingRequest> findByDoctorIdAndStatus(String doctorId, RequestStatus status);

    // UCR013 — Patient filters by patient and status
    List<ImagingRequest> findByPatientIdAndStatus(String patientId, RequestStatus status);

    // UCR009 — Staff sorts by priority descending (highest priority first)
    List<ImagingRequest> findByStatusOrderByPriorityScoreDesc(RequestStatus status);

    // Count helpers for dashboard metrics
    // Count helpers for dashboard metrics
long countByStatus(RequestStatus status);

// Find by human-readable requestId field (not MongoDB _id)
java.util.Optional<ImagingRequest> findByRequestId(String requestId);
}
