package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.client.ClientType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import com.example.insurance_app.domain.model.policy.PolicyStatus;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskLevelEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnumEntityMapper {

    BuildingTypeEntity toBuildingTypeEntity(BuildingType domain);

    BuildingType toBuildingType(BuildingTypeEntity entity);

    ClientTypeEntity toClientTypeEntity(ClientType domain);

    ClientType toClientType(ClientTypeEntity entity);

    RiskLevelEntity toRiskLevelEntity(RiskLevel domain);

    RiskLevel toRiskLevel(RiskLevelEntity entity);

    FeeConfigTypeEntity toFeeConfigTypeEntity(FeeConfigurationType domain);

    FeeConfigurationType toFeeConfigurationType(FeeConfigTypeEntity entity);

    PolicyStatusEntity toPolicyStatusEntity(PolicyStatus domain);

    PolicyStatus toPolicyStatus(PolicyStatusEntity entity);
}
