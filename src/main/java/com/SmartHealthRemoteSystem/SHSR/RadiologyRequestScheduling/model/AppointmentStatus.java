package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

/**
 * Complete lifecycle status of a RadiologyAppointment document.
 *
 * Main flow:
 *   SCHEDULED → CONFIRMED → IN_PROGRESS → COMPLETED
 *
 * Alternative flows:
 *   SCHEDULED  → CANCELLED  (Radiologist/Radiographer cancels with reason)
 *   CONFIRMED  → CANCELLED  (Radiologist/Radiographer cancels with reason)
 *   CONFIRMED  → NO_SHOW    (Patient absent at appointment time)
 *
 * On CANCELLED: linked ImagingRequest reverts SCHEDULED → APPROVED.
 * Forbidden cancellation target: COMPLETED, IN_PROGRESS.
 */
public enum AppointmentStatus {

    /** Appointment created and slot assigned; awaiting confirmation. */
    SCHEDULED,

    /** Appointment confirmed by radiology staff. */
    CONFIRMED,

    /** Imaging procedure is actively in progress. */
    IN_PROGRESS,

    /** Imaging completed successfully — terminal success state. */
    COMPLETED,

    /** Appointment cancelled by Radiologist or Radiographer with a recorded reason. */
    CANCELLED,

    /** Patient did not attend at the appointed time — terminal state. */
    NO_SHOW
}
