package com.example.insurance_app.application.dto.policy.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PolicyResponse(
        UUID id,
        String policyNumber,
        String status,
        UUID clientId,
        UUID buildingId,
        UUID brokerId,
        String currencyCode,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal basePremium,
        BigDecimal finalPremium,
        LocalDate cancelledAt,
        String cancellationReason,
        Instant createdAt,
        Instant updatedAt
) {
}
