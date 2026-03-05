package com.dylanclarke.FleetManagementAPI.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Centralized exception handling for REST API.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        // return 400 with message; controller methods sometimes already handle, but service can throw
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    // other handlers could go here (e.g. EntityNotFound)
}
