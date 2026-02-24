package com.example.insurance_app.domain.model.client.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record EmailAddress(String value) {
    public EmailAddress {
        DomainAssertions.notBlank(value, "email");
        var v = value.trim();

        DomainAssertions.check(v.length() <= 100, "Invalid email length");
        DomainAssertions.check(
                v.matches("^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]{1,253}\\.[A-Za-z]{2,63}$"),
                "Invalid email format");

        value = v;
    }
}