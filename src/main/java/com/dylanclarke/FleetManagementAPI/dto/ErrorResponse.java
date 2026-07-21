package com.dylanclarke.FleetManagementAPI.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public class ErrorResponse {

    @Schema(
            description = "HTTP status code",
            example = "401"
    )
    private int status;


    @Schema(
            description = "HTTP error category",
            example = "Unauthorized"
    )
    private String error;


    @Schema(
            description = "Detailed error message",
            example = "Full authentication is required to access this resource"
    )
    private String message;


    @Schema(
            description = "API endpoint where the error occurred",
            example = "/api/vehicles/1"
    )
    private String path;


    @Schema(
            description = "Timestamp when the error occurred"
    )
    private LocalDateTime timestamp;


    @Schema(
            description = "Request trace identifier used for diagnostics",
            example = "9f23fd2f-bbd4-4384-8c25-bfe41023bde5"
    )
    private String traceId;


    @Schema(
            description = "Validation errors for individual fields",
            nullable = true
    )
    private List<FieldError> fieldErrors;


    public ErrorResponse() {
    }


    public ErrorResponse(
            int status,
            String error,
            String message,
            String path,
            String traceId
    ) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
        this.traceId = traceId;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }


    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }


    public static class FieldError {

        @Schema(
                description = "Field that failed validation",
                example = "username"
        )
        private String field;


        @Schema(
                description = "Validation failure message",
                example = "Username cannot be blank"
        )
        private String message;


        @Schema(
                description = "Rejected value",
                example = " "
        )
        private Object rejectedValue;


        public FieldError(
                String field,
                String message,
                Object rejectedValue
        ) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }


        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
    }
}