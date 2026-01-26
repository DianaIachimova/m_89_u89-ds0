package com.example.insurance_app.application.exception;

import java.util.UUID;

public class InvalidCityException extends ResourceNotFoundException {
    public InvalidCityException(UUID cityId) {
        super("City", "id", cityId);
    }
}
