package com.example.insurance_app.application.exception;

import java.util.UUID;

public class BuildingNotFoundException extends ResourceNotFoundException {
    public BuildingNotFoundException(UUID buildingId) {
        super("Building", "id", buildingId);
    }
}
