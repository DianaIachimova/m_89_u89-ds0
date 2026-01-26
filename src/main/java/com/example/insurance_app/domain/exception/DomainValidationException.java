package com.example.insurance_app.domain.exception;

import com.example.insurance_app.webapi.error.ProblemTypes;
import org.springframework.http.HttpStatus;

public final class DomainValidationException extends DomainException {

    public DomainValidationException(String message) {
        super(
                message,
                HttpStatus.BAD_REQUEST,
                ProblemTypes.DOMAIN_VALIDATION,
                "Bad Request",
                "Invalid Request",
                "DOMAIN_VALIDATION",
                null
        );
    }
}
