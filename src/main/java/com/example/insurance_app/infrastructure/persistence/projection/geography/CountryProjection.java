package com.example.insurance_app.infrastructure.persistence.projection.geography;

import java.util.UUID;

public interface CountryProjection {
    UUID getId();
    String getName();
}
