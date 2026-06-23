package com.SmartHealthRemoteSystem.SHSR.RadiologyReport.service;

import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.CreateReportRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.ReportResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.UpdateReportStatusRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.exception.ReportNotFoundException;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.model.Report;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.repository.ReportRepository;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.ImagingRequestResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.exception.ImagingRequestNotFoundException;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service.ImagingRequestService;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service.SchedulingNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private static final String STATUS_DRAFT     = "Draft";
    private static final String STATUS_FINALIZED = "Finalized";

    // UCR016 — short, human-friendly report IDs (5-6 chars, alphanumeric, no prefix)
    private static final String ID_ALPHABET   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int    ID_LENGTH     = 6;
    private static final int    ID_MAX_RETRIES = 10;
    private static final Random ID_RANDOM    = new Random();

    private final ReportRepository reportRepository;
    private final ImagingRequestService imagingRequestService;
    private final SchedulingNotificationService notificationService;

    @Autowired
    public ReportService(ReportRepository reportRepository,
                         ImagingRequestService imagingRequestService,
                         SchedulingNotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.imagingRequestService = imagingRequestService;
        this.notificationService = notificationService;
    }

    // UCR016 — Create a new report with default status Draft
    public ReportResponseDTO createReport(CreateReportRequest request) {
        if (request.getRequestId() == null || request.getRequestId().isBlank()) {
            throw new IllegalArgumentException("requestId must not be blank.");
        }
        if (request.getRadiologistId() == null || request.getRadiologistId().isBlank()) {
            throw new IllegalArgumentException("radiologistId must not be blank.");
        }

        ImagingRequestResponseDTO imagingRequest;
        try {
            imagingRequest = imagingRequestService.getRequest(request.getRequestId());
        } catch (ImagingRequestNotFoundException e) {
            throw new IllegalArgumentException("No imaging request found with ID: " + request.getRequestId());
        }

        if (imagingRequest.getStatus() != RequestStatus.REPORT_PENDING) {
            throw new IllegalStateException(
                    "Cannot create a report — imaging request " + request.getRequestId()
                    + " is not ready for reporting (current status: " + imagingRequest.getStatus()
                    + "). Imaging must be completed and images uploaded first.");
        }

        Report report = Report.builder()
                .reportId(generateUniqueReportId())
                .requestId(request.getRequestId())
                .radiologistId(request.getRadiologistId())
                .doctorId(request.getDoctorId())
                .findings(request.getFindings())
                .impression(request.getImpression())
                .status(STATUS_DRAFT)
                .createdDate(new Date())
                .build();

        return ReportResponseDTO.fromEntity(reportRepository.save(report));
    }

    // UCR016 — Generates a short (6-char) alphanumeric report ID and retries
    // on the rare chance of a collision with an existing report.
    private String generateUniqueReportId() {
        for (int attempt = 0; attempt < ID_MAX_RETRIES; attempt++) {
            String candidate = randomAlphanumeric(ID_LENGTH);
            if (!reportRepository.existsById(candidate)) {
                return candidate;
            }
            logger.warn("Report ID collision on candidate {} (attempt {}); retrying.", candidate, attempt + 1);
        }
        throw new IllegalStateException(
                "Failed to generate a unique report ID after " + ID_MAX_RETRIES + " attempts.");
    }

    private String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ID_ALPHABET.charAt(ID_RANDOM.nextInt(ID_ALPHABET.length())));
        }
        return sb.toString();
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

        // UCR012/UCR019 — notify doctor and radiologist when report is finalized
        if (STATUS_FINALIZED.equals(newStatus)) {
            // Resolve the doctor via the imaging request rather than trusting
            // Report.doctorId alone — older reports (created before that field
            // was wired up) or callers that don't pass doctorId would otherwise
            // silently fail to notify anyone.
            String doctorId = null;
            try {
                doctorId = imagingRequestService.getRequest(saved.getRequestId()).getDoctorId();
            } catch (Exception e) {
                logger.warn("Could not resolve doctorId for request {} when finalizing report {}: {}",
                        saved.getRequestId(), saved.getReportId(), e.getMessage());
            }

            // Advance the linked imaging request to REPORT_READY so its status
            // no longer sits at REPORT_PENDING forever after the report is done.
            // Best-effort: if the request has already moved on for some other
            // reason (e.g. a second/addendum report on the same request), don't
            // let that block the report from finalizing — just log it.
            try {
                imagingRequestService.markReportReady(saved.getRequestId());
            } catch (Exception e) {
                logger.warn("Could not advance imaging request {} to REPORT_READY after finalizing report {}: {}",
                        saved.getRequestId(), saved.getReportId(), e.getMessage());
            }

            notificationService.notifyReportFinalized(
                    doctorId, saved.getRadiologistId(), saved.getRequestId(), saved.getReportId());
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

    // One-time data fix — finds every Finalized report whose linked ImagingRequest
    // is still stuck at REPORT_PENDING (i.e. reports that were finalized before
    // updateReportStatus() started calling markReportReady()) and advances the
    // request to REPORT_READY. Safe to call more than once: requests that are
    // already correct are simply skipped, not re-processed.
    public Map<String, Object> backfillReportReadyStatus() {
        List<Report> finalizedReports = reportRepository.findByStatus(STATUS_FINALIZED);

        List<String> updated = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        List<String> failed  = new ArrayList<>();

        for (Report report : finalizedReports) {
            String label = report.getReportId() + " (request " + report.getRequestId() + ")";
            try {
                ImagingRequestResponseDTO imagingRequest =
                        imagingRequestService.getRequest(report.getRequestId());

                if (imagingRequest.getStatus() == RequestStatus.REPORT_PENDING) {
                    imagingRequestService.markReportReady(report.getRequestId());
                    updated.add(label);
                } else {
                    skipped.add(label + " — already " + imagingRequest.getStatus());
                }
            } catch (Exception e) {
                failed.add(label + " — " + e.getMessage());
                logger.warn("Backfill failed for report {} (request {}): {}",
                        report.getReportId(), report.getRequestId(), e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalFinalizedReports", finalizedReports.size());
        result.put("updatedCount", updated.size());
        result.put("updated", updated);
        result.put("skippedCount", skipped.size());
        result.put("skipped", skipped);
        result.put("failedCount", failed.size());
        result.put("failed", failed);
        return result;
    }

    // --- Private helpers ---

    private Report findOrThrow(String reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(
                        "Report not found: " + reportId));
    }
}