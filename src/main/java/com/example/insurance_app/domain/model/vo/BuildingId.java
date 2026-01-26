package com.example.insurance_app.domain.model.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.util.UUID;

public record BuildingId(UUID value) {

    public BuildingId {
        DomainAssertions.notNull(value, "Building ID");
    }
}
