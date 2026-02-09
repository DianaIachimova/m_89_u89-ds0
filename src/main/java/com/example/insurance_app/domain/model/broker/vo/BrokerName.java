package com.example.insurance_app.domain.model.broker.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record BrokerName(String value) {
    public BrokerName {
        DomainAssertions.notBlank(value, "Broker name");
        value = DomainAssertions.normalize(value);
        DomainAssertions.check(
                value.length() >= 2 && value.length() <= 120,
                "Broker name must be between 2 and 120 characters"
        );
    }
}
