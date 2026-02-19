package com.example.insurance_app.application.dto.metadata.currency.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CurrencyRefResponse(
        UUID id,
        String code,
        String name,
        BigDecimal exchangeRateToBase,
        boolean isActive
) {
}
