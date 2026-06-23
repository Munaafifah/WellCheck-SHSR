package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

/**
 * Imaging modality types supported by the radiology department.
 * Stored in MongoDB as the enum name string (e.g. "XRAY").
 */
public enum Modality {
    XRAY,
    CT,
    MRI,
    ULTRASOUND
}
