package com.dylanclarke.FleetManagementAPI.exception;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}