package com.example.insurance_app.infrastructure.persistence.projection.geography;

import java.util.UUID;

public interface CountyProjection {
    UUID getId();
    String getName();
    String getCode();
}
