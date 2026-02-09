package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskLevelDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.CreateRiskFactorRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.response.RiskFactorResponse;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskFactorConfiguration;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.AdjustmentPercentage;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskTarget;
import org.springframework.stereotype.Component;

@Component
public class RiskFactorDtoMapper {

    public RiskFactorConfiguration toDomain(CreateRiskFactorRequest req) {
        if (req == null) {
            return null;
        }

        RiskTarget target = new RiskTarget(
                toDomainLevel(req.level()),
                req.referenceId(),
                toDomainBuildingType(req.buildingType())
        );

        return RiskFactorConfiguration.createNew(
                target,
                new AdjustmentPercentage(req.adjustmentPercentage()),
                req.active()
        );
    }

    public RiskFactorResponse toResponse(RiskFactorConfiguration domain) {
        if (domain == null) {
            return null;
        }

        return new RiskFactorResponse(
                domain.getId() != null ? domain.getId().value() : null,
                toDtoLevel(domain.getTarget().level()),
                domain.getTarget().referenceId(),
                toDtoBuildingType(domain.getTarget().buildingType()),
                domain.getPercentage().value(),
                domain.isActive(),
                domain.getAudit().createdAt(),
                domain.getAudit().updatedAt()
        );
    }

    public RiskLevel toDomainLevel(RiskLevelDto dto) {
        if (dto == null)
            return null;

        return switch (dto) {
            case BUILDING_TYPE -> RiskLevel.BUILDING_TYPE;
            case COUNTRY -> RiskLevel.COUNTRY;
            case COUNTY -> RiskLevel.COUNTY;
            case CITY -> RiskLevel.CITY;
        };
    }

    private RiskLevelDto toDtoLevel(RiskLevel domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case COUNTRY -> RiskLevelDto.COUNTRY;
            case COUNTY -> RiskLevelDto.COUNTY;
            case CITY -> RiskLevelDto.CITY;
            case BUILDING_TYPE -> RiskLevelDto.BUILDING_TYPE;
        };
    }

    private BuildingType toDomainBuildingType(BuildingTypeDto dto) {
        if (dto == null) {
            return null;
        }
        return switch (dto) {
            case RESIDENTIAL -> BuildingType.RESIDENTIAL;
            case OFFICE -> BuildingType.OFFICE;
            case INDUSTRIAL -> BuildingType.INDUSTRIAL;
        };
    }

    private BuildingTypeDto toDtoBuildingType(BuildingType domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case RESIDENTIAL -> BuildingTypeDto.RESIDENTIAL;
            case OFFICE -> BuildingTypeDto.OFFICE;
            case INDUSTRIAL -> BuildingTypeDto.INDUSTRIAL;
        };
    }
}
