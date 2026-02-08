package com.example.insurance_app.application.dto.metadata.feeconfig.request;

import com.example.insurance_app.application.dto.metadata.feeconfig.FeeConfigTypeDto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateFeeConfigRequest(
        @NotNull(message = "Update operation is required")
        FeeUpdateOperation operation,

        @Size(max = 200, message = "Name must be between 1 and 200 characters")
        String name,

        @Digits(integer = 2, fraction = 4)
        @DecimalMin(value = "0.0", inclusive = false, message = "Percentage must be more then 0")
        @DecimalMax(value = "50.0", message = "Percentage must be at most 50")
        BigDecimal percentage,

        LocalDate effectiveTo
) {
}
