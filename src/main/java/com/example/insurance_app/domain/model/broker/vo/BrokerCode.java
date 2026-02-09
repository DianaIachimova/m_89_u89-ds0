package com.example.insurance_app.domain.model.broker.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record BrokerCode(String value) {
    public BrokerCode {
        DomainAssertions.notBlank(value, "Broker code");
        value = DomainAssertions.normalize(value).toUpperCase();
        DomainAssertions.check(
                value.matches("^[A-Z0-9_-]{3,30}$"),
                "Broker code must be 3-30 characters, allowed: A-Z, 0-9, _, -"
        );
    }
}
