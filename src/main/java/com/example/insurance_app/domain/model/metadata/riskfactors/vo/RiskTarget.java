package com.example.insurance_app.domain.model.metadata.riskfactors.vo;

import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import com.example.insurance_app.domain.util.DomainAssertions;
import java.util.UUID;


public record RiskTarget(
        RiskLevel level,
        UUID referenceId,
        BuildingType buildingType
) {
    public RiskTarget {
        DomainAssertions.notNull(level, "Risk level");

        if (level == RiskLevel.BUILDING_TYPE) {
            DomainAssertions.notNull(buildingType, "Building type is required for BUILDING_TYPE level. Building type");
            DomainAssertions.check(referenceId == null,
                    "Reference ID must be null for BUILDING_TYPE level");
        } else {
            DomainAssertions.notNull(referenceId, "Reference ID is required for geographic levels. Reference ID");
            DomainAssertions.check(buildingType == null,
                    "Building type must be null for geographic levels");
        }
    }

    public static RiskTarget ofGeography(RiskLevel level, UUID referenceId) {
        return new RiskTarget(level, referenceId, null);
    }

    public static RiskTarget ofBuildingType(BuildingType buildingType) {
        return new RiskTarget(RiskLevel.BUILDING_TYPE, null, buildingType);
    }
}
