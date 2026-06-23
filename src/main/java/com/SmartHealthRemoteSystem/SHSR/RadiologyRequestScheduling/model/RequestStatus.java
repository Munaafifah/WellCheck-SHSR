package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

/**
 * Complete lifecycle status of an ImagingRequest document.
 *
 * Main flow:
 *   PENDING → APPROVED → SCHEDULED → IN_PROGRESS → COMPLETED → REPORT_PENDING → REPORT_READY
 *
 * Alternative flows:
 *   PENDING  → REJECTED    (Radiologist/Radiographer rejects)
 *   PENDING  → CANCELLED   (Radiologist/Radiographer cancels)
 *   APPROVED → CANCELLED   (Radiologist/Radiographer cancels)
 *
 * Forbidden cancellation targets: COMPLETED, REPORT_PENDING, REPORT_READY
 */
public enum RequestStatus {

    /** Initial state — submitted by Doctor, awaiting radiologist/radiographer review. */
    PENDING,

    /** Approved by Radiologist or Radiographer; AI prioritization has been applied. */
    APPROVED,

    /** Rejected by Radiologist or Radiographer with a recorded reason. */
    REJECTED,

    /** Appointment has been scheduled; slot assigned. */
    SCHEDULED,

    /** Imaging procedure is actively in progress. */
    IN_PROGRESS,

    /** Imaging procedure completed; report has not yet been written. */
    COMPLETED,

    /** Imaging done; radiology report is pending authorship. */
    REPORT_PENDING,

    /** Radiology report written and available; full lifecycle complete. */
    REPORT_READY,

    /** Request cancelled by Radiologist or Radiographer before imaging was performed. */
    CANCELLED
}
