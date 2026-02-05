package com.example.insurance_app.domain.model.building.vo;

import com.example.insurance_app.domain.util.DomainAssertions;
import java.util.UUID;

public record BuildingId(UUID value) {

    public BuildingId {
        DomainAssertions.notNull(value, "Building ID");
    }
}
