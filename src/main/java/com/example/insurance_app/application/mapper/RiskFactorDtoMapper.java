package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.metadata.riskfactors.request.CreateRiskFactorRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.response.RiskFactorResponse;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskFactorConfiguration;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.AdjustmentPercentage;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskTarget;
import org.springframework.stereotype.Component;

@Component
public class RiskFactorDtoMapper {

    private final EnumDtoMapper enumDtoMapper;

    public RiskFactorDtoMapper(EnumDtoMapper enumDtoMapper) {
        this.enumDtoMapper = enumDtoMapper;
    }

    public RiskFactorConfiguration toDomain(CreateRiskFactorRequest req) {
        if (req == null) {
            return null;
        }

        RiskTarget target = new RiskTarget(
                enumDtoMapper.toRiskLevel(req.level()),
                req.referenceId(),
                enumDtoMapper.toBuildingType(req.buildingType())
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
                enumDtoMapper.toRiskLevelDto(domain.getTarget().level()),
                domain.getTarget().referenceId(),
                enumDtoMapper.toBuildingTypeDto(domain.getTarget().buildingType()),
                domain.getPercentage().value(),
                domain.isActive(),
                domain.getAudit().createdAt(),
                domain.getAudit().updatedAt()
        );
    }

}
