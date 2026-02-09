package com.example.insurance_app.application.dto.policy.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PolicySummaryResponse(
        UUID id,
        String policyNumber,
        String status,
        UUID clientId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal finalPremium,
        String currencyCode
) {
}
