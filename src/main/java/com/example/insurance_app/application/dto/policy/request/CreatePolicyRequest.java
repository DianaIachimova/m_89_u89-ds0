package com.example.insurance_app.application.dto.policy.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreatePolicyRequest(
        @NotNull(message = "Client ID is required")
        UUID clientId,

        @NotNull(message = "Building ID is required")
        UUID buildingId,

        @NotNull(message = "Broker ID is required")
        UUID brokerId,

        @NotNull(message = "Currency ID is required")
        UUID currencyId,

        @NotNull(message = "Base premium is required")
        @Positive(message = "Base premium must be positive")
        BigDecimal basePremium,

        @NotNull(message = "Start date is required")
        @FutureOrPresent(message = "Start date must not be in the past")
        LocalDate startDate,

        @NotNull(message = "End date is required")
        LocalDate endDate
) {
}
