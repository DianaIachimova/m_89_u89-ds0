package com.example.insurance_app.domain.model.metadata.currency.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record CurrencyName(String name) {
    public CurrencyName{
        DomainAssertions.notBlank(name, "Currency Name");
        var v= DomainAssertions.normalize(name);
        DomainAssertions.check(v.length()>=3 && v.length() <= 100,
                "name must be at least 3 and at most 100 characters");
        name=v;
    }
}
