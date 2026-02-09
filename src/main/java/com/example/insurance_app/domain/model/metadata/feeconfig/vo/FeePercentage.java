package com.example.insurance_app.domain.model.metadata.feeconfig.vo;

import com.example.insurance_app.domain.util.DomainAssertions;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.insurance_app.domain.model.ConstantFields.FEE_PERCENTAGE;

public record FeePercentage (BigDecimal value) {

    public FeePercentage {
        DomainAssertions.notNull(value, FEE_PERCENTAGE);
        DomainAssertions.requireBigDecimalFormat(value, 1, 4, FEE_PERCENTAGE);
        DomainAssertions.requireInRange(value, BigDecimal.ZERO, new BigDecimal("0.5000"), FEE_PERCENTAGE);
        value = value.setScale(4, RoundingMode.HALF_UP);
    }

    public static FeePercentage of(BigDecimal value) {
        return new FeePercentage(value);
    }
}
