package com.dylanclarke.FleetManagementAPI.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.dylanclarke.FleetManagementAPI.dto.ErrorResponse;
import com.dylanclarke.FleetManagementAPI.exception.AuthenticationException;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * Retrieves the existing request trace ID.
     *
     * The RequestLoggingFilter creates the trace ID at the beginning
     * of the request lifecycle. If one does not exist, a fallback ID
     * is generated to ensure all errors remain traceable.
     */
    private String getTraceId(HttpServletRequest request) {

        return Optional.ofNullable(
                (String) request.getAttribute("traceId")
        )
        .orElse(UUID.randomUUID().toString());
    }


    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "RESOURCE_NOT_FOUND traceId={} method={} uri={} message={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "VALIDATION_FAILED traceId={} method={} uri={} message={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handle DuplicateResourceException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "DUPLICATE_RESOURCE traceId={} method={} uri={} message={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Duplicate Resource",
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }


    /**
     * Handle database constraint violations
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "DATA_INTEGRITY_VIOLATION traceId={} method={} uri={} message={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                ex.getMostSpecificCause().getClass().getSimpleName()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Data Conflict",
                "The request conflicts with existing data",
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.CONFLICT
        );
    }


    /**
     * Handle MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        var fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()))
                .collect(Collectors.toList());

        logger.warn(
                "REQUEST_VALIDATION_FAILED traceId={} method={} uri={} fieldErrors={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                fieldErrors
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request",
                "Request validation failed",
                request.getRequestURI(),
                traceId
        );

        response.setFieldErrors(fieldErrors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "BAD_REQUEST traceId={} method={} uri={} message={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
    * Handle invalid sort properties
    */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handlePropertyReferenceException(
            PropertyReferenceException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "INVALID_SORT_PROPERTY traceId={} method={} uri={} message={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Sort Property",
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    /**
     * Handle NoHandlerFoundException
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "ENDPOINT_NOT_FOUND traceId={} method={} url={}",
                traceId,
                ex.getHttpMethod(),
                ex.getRequestURL()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Endpoint Not Found",
                String.format(
                        "No handler found for %s %s",
                        ex.getHttpMethod(),
                        ex.getRequestURL()
                ),
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    /**
     * Handle AuthenticationException
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "AUTH_FAILED traceId={} method={} uri={}",
                traceId,
                request.getMethod(),
                request.getRequestURI()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid authentication credentials",
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    /**
     * Handle unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.error(
                "UNEXPECTED_ERROR traceId={} method={} uri={} message={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please contact support with trace ID: " + traceId,
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


    /**
     * Handle AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        String traceId = getTraceId(request);

        logger.warn(
                "ACCESS_DENIED traceId={} method={} uri={}",
                traceId,
                request.getMethod(),
                request.getRequestURI()
        );

        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "You do not have permission to access this resource",
                request.getRequestURI(),
                traceId
        );

        return new ResponseEntity<>(
                response,
                HttpStatus.FORBIDDEN
        );
    }
}
