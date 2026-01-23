package com.example.insurance_app.webapi.error.error;

import com.example.insurance_app.application.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleInvalidPathParam(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        logger.warn("Bad request method={} path={} error={}", req.getMethod(), req.getRequestURI(),
                ex.getCause() !=null ? ex.getCause() : ex.getClass().getSimpleName());

        return ProblemDetailsFactory.of(
                HttpStatus.BAD_REQUEST,
                ProblemTypes.INVALID_PARAMETER,
                "Bad Request",
                "Invalid value for parameter '%s'".formatted(ex.getName()),
                req.getRequestURI(),
                "INVALID_PARAMETER",
                Map.of(
                        "parameter", ex.getName(),
                        "expectedType", ex.getRequiredType() !=null ? ex.getRequiredType().getSimpleName():"unknown"
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
