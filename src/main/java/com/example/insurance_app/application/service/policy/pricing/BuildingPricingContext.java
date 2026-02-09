package com.example.insurance_app.application.service.policy.pricing;

import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;

import java.util.UUID;

public record BuildingPricingContext(
        UUID countryId,
        UUID countyId,
        UUID cityId,
        BuildingTypeEntity buildingType,
        RiskIndicators riskIndicators
) {
}
