package com.example.insurance_app.application.dto.metadata.currency.request;

import com.example.insurance_app.application.dto.metadata.currency.CurrencyAction;
import jakarta.validation.constraints.NotNull;

public record CurrencyActionRequest(
        @NotNull(message = "Action is required")
        CurrencyAction action
) {
}
