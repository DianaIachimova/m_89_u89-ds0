package com.example.insurance_app.application.exception;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final String field;
    private final Serializable value;

    public ResourceNotFoundException(String resource, String field, Serializable value) {
        super("%s not found".formatted(resource));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
    public String getResource() {
        return resource;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
