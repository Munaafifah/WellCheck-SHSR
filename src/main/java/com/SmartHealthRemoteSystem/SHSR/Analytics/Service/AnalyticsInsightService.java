package com.SmartHealthRemoteSystem.SHSR.Analytics.Service;

import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.DashboardStatsDTO;
import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.EmergencyAnalyticsDTO;
import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.InsightDTO;
import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.InsightDTO.InsightType;
import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.TurnaroundDTO;
import com.SmartHealthRemoteSystem.SHSR.Analytics.Repository.AnalyticsAggregationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * UCR030 — Auto-generates performance insights and recommendations
 * by comparing current metrics against previous-period benchmarks.
 */
@Service
public class AnalyticsInsightService {

    private final AnalyticsService          analyticsService;
    private final AnalyticsAggregationRepository aggRepo;

    @Autowired
    public AnalyticsInsightService(AnalyticsService analyticsService,
                                   AnalyticsAggregationRepository aggRepo) {
        this.analyticsService = analyticsService;
        this.aggRepo          = aggRepo;
    }

    public List<InsightDTO> generateInsights() {
        List<InsightDTO> insights = new ArrayList<>();

        DashboardStatsDTO stats     = analyticsService.getDashboardStats();
        TurnaroundDTO     tat       = analyticsService.getTurnaroundMetrics();
        EmergencyAnalyticsDTO emg   = analyticsService.getEmergencyAnalytics();
        Map<String, Object> modUtil = analyticsService.getModalityUtilization("monthly");

        // ── Volume growth ─────────────────────────────────────────────────────
        double growth = stats.getRequestGrowthPercent();
        if (growth >= 10) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.SUCCESS)
                    .icon("fa-chart-line")
                    .title("Imaging Volume Growing")
                    .message(String.format("Imaging requests increased by %.1f%% compared to last month.", growth))
                    .metric(String.format("+%.1f%%", growth))
                    .recommendation("Consider expanding equipment capacity to sustain growth.")
                    .build());
        } else if (growth < 0) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.WARNING)
                    .icon("fa-chart-line")
                    .title("Imaging Volume Declined")
                    .message(String.format("Imaging requests decreased by %.1f%% compared to last month.", Math.abs(growth)))
                    .metric(String.format("%.1f%%", growth))
                    .recommendation("Investigate causes such as referral drop-off or scheduling delays.")
                    .build());
        } else {
            insights.add(InsightDTO.builder()
                    .type(InsightType.INFO)
                    .icon("fa-chart-bar")
                    .title("Stable Imaging Volume")
                    .message("Imaging volume is consistent with the previous period.")
                    .metric(String.format("+%.1f%%", growth))
                    .recommendation("Maintain current scheduling capacity.")
                    .build());
        }

        // ── Appointment turnaround ────────────────────────────────────────────
        double apptTat = tat.getAvgAppointmentTurnaroundDays();
        if (apptTat > 5) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.CRITICAL)
                    .icon("fa-clock")
                    .title("High Appointment Turnaround")
                    .message(String.format("Average appointment turnaround is %.1f days — above the 5-day target.", apptTat))
                    .metric(String.format("%.1f days", apptTat))
                    .recommendation("Review scheduling bottlenecks and radiographer capacity.")
                    .build());
        } else if (apptTat > 0) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.SUCCESS)
                    .icon("fa-clock")
                    .title("Appointment Turnaround On Track")
                    .message(String.format("Average appointment turnaround is %.1f days — within acceptable range.", apptTat))
                    .metric(String.format("%.1f days", apptTat))
                    .recommendation("Continue monitoring scheduling efficiency.")
                    .build());
        }

        // ── Report turnaround ─────────────────────────────────────────────────
        double reportTat = tat.getAvgReportTurnaroundDays();
        if (reportTat > 3) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.WARNING)
                    .icon("fa-file-medical")
                    .title("Report Turnaround Needs Attention")
                    .message(String.format("Average report turnaround is %.1f days. Target is under 3 days.", reportTat))
                    .metric(String.format("%.1f days", reportTat))
                    .recommendation("Encourage radiologists to prioritize report completion.")
                    .build());
        } else if (reportTat > 0) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.SUCCESS)
                    .icon("fa-file-medical")
                    .title("Report Turnaround Excellent")
                    .message(String.format("Reports are being finalized within %.1f days on average.", reportTat))
                    .metric(String.format("%.1f days", reportTat))
                    .recommendation("Maintain current reporting workflow.")
                    .build());
        }

        // ── Emergency handling ────────────────────────────────────────────────
        long totalEmg   = emg.getTotalEmergencyRequests();
        long pendingEmg = emg.getPendingEmergencyCases();
        if (totalEmg > 0 && (double) pendingEmg / totalEmg > 0.3) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.CRITICAL)
                    .icon("fa-triangle-exclamation")
                    .title("High Pending Emergency Cases")
                    .message(String.format("%d of %d emergency cases are still pending (%.0f%%).",
                            pendingEmg, totalEmg, (double) pendingEmg / totalEmg * 100))
                    .metric(pendingEmg + " pending")
                    .recommendation("Immediately escalate unresolved emergency imaging requests.")
                    .build());
        } else if (totalEmg > 0) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.SUCCESS)
                    .icon("fa-triangle-exclamation")
                    .title("Emergency Response Performing Well")
                    .message(String.format("%.0f%% of emergency cases have been completed.",
                            (double) emg.getCompletedEmergencyCases() / totalEmg * 100))
                    .metric(String.format("%.0f%% resolved", (double) emg.getCompletedEmergencyCases() / totalEmg * 100))
                    .recommendation("Sustain rapid triage protocols for emergency imaging.")
                    .build());
        }

        // ── Pending reports workload ──────────────────────────────────────────
        long pendingReports = stats.getPendingReports();
        if (pendingReports > 20) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.WARNING)
                    .icon("fa-hourglass-half")
                    .title("Pending Report Backlog")
                    .message(String.format("There are %d pending reports awaiting finalization.", pendingReports))
                    .metric(pendingReports + " pending")
                    .recommendation("Redistribute reporting workload or schedule overtime sessions.")
                    .build());
        }

        // ── Modality utilization ──────────────────────────────────────────────
        @SuppressWarnings("unchecked")
        List<Long> modalityCounts = (List<Long>) modUtil.get("counts");
        long modTotal = modalityCounts == null ? 0 :
                modalityCounts.stream().mapToLong(Long::longValue).sum();

        if (modTotal > 0 && modalityCounts != null) {
            List<String> modalityNames = List.of("X-Ray", "CT", "MRI", "Ultrasound");
            for (int i = 0; i < modalityCounts.size(); i++) {
                double pct = (double) modalityCounts.get(i) / modTotal * 100;
                if (pct < 5 && modTotal > 10) {
                    insights.add(InsightDTO.builder()
                            .type(InsightType.INFO)
                            .icon("fa-x-ray")
                            .title(modalityNames.get(i) + " Utilization Low")
                            .message(String.format("%s accounts for only %.1f%% of total imaging volume.",
                                    modalityNames.get(i), pct))
                            .metric(String.format("%.1f%%", pct))
                            .recommendation("Evaluate if " + modalityNames.get(i) + " equipment is underutilized or requests are misdirected.")
                            .build());
                }
            }
        }

        // ── Radiologist coverage ──────────────────────────────────────────────
        long activeRads = stats.getActiveRadiologists();
        if (activeRads == 0) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.CRITICAL)
                    .icon("fa-user-doctor")
                    .title("No Active Radiologists")
                    .message("No radiologist accounts are currently registered in the system.")
                    .metric("0 radiologists")
                    .recommendation("Onboard radiologist staff immediately.")
                    .build());
        } else if (pendingReports > activeRads * 10) {
            insights.add(InsightDTO.builder()
                    .type(InsightType.WARNING)
                    .icon("fa-user-doctor")
                    .title("Radiologist Workload High")
                    .message(String.format("%d pending reports for %d radiologist(s) — over 10 reports each.",
                            pendingReports, activeRads))
                    .metric(String.format("%.0f reports/radiologist", (double) pendingReports / activeRads))
                    .recommendation("Consider adding additional radiologist capacity.")
                    .build());
        }

        return insights;
    }
}
