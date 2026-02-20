package com.example.insurance_app.infrastructure.persistence.repository.policy.projection;

import java.math.BigDecimal;

public record PolicyReportProjection(
    String groupingKey,
    String currencyCode,
    long policyCount,
    BigDecimal totalFinalPremium,
    BigDecimal totalFinalPremiumInBaseCurrency
) {}
