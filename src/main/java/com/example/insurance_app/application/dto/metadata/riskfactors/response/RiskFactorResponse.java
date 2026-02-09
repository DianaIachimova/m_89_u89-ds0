package com.example.insurance_app.application.dto.metadata.riskfactors.response;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskLevelDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RiskFactorResponse(
        UUID id,
        RiskLevelDto level,
        UUID referenceId,
        BuildingTypeDto buildingType,
        BigDecimal adjustmentPercentage,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
