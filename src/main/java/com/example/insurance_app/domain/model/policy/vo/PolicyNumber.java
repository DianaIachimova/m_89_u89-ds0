package com.example.insurance_app.domain.model.policy.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record PolicyNumber(String value) {
    public PolicyNumber {
        DomainAssertions.notBlank(value, "Policy number");
        value = DomainAssertions.normalize(value);
        DomainAssertions.check(
                value.length() >= 5 && value.length() <= 30,
                "Policy number must be between 5 and 30 characters"
        );
    }
}
