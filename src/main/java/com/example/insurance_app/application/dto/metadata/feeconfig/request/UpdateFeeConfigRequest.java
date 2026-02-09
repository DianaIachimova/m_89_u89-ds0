package com.example.insurance_app.application.dto.metadata.feeconfig.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateFeeConfigRequest(
        @Size(max = 200, message = "Name must be between 1 and 200 characters")
        String name,

        @Digits(integer = 1, fraction = 4)
        @DecimalMin(value = "0.0", inclusive = false, message = "Percentage must be more then 0")
        @DecimalMax(value = "0.5", message = "Percentage must be at most 50")
        BigDecimal percentage,

        LocalDate effectiveTo
) {
}
