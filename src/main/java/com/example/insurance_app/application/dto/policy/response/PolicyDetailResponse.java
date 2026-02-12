package com.example.insurance_app.application.dto.policy.response;

import com.example.insurance_app.application.dto.building.response.BuildingDetailedResponse;
import com.example.insurance_app.application.dto.client.response.ClientResponse;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PolicyDetailResponse(
        UUID id,
        String policyNumber,
        String status,
        ClientResponse client,
        BuildingDetailedResponse building,
        UUID brokerId,
        CurrencyResponse currency,
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