package com.example.insurance_app.application.dto.metadata.riskfactors.request;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskLevelDto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;
public record CreateRiskFactorRequest (
    @NotNull(message = "Level is required")
    RiskLevelDto level,

    UUID referenceId,

    BuildingTypeDto buildingType,

    @NotNull(message = "Adjustment percentage is required")
    @Digits(integer = 1, fraction = 4)
    @DecimalMin(value = "-0.50", message = "Adjustment percentage must be at least -0.50")
    @DecimalMax(value = "1.00", message = "Adjustment percentage must be at most 1.00")
    BigDecimal adjustmentPercentage,

    @NotNull(message = "Active status is required")
    Boolean active
){}
