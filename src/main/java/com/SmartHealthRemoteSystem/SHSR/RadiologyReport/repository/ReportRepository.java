package com.SmartHealthRemoteSystem.SHSR.RadiologyReport.repository;

import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {

    // UCR017 — All reports linked to an imaging request
    List<Report> findByRequestId(String requestId);

    // UCR018 — All reports authored by a radiologist
    List<Report> findByRadiologistId(String radiologistId);

    // UCR018 — All reports with a given status
    List<Report> findByStatus(String status);

    // UCR017 — Most recent report for a given request
    Optional<Report> findFirstByRequestIdOrderByCreatedDateDesc(String requestId);
}
