package com.example.insurance_app.application.dto.metadata.currency.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateCurrencyRequest(
        @NotBlank(message = "Currency code is required")
        @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters (ISO 4217)")
        String code,

        @NotBlank(message = "Currency name is required")
        @Size(min = 3, max = 100, message = "Currency name must be between 1 and 100 characters")
        String name,

        @NotNull(message = "Exchange rate to base is required")
        @DecimalMin(value = "0.000001", message = "Exchange rate to base must be positive")
        @Digits(integer = 10, fraction = 6)
        BigDecimal exchangeRateToBase,

        @NotNull(message = "Currency status is required")
        Boolean isActive
) {
}
