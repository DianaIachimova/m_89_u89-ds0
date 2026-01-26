package com.example.insurance_app.webapi.error;

public final class ProblemTypes {
    private ProblemTypes() {}

    public static final String NOT_FOUND = "urn:problem:not-found";
    public static final String INVALID_PARAMETER = "urn:problem:invalid-parameter";
    public static final String ENDPOINT_NOT_FOUND = "urn:problem:endpoint-not-found";
    public static final String DB_ERROR = "urn:problem:db-error";
    public static final String INTERNAL_ERROR = "urn:problem:internal-error";
    public static final String DOMAIN_VALIDATION = "urn:problem:domain-validation";
    public static final String DUPLICATE_RESOURCE = "urn:problem:duplicate-resource";
    public static final String VALIDATION_ERROR = "urn:problem:validation-error";
}