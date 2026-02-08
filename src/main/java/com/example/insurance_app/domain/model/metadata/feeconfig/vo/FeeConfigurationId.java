package com.example.insurance_app.domain.model.metadata.feeconfig.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.util.UUID;

public record FeeConfigurationId(UUID value) {
    public FeeConfigurationId {
        DomainAssertions.notNull(value, "Fee Configuration ID");
    }
}
