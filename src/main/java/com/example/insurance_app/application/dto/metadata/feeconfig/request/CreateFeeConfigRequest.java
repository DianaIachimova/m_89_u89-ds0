package com.example.insurance_app.application.dto.metadata.feeconfig.request;

import com.example.insurance_app.application.dto.metadata.feeconfig.FeeConfigTypeDto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateFeeConfigRequest(
        @NotBlank(message = "Code is required")
        @Size(min = 3, max = 50, message = "Code must be between 3 and 50 characters")
        @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "code must be uppercase with underscore, e.g. STANDARD_BROKER_FEE")
        String code,

        @NotBlank(message = "Name is required")
        @Size(max = 200, message = "Name must be between 1 and 200 characters")
        String name,

        @NotNull(message = "Type is required")
        FeeConfigTypeDto type,

        @NotNull(message = "Percentage is required")
        @Digits(integer = 1, fraction = 4)
        @DecimalMin(value = "0.0", inclusive = false, message = "Percentage must be more then 0")
        @DecimalMax(value = "0.5", message = "Percentage must be at most 50")
        BigDecimal percentage,

        @NotNull(message = "Effective from is required")
        LocalDate effectiveFrom,

        LocalDate effectiveTo,

        @NotNull(message = "Currency status is required")
        Boolean isActive
) {
}
