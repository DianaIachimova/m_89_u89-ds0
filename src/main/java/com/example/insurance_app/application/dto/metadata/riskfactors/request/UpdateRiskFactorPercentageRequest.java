package com.example.insurance_app.application.dto.metadata.riskfactors.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateRiskFactorPercentageRequest (
        @NotNull(message = "Adjustment percentage is required")
        @Digits(integer = 1, fraction = 4)
        @DecimalMin(value = "-0.50", message = "Adjustment percentage must be at least -0.50")
        @DecimalMax(value = "1.00", message = "Adjustment percentage must be at most 1.00")
        BigDecimal adjustmentPercentage
){
}
