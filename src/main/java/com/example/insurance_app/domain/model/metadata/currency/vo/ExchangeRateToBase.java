package com.example.insurance_app.domain.model.metadata.currency.vo;

import com.example.insurance_app.domain.util.DomainAssertions;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.insurance_app.domain.model.ConstantFields.EXCHANGE_RATE;

public record ExchangeRateToBase(BigDecimal exchangeRate) {
    public ExchangeRateToBase{
        DomainAssertions.notNull(exchangeRate, EXCHANGE_RATE);
        DomainAssertions.requirePositive(exchangeRate, EXCHANGE_RATE);
        DomainAssertions.requireBigDecimalFormat(exchangeRate, 10, 6, EXCHANGE_RATE);
        exchangeRate = exchangeRate.setScale(6, RoundingMode.HALF_UP);
    }
}

