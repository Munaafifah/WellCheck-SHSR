package com.SmartHealthRemoteSystem.SHSR.Analytics.Service;

import com.SmartHealthRemoteSystem.SHSR.Analytics.DTO.*;

import java.util.List;
import java.util.Map;

public interface AnalyticsService {

    DashboardStatsDTO getDashboardStats();

    ImagingVolumeDTO getImagingVolume(String period);

    TurnaroundDTO getTurnaroundMetrics();

    EmergencyAnalyticsDTO getEmergencyAnalytics();

    List<TrendDTO> getTrends(String period);

    Map<String, Object> getRadiologistPerformance();

    Map<String, Object> getRadiographerPerformance();

    Map<String, Object> getPatientDemographics();

    Map<String, Object> getModalityUtilization(String period);
}
