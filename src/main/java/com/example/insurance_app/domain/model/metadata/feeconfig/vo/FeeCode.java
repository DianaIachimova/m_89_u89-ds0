package com.example.insurance_app.domain.model.metadata.feeconfig.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record FeeCode(String value) {
    public FeeCode {
        DomainAssertions.notBlank(value, "Fee Code");
        var v = DomainAssertions.normalize(value);

        DomainAssertions.check(
                v.length()>=3 && v.length() <= 50,
                "Invalid fee code length");

        DomainAssertions.check(
                v.matches("^[A-Z][A-Z0-9_]*$"),
                "Invalid Fee code format");
        value = v;
    }

    public static FeeCode of(String value) {
        return new FeeCode(value);
    }
}
