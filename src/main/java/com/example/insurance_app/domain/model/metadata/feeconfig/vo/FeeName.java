package com.example.insurance_app.domain.model.metadata.feeconfig.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record FeeName(String value) {
    public FeeName{
        DomainAssertions.notBlank(value, "name");
        var v= DomainAssertions.normalize(value);
        DomainAssertions.check(v.length() <= 200,
                "name must be at most 200 characters");
        value=v;

    }
    public static FeeName of(String name) {
        return new FeeName(name);
    }
}
