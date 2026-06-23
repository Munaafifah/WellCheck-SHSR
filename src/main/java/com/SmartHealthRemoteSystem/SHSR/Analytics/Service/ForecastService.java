package com.SmartHealthRemoteSystem.SHSR.Analytics.Service;

import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.ForecastDTO;
import com.SmartHealthRemoteSystem.SHSR.Analytics.Repository.AnalyticsAggregationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * UCR030 — 3-Month Moving Average Forecasting.
 * No external AI APIs. Uses historical monthly data to project next month.
 */
@Service
public class ForecastService {

    private static final String[] MONTH_NAMES = {
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    };

    private final AnalyticsAggregationRepository aggRepo;

    @Autowired
    public ForecastService(AnalyticsAggregationRepository aggRepo) {
        this.aggRepo = aggRepo;
    }

    public ForecastDTO getForecast() {
        // Fetch last 6 months of data for moving average
        Map<String, Long> requestsByMonth   = aggRepo.getMonthlyRequestCounts(6);
        Map<String, Long> emergencyByMonth  = aggRepo.getMonthlyEmergencyCounts(6);
        Map<String, Long> reportsByMonth    = aggRepo.getMonthlyReportCounts(6);

        List<String> keys6 = aggRepo.getMonthKeys(6);

        List<Long> requestValues  = keys6.stream()
                .map(k -> requestsByMonth.getOrDefault(k, 0L)).collect(Collectors.toList());
        List<Long> emergencyValues = keys6.stream()
                .map(k -> emergencyByMonth.getOrDefault(k, 0L)).collect(Collectors.toList());
        List<Long> reportValues   = keys6.stream()
                .map(k -> reportsByMonth.getOrDefault(k, 0L)).collect(Collectors.toList());

        // 3-month moving average forecast
        long forecastVolume    = movingAverage3(requestValues);
        long forecastEmergency = movingAverage3(emergencyValues);
        long forecastReports   = movingAverage3(reportValues);

        // Chart: show last 3 months + forecasted next month
        List<String> keys3 = aggRepo.getMonthKeys(3);
        List<String> chartLabels = keys3.stream()
                .map(this::monthKeyToLabel).collect(Collectors.toList());
        chartLabels.add(nextMonthLabel());

        List<Long> historicalVolumes = keys3.stream()
                .map(k -> requestsByMonth.getOrDefault(k, 0L)).collect(Collectors.toList());

        // Confidence interval: ±10% of standard deviation of last 3 months
        double stdDev = stdDev(historicalVolumes);
        double confidenceInterval = Math.round(stdDev * 100.0) / 100.0;

        return ForecastDTO.builder()
                .forecastedNextMonthVolume(forecastVolume)
                .forecastedNextMonthEmergency(forecastEmergency)
                .forecastedNextMonthReports(forecastReports)
                .chartLabels(chartLabels)
                .historicalVolumes(historicalVolumes)
                .forecastedVolume(forecastVolume)
                .confidenceInterval(confidenceInterval)
                .forecastMethod("3-Month Moving Average")
                .nextMonthLabel(nextMonthLabel())
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private long movingAverage3(List<Long> values) {
        if (values == null || values.size() < 3) {
            long sum = values == null ? 0 : values.stream().mapToLong(Long::longValue).sum();
            long cnt = values == null || values.isEmpty() ? 1 : values.size();
            return sum / cnt;
        }
        int n = values.size();
        return (values.get(n - 1) + values.get(n - 2) + values.get(n - 3)) / 3;
    }

    private double stdDev(List<Long> values) {
        if (values == null || values.size() < 2) return 0.0;
        double mean = values.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double variance = values.stream()
                .mapToDouble(v -> Math.pow(v - mean, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }

    private String nextMonthLabel() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        return MONTH_NAMES[c.get(Calendar.MONTH)] + " (Forecast)";
    }

    private String monthKeyToLabel(String key) {
        if (key == null || !key.contains("-")) return key;
        int month = Integer.parseInt(key.split("-")[1]) - 1;
        return MONTH_NAMES[month];
    }
}
