package com.example.insurance_app.application.dto.report;

import java.math.BigDecimal;

public record PolicyReportResponse(
    String groupingKey,
    String currencyCode,
    long policyCount,
    BigDecimal totalFinalPremium,
    BigDecimal totalFinalPremiumInBaseCurrency
) {}
