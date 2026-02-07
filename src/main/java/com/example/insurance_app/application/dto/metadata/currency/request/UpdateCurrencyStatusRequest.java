package com.example.insurance_app.application.dto.metadata.currency.request;

import jakarta.validation.constraints.NotNull;

public record UpdateCurrencyStatusRequest(
        @NotNull(message = "Currency status is required")
        Boolean isActive
) {
}
