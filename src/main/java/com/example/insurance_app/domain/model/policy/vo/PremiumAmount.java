package com.example.insurance_app.domain.model.policy.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.math.BigDecimal;

import static com.example.insurance_app.domain.model.ConstantFields.PREMIUM_AMOUNT;

public record PremiumAmount(BigDecimal value) {



    public PremiumAmount {
        DomainAssertions.notNull(value, PREMIUM_AMOUNT);
        DomainAssertions.requirePositive(value, PREMIUM_AMOUNT);
        DomainAssertions.requireBigDecimalFormat(value, 13, 2, PREMIUM_AMOUNT);
    }
}
