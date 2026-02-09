package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskFactorConfiguration;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.AdjustmentPercentage;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskFactorConfigurationId;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskTarget;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskFactorConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskLevelEntity;
import org.springframework.stereotype.Component;


@Component
public class RiskFactorEntityMapper {
    public RiskFactorConfiguration toDomain(RiskFactorConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }

        RiskTarget target = new RiskTarget(
                toDomainLevel(entity.getLevel()),
                entity.getReferenceId(),
                toDomainBuildingType(entity.getBuildingType())
        );

        return RiskFactorConfiguration.rehydrate(
                new RiskFactorConfigurationId(entity.getId()),
                target,
                AdjustmentPercentage.of(entity.getAdjustmentPercentage()),
                entity.isActive(),
                new AuditInfo(entity.getCreatedAt(), entity.getUpdatedAt())
        );
    }

    public RiskFactorConfigurationEntity toEntity(RiskFactorConfiguration domain) {
        if (domain == null) {
            return null;
        }

        return new RiskFactorConfigurationEntity(
                null,
                toEntityLevel(domain.getTarget().level()),
                domain.getTarget().referenceId(),
                toEntityBuildingType(domain.getTarget().buildingType()),
                domain.getPercentage().value(),
                domain.isActive()
        );
    }

    public void updateEntity(RiskFactorConfiguration domain, RiskFactorConfigurationEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setAdjustmentPercentage(domain.getPercentage().value());
        entity.setActive(domain.isActive());
    }

    private RiskLevel toDomainLevel(RiskLevelEntity entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity) {
            case COUNTRY -> RiskLevel.COUNTRY;
            case COUNTY -> RiskLevel.COUNTY;
            case CITY -> RiskLevel.CITY;
            case BUILDING_TYPE -> RiskLevel.BUILDING_TYPE;
        };
    }

    public RiskLevelEntity toEntityLevel(RiskLevel domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case COUNTRY -> RiskLevelEntity.COUNTRY;
            case COUNTY -> RiskLevelEntity.COUNTY;
            case CITY -> RiskLevelEntity.CITY;
            case BUILDING_TYPE -> RiskLevelEntity.BUILDING_TYPE;
        };
    }

    private BuildingType toDomainBuildingType(BuildingTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity) {
            case RESIDENTIAL -> BuildingType.RESIDENTIAL;
            case OFFICE -> BuildingType.OFFICE;
            case INDUSTRIAL -> BuildingType.INDUSTRIAL;
        };
    }

    public BuildingTypeEntity toEntityBuildingType(BuildingType domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case RESIDENTIAL -> BuildingTypeEntity.RESIDENTIAL;
            case OFFICE -> BuildingTypeEntity.OFFICE;
            case INDUSTRIAL -> BuildingTypeEntity.INDUSTRIAL;
        };
    }
}
