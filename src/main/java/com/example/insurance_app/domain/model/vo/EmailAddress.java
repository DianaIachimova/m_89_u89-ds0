package com.example.insurance_app.domain.model.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record EmailAddress(String value) {
    public EmailAddress {
        DomainAssertions.notBlank(value, "email");
        var v = value.trim();

        DomainAssertions.check(v.length() <= 254, "Invalid email length");
        DomainAssertions.check(
                v.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"),
                "Invalid email format");

        value = v;
    }
}