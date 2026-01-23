package com.example.insurance_app.application.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String resource;
    private final String field;
    private final Object value;

    public ResourceNotFoundException(String resource, String field, Object value) {
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
