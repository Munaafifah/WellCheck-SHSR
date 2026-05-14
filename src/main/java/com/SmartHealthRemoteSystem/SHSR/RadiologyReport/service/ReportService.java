package com.SmartHealthRemoteSystem.SHSR.RadiologyReport.service;

import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.CreateReportRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.ReportResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.UpdateReportStatusRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.exception.ReportNotFoundException;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.model.Report;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.repository.ReportRepository;
import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
import com.SmartHealthRemoteSystem.SHSR.User.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private static final String STATUS_DRAFT     = "Draft";
    private static final String STATUS_FINALIZED = "Finalized";

    private final ReportRepository reportRepository;
    private final UserRepository   userRepository;
    private final MailService      mailService;

    @Autowired
    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         MailService mailService) {
        this.reportRepository = reportRepository;
        this.userRepository   = userRepository;
        this.mailService      = mailService;
    }

    // UCR016 — Create a new report with default status Draft
    public ReportResponseDTO createReport(CreateReportRequest request) {
        if (request.getRequestId() == null || request.getRequestId().isBlank()) {
            throw new IllegalArgumentException("requestId must not be blank.");
        }
        if (request.getRadiologistId() == null || request.getRadiologistId().isBlank()) {
            throw new IllegalArgumentException("radiologistId must not be blank.");
        }

        Report report = Report.builder()
                .reportId(UUID.randomUUID().toString())
                .requestId(request.getRequestId())
                .radiologistId(request.getRadiologistId())
                .findings(request.getFindings())
                .impression(request.getImpression())
                .status(STATUS_DRAFT)
                .createdDate(new Date())
                .build();

        return ReportResponseDTO.fromEntity(reportRepository.save(report));
    }

    // UCR017 — Retrieve a single report by its ID
    public ReportResponseDTO getReportById(String reportId) {
        return ReportResponseDTO.fromEntity(findOrThrow(reportId));
    }

    // UCR017 — Retrieve all reports linked to an imaging request
    public List<ReportResponseDTO> getReportsByRequestId(String requestId) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId must not be blank.");
        }
        return reportRepository.findByRequestId(requestId).stream()
                .map(ReportResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // UCR018 — Update report status; triggers alert when transitioning to Finalized
    public ReportResponseDTO updateReportStatus(String reportId, UpdateReportStatusRequest request) {
        String newStatus = request.getStatus();
        if (newStatus == null || (!newStatus.equals(STATUS_DRAFT) && !newStatus.equals(STATUS_FINALIZED))) {
            throw new IllegalArgumentException("status must be 'Draft' or 'Finalized'.");
        }

        Report report = findOrThrow(reportId);

        if (STATUS_FINALIZED.equals(report.getStatus()) && STATUS_FINALIZED.equals(newStatus)) {
            throw new IllegalStateException("Report is already Finalized.");
        }

        report.setStatus(newStatus);
        Report saved = reportRepository.save(report);

        // UCR019 — Send notification alert when report is finalized
        if (STATUS_FINALIZED.equals(newStatus)) {
            sendFinalizationAlert(saved);
        }

        return ReportResponseDTO.fromEntity(saved);
    }

    // UCR020 — Resolve report content for download; caller renders as text/plain attachment
    public Report resolveReportForDownload(String reportId) {
        return findOrThrow(reportId);
    }

    // UCR018 — Retrieve all reports by radiologist
    public List<ReportResponseDTO> getReportsByRadiologistId(String radiologistId) {
        if (radiologistId == null || radiologistId.isBlank()) {
            throw new IllegalArgumentException("radiologistId must not be blank.");
        }
        return reportRepository.findByRadiologistId(radiologistId).stream()
                .map(ReportResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // UCR018 — Retrieve all reports with a given status
    public List<ReportResponseDTO> getReportsByStatus(String status) {
        if (status == null || (!status.equals(STATUS_DRAFT) && !status.equals(STATUS_FINALIZED))) {
            throw new IllegalArgumentException("status must be 'Draft' or 'Finalized'.");
        }
        return reportRepository.findByStatus(status).stream()
                .map(ReportResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // --- Private helpers ---

    private Report findOrThrow(String reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(
                        "Report not found: " + reportId));
    }

    // UCR019 — Notify radiologist and relevant parties on finalization
    private void sendFinalizationAlert(Report report) {
        com.SmartHealthRemoteSystem.SHSR.User.User radiologist =
                userRepository.get(report.getRadiologistId());

        if (radiologist == null || radiologist.getEmail() == null) {
            logger.warn("Cannot send finalization alert: radiologist {} not found or has no email.",
                    report.getRadiologistId());
            return;
        }

        String subject = "Radiology Report Finalized — Request #" + report.getRequestId();
        String body = String.format(
                "Dear %s,%n%n" +
                "Your radiology report (ID: %s) for imaging request %s has been marked as Finalized.%n%n" +
                "Findings:%n%s%n%n" +
                "Impression:%n%s%n%n" +
                "Best regards,%nWellCheck System",
                radiologist.getName(),
                report.getReportId(),
                report.getRequestId(),
                report.getFindings()   != null ? report.getFindings()   : "N/A",
                report.getImpression() != null ? report.getImpression() : "N/A"
        );
        try {
            mailService.sendMail(radiologist.getEmail(), subject, body);
            logger.info("Finalization alert sent for report {} to {}",
                    report.getReportId(), radiologist.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send finalization alert for report {}: {}",
                    report.getReportId(), e.getMessage());
        }
    }
}
