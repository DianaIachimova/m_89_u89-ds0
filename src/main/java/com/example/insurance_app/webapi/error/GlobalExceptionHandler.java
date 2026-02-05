package com.example.insurance_app.webapi.error;

import com.example.insurance_app.application.exception.DuplicateIdentificationNumberException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String BAD_REQUEST = "Bad Request";
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomain(DomainException ex, HttpServletRequest req) {
        logger.warn("Domain error code={} path={} msg={}", ex.getCode(), req.getRequestURI(), ex.getMessage());
        Map<String, Object> pdProps =
                ex.getProps() == null ? null : new HashMap<>(ex.getProps());

        return ProblemDetailsFactory.of(
                ex.getStatus(),
                ex.getType(),
                ex.getTitle(),
                ex.getDetail() + " "+ ex.getMessage(),
                req.getRequestURI(),
                ex.getCode(),
                pdProps
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        logger.warn("Not found resource={} field={} value={} path={}",
                ex.getResource(), ex.getField(), ex.getValue(), req.getRequestURI());

        return ProblemDetailsFactory.of(
                HttpStatus.NOT_FOUND,
                ProblemTypes.NOT_FOUND,
                "Not Found",
                "%s was not found".formatted(ex.getResource()),
                req.getRequestURI(),
                "NOT_FOUND",
                Map.of(
                        "resource", ex.getResource(),
                        "field", ex.getField()
                )
        );
    }

    @ExceptionHandler(DuplicateIdentificationNumberException.class)
    public ProblemDetail handleDuplicateIdentificationNumber(
            DuplicateIdentificationNumberException ex, HttpServletRequest req) {
        logger.warn("Duplicate identification number={} path={}",
                ex.getIdentificationNumber(), req.getRequestURI());

        return ProblemDetailsFactory.of(
                HttpStatus.CONFLICT,
                ProblemTypes.DUPLICATE_RESOURCE,
                "Conflict",
                ex.getMessage(),
                req.getRequestURI(),
                "DUPLICATE_IDENTIFICATION_NUMBER",
                Map.of("identificationNumber", ex.getIdentificationNumber())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        logger.warn("Validation failed path={}", req.getRequestURI());

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        return ProblemDetailsFactory.of(
                HttpStatus.BAD_REQUEST,
                ProblemTypes.VALIDATION_ERROR,
                BAD_REQUEST,
                "Validation failed",
                req.getRequestURI(),
                "VALIDATION_ERROR",
                Map.of("errors", errors)
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        logger.warn("Invalid argument path={} msg={}", req.getRequestURI(), ex.getMessage());

        return ProblemDetailsFactory.of(
                HttpStatus.BAD_REQUEST,
                ProblemTypes.INVALID_PARAMETER,
                BAD_REQUEST,
                ex.getMessage(),
                req.getRequestURI(),
                "INVALID_ARGUMENT",
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleInvalidPathParam(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        logger.warn("Bad request method={} path={} error={}", req.getMethod(), req.getRequestURI(),
                ex.getCause() !=null ? ex.getCause() : ex.getClass().getSimpleName());
        var requiredType = ex.getRequiredType();

        return ProblemDetailsFactory.of(
                HttpStatus.BAD_REQUEST,
                ProblemTypes.INVALID_PARAMETER,
                BAD_REQUEST,
                "Invalid value for parameter '%s'".formatted(ex.getName()),
                req.getRequestURI(),
                "INVALID_PARAMETER",
                Map.of(
                        "parameter", ex.getName(),
                        "expectedType",
                        requiredType != null ? requiredType.getSimpleName() : "unknown"

                )
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleEndpointNotFound(NoResourceFoundException ex, HttpServletRequest req) {
        logger.warn("Endpoint not found method={} path={}", req.getMethod(), req.getRequestURI());

        return ProblemDetailsFactory.of(
                HttpStatus.NOT_FOUND,
                ProblemTypes.ENDPOINT_NOT_FOUND,
                "Not Found",
                "Endpoint not found",
                req.getRequestURI(),
                "ENDPOINT_NOT_FOUND",
                null
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccessBD(DataAccessException ex, HttpServletRequest req) {
        logger.error("Database error path={}", req.getRequestURI(), ex);

        return ProblemDetailsFactory.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemTypes.DB_ERROR,
                "Internal Server Error",
                "Unexpected error occurred",
                req.getRequestURI(),
                "DB_ERROR",
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex, HttpServletRequest req) {
        logger.error("Unexpected error path={}", req.getRequestURI(), ex);

        return ProblemDetailsFactory.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ProblemTypes.INTERNAL_ERROR,
                "Internal Server Error",
                "Unexpected error occurred",
                req.getRequestURI(),
                "INTERNAL_ERROR",
                null
        );
    }
}
