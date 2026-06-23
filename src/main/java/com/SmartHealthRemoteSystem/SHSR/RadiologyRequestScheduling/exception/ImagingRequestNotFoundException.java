package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.exception;

/**
 * Thrown when a requested ImagingRequest document does not exist in MongoDB.
 * Maps to HTTP 404 in SchedulingExceptionHandler.
 */
public class ImagingRequestNotFoundException extends RuntimeException {

    public ImagingRequestNotFoundException(String message) {
        super(message);
    }
}
