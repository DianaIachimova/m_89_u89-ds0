package com.example.insurance_app.domain.model.metadata.currency.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record CurrencyCode(String code) {
    public CurrencyCode {
        DomainAssertions.notBlank(code, "Currency code");
        var value= DomainAssertions.normalize(code).toUpperCase();
        DomainAssertions.check(value.matches(code), "code must be exactly 3 uppercase letters");
        code=value;
    }
}
