package com.SmartHealthRemoteSystem.SHSR.RadiologyReport.controller;

import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.CreateReportRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.ReportResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.dto.UpdateReportStatusRequest;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.model.Report;
import com.SmartHealthRemoteSystem.SHSR.RadiologyReport.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // UCR016 — POST /api/reports
    // Create a new radiology report; default status is Draft
    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(
            @Valid @RequestBody CreateReportRequest request) {
        ReportResponseDTO response = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UCR017 — GET /api/reports/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    // UCR017 — GET /api/reports/request/{requestId}
    @GetMapping("/request/{requestId}")
    public ResponseEntity<List<ReportResponseDTO>> getByRequestId(
            @PathVariable String requestId) {
        return ResponseEntity.ok(reportService.getReportsByRequestId(requestId));
    }

    // UCR018 — GET /api/reports/radiologist/{radiologistId}
    @GetMapping("/radiologist/{radiologistId}")
    public ResponseEntity<List<ReportResponseDTO>> getByRadiologist(
            @PathVariable String radiologistId) {
        return ResponseEntity.ok(reportService.getReportsByRadiologistId(radiologistId));
    }

    // UCR018 — GET /api/reports/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReportResponseDTO>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status));
    }

    // UCR018 — PUT /api/reports/{id}/status
    // Triggers finalization alert (UCR019) when status becomes Finalized
    @PutMapping("/{id}/status")
    public ResponseEntity<ReportResponseDTO> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        return ResponseEntity.ok(reportService.updateReportStatus(id, request));
    }

    // UCR020 — GET /api/reports/{id}/download
    // Returns the report as a plain-text attachment
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String id) {
        Report report = reportService.resolveReportForDownload(id);

        String content = buildReportText(report);
        byte[] bytes   = content.getBytes(StandardCharsets.UTF_8);
        String filename = "report-" + report.getReportId() + ".txt";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(bytes);
    }

    // --- Private helpers ---

    private String buildReportText(Report report) {
        return String.format(
                "RADIOLOGY REPORT%n" +
                "=================%n" +
                "Report ID     : %s%n" +
                "Request ID    : %s%n" +
                "Radiologist ID: %s%n" +
                "Status        : %s%n" +
                "Created Date  : %s%n%n" +
                "FINDINGS%n" +
                "--------%n" +
                "%s%n%n" +
                "IMPRESSION%n" +
                "----------%n" +
                "%s%n",
                report.getReportId(),
                report.getRequestId(),
                report.getRadiologistId(),
                report.getStatus(),
                report.getCreatedDate() != null ? report.getCreatedDate().toString() : "N/A",
                report.getFindings()   != null ? report.getFindings()   : "N/A",
                report.getImpression() != null ? report.getImpression() : "N/A"
        );
    }
}
