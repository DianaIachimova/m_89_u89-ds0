package com.example.insurance_app.domain.model.metadata.riskfactors.vo;

import com.example.insurance_app.domain.util.DomainAssertions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import static com.example.insurance_app.domain.model.ConstantFields.ADJUSTMENT_PERCENTAGE;

public record AdjustmentPercentage(BigDecimal value) {

    private static final BigDecimal MIN = new BigDecimal("-0.50");
    private static final BigDecimal MAX = new BigDecimal("1.00");

    public AdjustmentPercentage {
        DomainAssertions.notNull(value, ADJUSTMENT_PERCENTAGE);
        DomainAssertions.requireBigDecimalFormat(value, 1, 4, ADJUSTMENT_PERCENTAGE);
        DomainAssertions.requireInRange(value, MIN, MAX, ADJUSTMENT_PERCENTAGE);
        value = value.setScale(4, RoundingMode.HALF_UP);
    }

    public static AdjustmentPercentage of(BigDecimal value) {
        return new AdjustmentPercentage(value);
    }
}

