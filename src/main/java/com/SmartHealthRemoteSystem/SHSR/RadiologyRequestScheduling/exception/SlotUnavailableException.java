package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.exception;

/**
 * Thrown when the requested equipment + time slot combination is already
 * occupied on the target date.  Maps to HTTP 409 Conflict.
 */
public class SlotUnavailableException extends RuntimeException {

    public SlotUnavailableException(String message) {
        super(message);
    }
}
