package com.example.insurance_app.application.dto.metadata.currency.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CurrencyResponse(
        UUID id,
        String code,
        String name,
        BigDecimal exchangeRateToBase,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt

) {
}
