package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.exception;

/**
 * Thrown when a RadiologyAppointment document cannot be found.
 * Maps to HTTP 404 in SchedulingExceptionHandler.
 */
public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
