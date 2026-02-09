package com.example.insurance_app.domain.model.policy.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.util.UUID;

public record PolicyId(UUID value) {
    public PolicyId {
        DomainAssertions.notNull(value, "Policy ID");
    }
}
