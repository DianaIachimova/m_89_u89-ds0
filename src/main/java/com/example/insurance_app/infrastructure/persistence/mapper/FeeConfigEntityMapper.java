package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfiguration;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeDetails;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.*;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.ValidityPeriodEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class FeeConfigEntityMapper {

    public FeeConfiguration toDomain(FeeConfigurationEntity entity) {
        if (entity == null) {
            return null;
        }
        FeeDetails details = new FeeDetails(
                FeeCode.of(entity.getCode()),
                FeeName.of(entity.getName()),
                toFeeConfigurationType(entity.getType()),
                FeePercentage.of(entity.getPercentage()),
                toEffectivePeriod(entity.getPeriod())
        );
        return FeeConfiguration.rehydrate(
                new FeeConfigurationId(entity.getId()),
                details,
                entity.isActive(),
                new AuditInfo(entity.getCreatedAt(), entity.getUpdatedAt()));
    }

    public FeeConfigurationEntity toEntity(FeeConfiguration domain) {
        if (domain == null) {
            return null;
        }
        return new FeeConfigurationEntity(
                null,
                domain.getDetails().code().value(),
                domain.getDetails().name().value(),
                toFeeConfigTypeEntity(domain.getDetails().type()),
                domain.getDetails().percentage().value(),
                toValidityPeriodEmbeddable(domain.getDetails().period()),
                domain.isActive()
        );
    }

    public void updateEntity(FeeConfiguration domain, FeeConfigurationEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setName(domain.getDetails().name().value());
        entity.setPercentage(domain.getDetails().percentage().value());
        entity.setPeriod(
                toValidityPeriodEmbeddable(domain.getDetails().period()));
        entity.setActive(domain.isActive());
    }


    private EffectivePeriod toEffectivePeriod(ValidityPeriodEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return EffectivePeriod.of(embeddable.getEffectiveFrom(), embeddable.getEffectiveTo());
    }

    private ValidityPeriodEmbeddable toValidityPeriodEmbeddable(EffectivePeriod domain) {
        if (domain == null) {
            return null;
        }
        return new ValidityPeriodEmbeddable(domain.from(), domain.to());
    }


    private FeeConfigurationType toFeeConfigurationType(FeeConfigTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity) {
            case BROKER_COMMISSION -> FeeConfigurationType.BROKER_COMMISSION;
            case RISK_ADJUSTMENT -> FeeConfigurationType.RISK_ADJUSTMENT;
            case ADMIN_FEE -> FeeConfigurationType.ADMIN_FEE;
        };
    }

    public FeeConfigTypeEntity toFeeConfigTypeEntity(FeeConfigurationType domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case BROKER_COMMISSION -> FeeConfigTypeEntity.BROKER_COMMISSION;
            case RISK_ADJUSTMENT -> FeeConfigTypeEntity.RISK_ADJUSTMENT;
            case ADMIN_FEE -> FeeConfigTypeEntity.ADMIN_FEE;
        };
    }
}
