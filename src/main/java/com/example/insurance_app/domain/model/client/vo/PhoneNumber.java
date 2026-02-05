package com.example.insurance_app.domain.model.client.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record PhoneNumber(String value) {
    public PhoneNumber {
        DomainAssertions.notBlank(value, "phone");
        var v = value.replaceAll("\\s+", "");

        DomainAssertions.check(
                v.length() >= 6 && v.length() <= 20,
                "Invalid phone length");

        DomainAssertions.check(v.matches(
                "^[+]?\\d{6,20}$"),
                "Invalid phone format");

        value = v;
    }
}
