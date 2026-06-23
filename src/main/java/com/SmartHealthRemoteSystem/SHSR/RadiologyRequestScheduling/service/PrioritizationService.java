package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.UrgencyLevel;
import org.springframework.stereotype.Service;

/**
 * UCR011 — Assigns a numeric priority score to an ImagingRequest.
 * Base score comes from urgency level; clinical notes can boost the score
 * when high-risk keywords are detected.
 */
@Service
public class PrioritizationService {

    private static final int BASE_EMERGENCY = 100;
    private static final int BASE_URGENT    =  70;
    private static final int BASE_ROUTINE   =  30;
    private static final int KEYWORD_BOOST  =  20;

    private static final String[] HIGH_RISK_KEYWORDS = { "stroke", "bleeding", "trauma" };

    public int computeScore(UrgencyLevel urgencyLevel, String clinicalNotes) {
        int score = baseScore(urgencyLevel);
        if (clinicalNotes != null && containsHighRiskKeyword(clinicalNotes.toLowerCase())) {
            score = Math.min(100, score + KEYWORD_BOOST);
        }
        return score;
    }

    private int baseScore(UrgencyLevel urgencyLevel) {
        if (urgencyLevel == null) return BASE_ROUTINE;
        switch (urgencyLevel) {
            case EMERGENCY: return BASE_EMERGENCY;
            case URGENT:    return BASE_URGENT;
            default:        return BASE_ROUTINE;
        }
    }

    private boolean containsHighRiskKeyword(String lowerNotes) {
        for (String keyword : HIGH_RISK_KEYWORDS) {
            if (lowerNotes.contains(keyword)) return true;
        }
        return false;
    }
}
