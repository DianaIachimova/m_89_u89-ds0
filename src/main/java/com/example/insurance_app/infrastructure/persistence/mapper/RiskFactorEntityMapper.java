package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskFactorConfiguration;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.AdjustmentPercentage;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskFactorConfigurationId;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskTarget;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskFactorConfigurationEntity;
import org.springframework.stereotype.Component;

@Component
public class RiskFactorEntityMapper {

    private final EnumEntityMapper enumEntityMapper;

    public RiskFactorEntityMapper(EnumEntityMapper enumEntityMapper) {
        this.enumEntityMapper = enumEntityMapper;
    }

    public RiskFactorConfiguration toDomain(RiskFactorConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }

        RiskTarget target = new RiskTarget(
                enumEntityMapper.toRiskLevel(entity.getLevel()),
                entity.getReferenceId(),
                enumEntityMapper.toBuildingType(entity.getBuildingType())
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
                enumEntityMapper.toRiskLevelEntity(domain.getTarget().level()),
                domain.getTarget().referenceId(),
                enumEntityMapper.toBuildingTypeEntity(domain.getTarget().buildingType()),
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

}
