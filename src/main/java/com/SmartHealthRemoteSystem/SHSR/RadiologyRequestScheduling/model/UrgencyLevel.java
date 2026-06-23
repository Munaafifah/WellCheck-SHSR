package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

/**
 * Clinical urgency level of an imaging request.
 * Drives the AI prioritization scoring in PrioritizationService.
 */
public enum UrgencyLevel {
    ROUTINE,
    URGENT,
    EMERGENCY
}
