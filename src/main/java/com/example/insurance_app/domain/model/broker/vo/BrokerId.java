package com.example.insurance_app.domain.model.broker.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.util.UUID;

public record BrokerId(UUID value) {
    public BrokerId {
        DomainAssertions.notNull(value, "Broker ID");
    }
}
