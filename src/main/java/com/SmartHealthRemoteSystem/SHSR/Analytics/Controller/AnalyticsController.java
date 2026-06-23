package com.SmartHealthRemoteSystem.SHSR.Analytics.Controller;

import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.*;
import com.SmartHealthRemoteSystem.SHSR.Analytics.Service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UCR026-UCR031 — Analytics Dashboard controller.
 * All routes under /admin/analytics/** are protected by ROLE_ADMIN.
 * The parent pattern /admin/** is already secured in SecurityConfiguration,
 * and @PreAuthorize adds a second layer of defence-in-depth.
 */
@Controller
@RequestMapping("/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    private final AnalyticsService        analyticsService;
    private final AnalyticsInsightService insightService;
    private final ForecastService         forecastService;
    private final ReportGenerationService reportGenerationService;
    private final ObjectMapper            objectMapper;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService,
                               AnalyticsInsightService insightService,
                               ForecastService forecastService,
                               ReportGenerationService reportGenerationService,
                               ObjectMapper objectMapper) {
        this.analyticsService       = analyticsService;
        this.insightService         = insightService;
        this.forecastService        = forecastService;
        this.reportGenerationService = reportGenerationService;
        this.objectMapper           = objectMapper;
    }

    // ── Main dashboard page ───────────────────────────────────────────────────

    @GetMapping
    public String analyticsDashboard(
            @RequestParam(defaultValue = "monthly") String period,
            Model model) {

        DashboardStatsDTO     stats            = analyticsService.getDashboardStats();
        ImagingVolumeDTO      imagingVolume    = analyticsService.getImagingVolume(period);
        TurnaroundDTO         turnaround       = analyticsService.getTurnaroundMetrics();
        EmergencyAnalyticsDTO emergency        = analyticsService.getEmergencyAnalytics();
        List<TrendDTO>        trends           = analyticsService.getTrends(period);
        ForecastDTO           forecast         = forecastService.getForecast();
        List<InsightDTO>      insights         = insightService.generateInsights();
        Map<String, Object>   radiologistPerf  = analyticsService.getRadiologistPerformance();
        Map<String, Object>   radiographerPerf = analyticsService.getRadiographerPerformance();
        Map<String, Object>   demographics     = analyticsService.getPatientDemographics();
        Map<String, Object>   modalityUtil     = analyticsService.getModalityUtilization(period);

        model.addAttribute("stats",            stats);
        model.addAttribute("imagingVolume",    imagingVolume);
        model.addAttribute("turnaround",       turnaround);
        model.addAttribute("emergency",        emergency);
        model.addAttribute("trends",           trends);
        model.addAttribute("forecast",         forecast);
        model.addAttribute("insights",         insights);
        model.addAttribute("radiologistPerf",  radiologistPerf);
        model.addAttribute("radiographerPerf", radiographerPerf);
        model.addAttribute("demographics",     demographics);
        model.addAttribute("modalityUtil",     modalityUtil);
        model.addAttribute("selectedPeriod",   period);

        // Serialise chart data as JSON for Chart.js inline scripts
        model.addAttribute("imagingVolumeJson",    toJson(imagingVolume));
        model.addAttribute("turnaroundJson",       toJson(turnaround));
        model.addAttribute("emergencyJson",        toJson(emergency));
        model.addAttribute("forecastJson",         toJson(forecast));
        model.addAttribute("modalityUtilJson",     toJson(modalityUtil));
        model.addAttribute("demographicsJson",     toJson(demographics));

        return "analytics-dashboard";
    }

    // ── REST API endpoints (used by AJAX period-filter updates) ──────────────

    @GetMapping("/api/stats")
    @ResponseBody
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(analyticsService.getDashboardStats());
    }

    @GetMapping("/api/imaging-volume")
    @ResponseBody
    public ResponseEntity<ImagingVolumeDTO> getImagingVolume(
            @RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(analyticsService.getImagingVolume(period));
    }

    @GetMapping("/api/turnaround")
    @ResponseBody
    public ResponseEntity<TurnaroundDTO> getTurnaround() {
        return ResponseEntity.ok(analyticsService.getTurnaroundMetrics());
    }

    @GetMapping("/api/emergency")
    @ResponseBody
    public ResponseEntity<EmergencyAnalyticsDTO> getEmergency() {
        return ResponseEntity.ok(analyticsService.getEmergencyAnalytics());
    }

    @GetMapping("/api/forecast")
    @ResponseBody
    public ResponseEntity<ForecastDTO> getForecast() {
        return ResponseEntity.ok(forecastService.getForecast());
    }

    @GetMapping("/api/insights")
    @ResponseBody
    public ResponseEntity<List<InsightDTO>> getInsights() {
        return ResponseEntity.ok(insightService.generateInsights());
    }

    @GetMapping("/api/modality-utilization")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getModalityUtilization(
            @RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(analyticsService.getModalityUtilization(period));
    }

    @GetMapping("/api/radiologist-performance")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRadiologistPerformance() {
        return ResponseEntity.ok(analyticsService.getRadiologistPerformance());
    }

    @GetMapping("/api/radiographer-performance")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRadiographerPerformance() {
        return ResponseEntity.ok(analyticsService.getRadiographerPerformance());
    }

    // ── Export endpoints (UCR031) ─────────────────────────────────────────────

    @GetMapping("/export/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> exportPdf() {
        try {
            byte[] pdf = reportGenerationService.generatePdfReport();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"analytics-report.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Throwable t) {
            log.error("[Analytics] PDF export failed: {}", t.getMessage(), t);
            String msg = "PDF export failed: " + t.getClass().getSimpleName() + " - " + t.getMessage();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(msg.getBytes(StandardCharsets.UTF_8));
        }
    }

    @GetMapping("/export/excel")
    @ResponseBody
    public ResponseEntity<byte[]> exportExcel() {
        try {
            byte[] xlsx = reportGenerationService.generateExcelReport();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"analytics-report.xlsx\"")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(xlsx);
        } catch (Throwable t) {
            log.error("[Analytics] Excel export failed: {}", t.getMessage(), t);
            String msg = "Excel export failed: " + t.getClass().getSimpleName() + " - " + t.getMessage();
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(msg.getBytes(StandardCharsets.UTF_8));
        }
    }

    // ── Debug endpoint — remove after confirming roles are correct ────────────

    @GetMapping("/whoami")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> whoami(Authentication auth) {
        Map<String, Object> info = new LinkedHashMap<>();
        if (auth == null || !auth.isAuthenticated()) {
            info.put("authenticated", false);
            return ResponseEntity.status(401).body(info);
        }
        info.put("authenticated", true);
        info.put("principal", auth.getName());
        info.put("authorities", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        info.put("hasRole_ADMIN",
                auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())));
        return ResponseEntity.ok(info);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
