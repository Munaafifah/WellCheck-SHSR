package com.SmartHealthRemoteSystem.SHSR.Sensor.Repository;

import com.SmartHealthRemoteSystem.SHSR.Sensor.Model.SensorRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRequestRepository extends MongoRepository<SensorRequest, String> {

    // Find all requests by status (e.g. "PENDING", "APPROVED", "REJECTED")
    List<SensorRequest> findByStatus(String status);

    // Find request by patientId (to check if patient already submitted a request)
    Optional<SensorRequest> findByPatientId(String patientId);

    // Find requests by patientId and status
    Optional<SensorRequest> findByPatientIdAndStatus(String patientId, String status);
}