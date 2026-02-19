package com.example.insurance_app.application.dto.policy.response;

import com.example.insurance_app.application.dto.building.response.BuildingDetailedResponse;
import com.example.insurance_app.application.dto.client.response.ClientRefResponse;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyRefResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PolicyDetailResponse(
        UUID id,
        String policyNumber,
        String status,
        ClientRefResponse client,
        BuildingDetailedResponse building,
        UUID brokerId,
        CurrencyRefResponse currency,
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