package com.SmartHealthRemoteSystem.SHSR.Analytics.Service;

import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.*;

// OpenPDF — explicit imports only (wildcard removed to avoid conflict with
// com.lowagie.text.List, com.lowagie.text.Row, com.lowagie.text.Cell)
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

// Apache POI — explicit imports only (wildcard removed to avoid conflict with
// com.lowagie.text.Cell; org.apache.poi.ss.usermodel.Font used by FQN to avoid
// conflict with com.lowagie.text.Font)
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * UCR031 — Generates PDF and Excel analytics reports.
 * PDF: OpenPDF (com.lowagie). Excel: Apache POI (XSSF).
 */
@Service
public class ReportGenerationService {

    private final AnalyticsService        analyticsService;
    private final ForecastService         forecastService;
    private final AnalyticsInsightService insightService;

    @Autowired
    public ReportGenerationService(AnalyticsService analyticsService,
                                   ForecastService forecastService,
                                   AnalyticsInsightService insightService) {
        this.analyticsService = analyticsService;
        this.forecastService  = forecastService;
        this.insightService   = insightService;
    }

    // ── PDF Report ────────────────────────────────────────────────────────────

    public byte[] generatePdfReport() throws Exception {
        DashboardStatsDTO     stats    = analyticsService.getDashboardStats();
        TurnaroundDTO         tat      = analyticsService.getTurnaroundMetrics();
        EmergencyAnalyticsDTO emg      = analyticsService.getEmergencyAnalytics();
        ForecastDTO           forecast = forecastService.getForecast();
        List<InsightDTO>      insights = insightService.generateInsights();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 60, 60);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        // ── Fonts ─────────────────────────────────────────────────────────────
        Font titleFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.decode("#1a1a2e"));
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.decode("#16213e"));
        Font subFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.decode("#0f3460"));
        Font bodyFont   = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Font smallFont  = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);

        // ── Title ─────────────────────────────────────────────────────────────
        Paragraph title = new Paragraph("WellCheck Radiance — Analytics Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        doc.add(title);

        String dateStr = new SimpleDateFormat("dd MMMM yyyy, HH:mm").format(new Date());
        Paragraph subtitle = new Paragraph("Generated: " + dateStr, smallFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        doc.add(subtitle);

        addSpacer(doc);

        // ── KPI Summary ───────────────────────────────────────────────────────
        doc.add(new Paragraph("KPI Summary", headerFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable kpiTable = newTable(2);
        addKpiRow(kpiTable, "Total Imaging Requests",
                String.valueOf(stats.getTotalImagingRequests()), subFont, bodyFont);
        addKpiRow(kpiTable, "Average Turnaround (days)",
                String.format("%.1f", stats.getAvgTurnaroundDays()), subFont, bodyFont);
        addKpiRow(kpiTable, "Total Emergency Cases",
                String.valueOf(stats.getTotalEmergencyCases()), subFont, bodyFont);
        addKpiRow(kpiTable, "Avg Emergency Response (min)",
                String.format("%.1f", stats.getAvgEmergencyResponseMinutes()), subFont, bodyFont);
        addKpiRow(kpiTable, "Active Radiologists",
                String.valueOf(stats.getActiveRadiologists()), subFont, bodyFont);
        addKpiRow(kpiTable, "Pending Reports",
                String.valueOf(stats.getPendingReports()), subFont, bodyFont);
        addKpiRow(kpiTable, "Request Growth (vs last month)",
                String.format("%.1f%%", stats.getRequestGrowthPercent()), subFont, bodyFont);
        doc.add(kpiTable);

        // ── Turnaround Statistics ─────────────────────────────────────────────
        doc.add(new Paragraph("Turnaround Statistics", headerFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable tatTable = newTable(2);
        addKpiRow(tatTable, "Avg Appointment Turnaround",
                String.format("%.1f days", tat.getAvgAppointmentTurnaroundDays()), subFont, bodyFont);
        addKpiRow(tatTable, "Fastest Appointment Turnaround",
                String.format("%.1f days", tat.getFastestAppointmentTurnaroundDays()), subFont, bodyFont);
        addKpiRow(tatTable, "Slowest Appointment Turnaround",
                String.format("%.1f days", tat.getSlowestAppointmentTurnaroundDays()), subFont, bodyFont);
        addKpiRow(tatTable, "Avg Report Turnaround",
                String.format("%.1f days", tat.getAvgReportTurnaroundDays()), subFont, bodyFont);
        addKpiRow(tatTable, "Fastest Report Turnaround",
                String.format("%.1f days", tat.getFastestReportTurnaroundDays()), subFont, bodyFont);
        addKpiRow(tatTable, "Slowest Report Turnaround",
                String.format("%.1f days", tat.getSlowestReportTurnaroundDays()), subFont, bodyFont);
        doc.add(tatTable);

        // ── Emergency Analytics ───────────────────────────────────────────────
        doc.add(new Paragraph("Emergency Case Analysis", headerFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable emgTable = newTable(2);
        addKpiRow(emgTable, "Total Emergency Requests",
                String.valueOf(emg.getTotalEmergencyRequests()), subFont, bodyFont);
        addKpiRow(emgTable, "Completed",
                String.valueOf(emg.getCompletedEmergencyCases()), subFont, bodyFont);
        addKpiRow(emgTable, "Pending",
                String.valueOf(emg.getPendingEmergencyCases()), subFont, bodyFont);
        addKpiRow(emgTable, "Avg Response Time",
                String.format("%.1f hours", emg.getAvgResponseTimeHours()), subFont, bodyFont);
        doc.add(emgTable);

        // ── AI Forecast ───────────────────────────────────────────────────────
        doc.add(new Paragraph("Workload Forecast (" + forecast.getForecastMethod() + ")", headerFont));
        doc.add(Chunk.NEWLINE);

        PdfPTable fcTable = newTable(2);
        addKpiRow(fcTable, "Forecasted Imaging Volume (" + forecast.getNextMonthLabel() + ")",
                String.valueOf(forecast.getForecastedNextMonthVolume()), subFont, bodyFont);
        addKpiRow(fcTable, "Forecasted Emergency Cases",
                String.valueOf(forecast.getForecastedNextMonthEmergency()), subFont, bodyFont);
        addKpiRow(fcTable, "Forecasted Report Workload",
                String.valueOf(forecast.getForecastedNextMonthReports()), subFont, bodyFont);
        doc.add(fcTable);

        // ── Performance Insights ──────────────────────────────────────────────
        doc.add(new Paragraph("Performance Insights", headerFont));
        doc.add(Chunk.NEWLINE);

        for (InsightDTO insight : insights) {
            Color typeColor = insightTypeColor(insight.getType());
            Font insightFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, typeColor);

            Paragraph p = new Paragraph();
            p.add(new Chunk("[" + insight.getType().name() + "] ", insightFont));
            p.add(new Chunk(insight.getTitle() + ": ", subFont));
            p.add(new Chunk(insight.getMessage(), bodyFont));
            if (insight.getRecommendation() != null) {
                p.add(new Chunk(" → " + insight.getRecommendation(), smallFont));
            }
            p.setSpacingAfter(6);
            doc.add(p);
        }

        doc.close();
        return baos.toByteArray();
    }

    // ── Excel Report ──────────────────────────────────────────────────────────

    public byte[] generateExcelReport() throws Exception {
        DashboardStatsDTO     stats    = analyticsService.getDashboardStats();
        TurnaroundDTO         tat      = analyticsService.getTurnaroundMetrics();
        EmergencyAnalyticsDTO emg      = analyticsService.getEmergencyAnalytics();
        ForecastDTO           forecast = forecastService.getForecast();
        List<InsightDTO>      insights = insightService.generateInsights();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle dataStyle   = createDataStyle(wb);

            // ── Sheet 1: KPI Summary ──────────────────────────────────────────
            Sheet kpiSheet = wb.createSheet("KPI Summary");
            String[][] kpiData = {
                {"Metric", "Value"},
                {"Total Imaging Requests",      String.valueOf(stats.getTotalImagingRequests())},
                {"Avg Turnaround (days)",        String.format("%.1f", stats.getAvgTurnaroundDays())},
                {"Total Emergency Cases",        String.valueOf(stats.getTotalEmergencyCases())},
                {"Avg Emergency Response (min)", String.format("%.1f", stats.getAvgEmergencyResponseMinutes())},
                {"Active Radiologists",          String.valueOf(stats.getActiveRadiologists())},
                {"Pending Reports",              String.valueOf(stats.getPendingReports())},
                {"Request Growth (%)",           String.format("%.1f", stats.getRequestGrowthPercent())},
                {"Emergency This Month",         String.valueOf(stats.getEmergencyThisMonth())},
            };
            writeSheet(kpiSheet, kpiData, headerStyle, dataStyle);

            // ── Sheet 2: Turnaround ───────────────────────────────────────────
            Sheet tatSheet = wb.createSheet("Turnaround Metrics");
            String[][] tatData = {
                {"Metric", "Value (days)"},
                {"Avg Appointment Turnaround",    String.format("%.1f", tat.getAvgAppointmentTurnaroundDays())},
                {"Median Appointment Turnaround", String.format("%.1f", tat.getMedianAppointmentTurnaroundDays())},
                {"Fastest Appointment",           String.format("%.1f", tat.getFastestAppointmentTurnaroundDays())},
                {"Slowest Appointment",           String.format("%.1f", tat.getSlowestAppointmentTurnaroundDays())},
                {"Avg Report Turnaround",         String.format("%.1f", tat.getAvgReportTurnaroundDays())},
                {"Median Report Turnaround",      String.format("%.1f", tat.getMedianReportTurnaroundDays())},
                {"Fastest Report",                String.format("%.1f", tat.getFastestReportTurnaroundDays())},
                {"Slowest Report",                String.format("%.1f", tat.getSlowestReportTurnaroundDays())},
            };
            writeSheet(tatSheet, tatData, headerStyle, dataStyle);

            if (tat.getMonthLabels() != null) {
                int rowNum = tatData.length + 2;
                Row trendHeader = tatSheet.createRow(rowNum++);
                writeCell(trendHeader, 0, "Month",                    headerStyle);
                writeCell(trendHeader, 1, "Appointment TAT (days)",   headerStyle);
                writeCell(trendHeader, 2, "Report TAT (days)",        headerStyle);
                for (int i = 0; i < tat.getMonthLabels().size(); i++) {
                    Row r = tatSheet.createRow(rowNum++);
                    writeCell(r, 0, tat.getMonthLabels().get(i), dataStyle);
                    writeCell(r, 1, formatListValue(tat.getAppointmentTurnaroundByMonth(), i), dataStyle);
                    writeCell(r, 2, formatListValue(tat.getReportTurnaroundByMonth(), i),      dataStyle);
                }
            }

            // ── Sheet 3: Emergency ────────────────────────────────────────────
            Sheet emgSheet = wb.createSheet("Emergency Analysis");
            String[][] emgData = {
                {"Metric", "Value"},
                {"Total Emergency Requests",  String.valueOf(emg.getTotalEmergencyRequests())},
                {"Completed Cases",           String.valueOf(emg.getCompletedEmergencyCases())},
                {"Pending Cases",             String.valueOf(emg.getPendingEmergencyCases())},
                {"Avg Response Time (hours)", String.format("%.1f", emg.getAvgResponseTimeHours())},
            };
            writeSheet(emgSheet, emgData, headerStyle, dataStyle);

            if (emg.getMonthLabels() != null) {
                int rowNum = emgData.length + 2;
                Row trendH = emgSheet.createRow(rowNum++);
                writeCell(trendH, 0, "Month",           headerStyle);
                writeCell(trendH, 1, "Emergency Cases", headerStyle);
                for (int i = 0; i < emg.getMonthLabels().size(); i++) {
                    Row r = emgSheet.createRow(rowNum++);
                    writeCell(r, 0, emg.getMonthLabels().get(i), dataStyle);
                    writeCell(r, 1, formatListValue(emg.getMonthlyEmergencyCounts(), i), dataStyle);
                }
            }

            // ── Sheet 4: Forecast ─────────────────────────────────────────────
            Sheet fcSheet = wb.createSheet("Forecast");
            String[][] fcData = {
                {"Metric", "Value"},
                {"Forecast Method",            forecast.getForecastMethod()},
                {"Next Month",                 forecast.getNextMonthLabel()},
                {"Forecasted Imaging Volume",  String.valueOf(forecast.getForecastedNextMonthVolume())},
                {"Forecasted Emergency Cases", String.valueOf(forecast.getForecastedNextMonthEmergency())},
                {"Forecasted Report Workload", String.valueOf(forecast.getForecastedNextMonthReports())},
                {"Confidence Interval (±)",    String.format("%.2f", forecast.getConfidenceInterval())},
            };
            writeSheet(fcSheet, fcData, headerStyle, dataStyle);

            if (forecast.getChartLabels() != null) {
                int rowNum = fcData.length + 2;
                Row histH = fcSheet.createRow(rowNum++);
                writeCell(histH, 0, "Period", headerStyle);
                writeCell(histH, 1, "Volume", headerStyle);
                List<Long> hist = forecast.getHistoricalVolumes();
                for (int i = 0; i < forecast.getChartLabels().size(); i++) {
                    Row r = fcSheet.createRow(rowNum++);
                    writeCell(r, 0, forecast.getChartLabels().get(i), dataStyle);
                    String vol = (hist != null && i < hist.size())
                            ? String.valueOf(hist.get(i))
                            : String.valueOf(forecast.getForecastedVolume()) + " (forecast)";
                    writeCell(r, 1, vol, dataStyle);
                }
            }

            // ── Sheet 5: Insights ─────────────────────────────────────────────
            Sheet insightSheet = wb.createSheet("Performance Insights");
            Row insightHeader = insightSheet.createRow(0);
            writeCell(insightHeader, 0, "Type",           headerStyle);
            writeCell(insightHeader, 1, "Title",          headerStyle);
            writeCell(insightHeader, 2, "Message",        headerStyle);
            writeCell(insightHeader, 3, "Metric",         headerStyle);
            writeCell(insightHeader, 4, "Recommendation", headerStyle);

            int ir = 1;
            for (InsightDTO insight : insights) {
                Row r = insightSheet.createRow(ir++);
                writeCell(r, 0, insight.getType() != null ? insight.getType().name() : "", dataStyle);
                writeCell(r, 1, nvl(insight.getTitle()),          dataStyle);
                writeCell(r, 2, nvl(insight.getMessage()),        dataStyle);
                writeCell(r, 3, nvl(insight.getMetric()),         dataStyle);
                writeCell(r, 4, nvl(insight.getRecommendation()), dataStyle);
            }
            autoSizeColumns(insightSheet, 5);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            return baos.toByteArray();
        }
    }

    // ── PDF helpers ───────────────────────────────────────────────────────────

    private PdfPTable newTable(int columns) {
        PdfPTable t = new PdfPTable(columns);
        t.setWidthPercentage(100);
        t.setSpacingBefore(6);
        t.setSpacingAfter(16);
        return t;
    }

    private void addSpacer(Document doc) throws DocumentException {
        doc.add(new Paragraph(" "));
    }

    private void addKpiRow(PdfPTable table, String label, String value,
                           Font labelFont, Font valueFont) {
        PdfPCell lc = new PdfPCell(new Phrase(label, labelFont));
        lc.setBorder(Rectangle.BOTTOM);
        lc.setPadding(6);
        PdfPCell vc = new PdfPCell(new Phrase(value, valueFont));
        vc.setBorder(Rectangle.BOTTOM);
        vc.setPadding(6);
        table.addCell(lc);
        table.addCell(vc);
    }

    private Color insightTypeColor(InsightDTO.InsightType type) {
        if (type == null) return Color.GRAY;
        switch (type) {
            case SUCCESS:  return Color.decode("#198754");
            case WARNING:  return Color.decode("#fd7e14");
            case CRITICAL: return Color.decode("#dc3545");
            default:       return Color.decode("#0dcaf0");
        }
    }

    // ── Excel helpers ─────────────────────────────────────────────────────────

    private void writeSheet(Sheet sheet, String[][] data,
                            CellStyle headerStyle, CellStyle dataStyle) {
        for (int r = 0; r < data.length; r++) {
            Row row = sheet.createRow(r);
            for (int c = 0; c < data[r].length; c++) {
                writeCell(row, c, data[r][c], r == 0 ? headerStyle : dataStyle);
            }
        }
        autoSizeColumns(sheet, data[0].length);
    }

    private void writeCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        // Use FQN to avoid conflict with com.lowagie.text.Font
        org.apache.poi.ss.usermodel.Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDataStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setWrapText(true);
        return style;
    }

    private void autoSizeColumns(Sheet sheet, int numCols) {
        for (int i = 0; i < numCols; i++) {
            sheet.autoSizeColumn(i);
            int width = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.min(width + 512, 20000));
        }
    }

    /**
     * Formats one element from a heterogeneous list as a display string.
     * Handles Double (1 decimal place), Long, and any other type via toString.
     */
    private String formatListValue(List<?> list, int index) {
        if (list == null || index >= list.size()) return "0";
        Object v = list.get(index);
        if (v instanceof Double) return String.format("%.1f", (Double) v);
        return v != null ? v.toString() : "0";
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }
}
