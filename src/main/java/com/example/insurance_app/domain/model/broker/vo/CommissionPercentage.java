package com.example.insurance_app.domain.model.broker.vo;

import com.example.insurance_app.domain.util.DomainAssertions;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.insurance_app.domain.model.ConstantFields.COMMISSION_PERCENTAGE;

public record CommissionPercentage(BigDecimal value) {
    public CommissionPercentage {
        DomainAssertions.notNull(value, COMMISSION_PERCENTAGE);
        DomainAssertions.requireInRange(value, BigDecimal.ZERO, BigDecimal.valueOf(100) , COMMISSION_PERCENTAGE);
        DomainAssertions.requireBigDecimalFormat(value, 3, 2, COMMISSION_PERCENTAGE);
        value = value.setScale(2, RoundingMode.HALF_UP);
    }
}
