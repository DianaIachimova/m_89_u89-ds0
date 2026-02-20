package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.client.ClientType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskLevelEntity;

public final class EnumEntityMapper {

    private EnumEntityMapper() {}

    public static BuildingTypeEntity toBuildingTypeEntity(BuildingType domain) {
        if (domain == null) return null;
        return switch (domain) {
            case RESIDENTIAL -> BuildingTypeEntity.RESIDENTIAL;
            case OFFICE -> BuildingTypeEntity.OFFICE;
            case INDUSTRIAL -> BuildingTypeEntity.INDUSTRIAL;
        };
    }

    public static BuildingType toBuildingType(BuildingTypeEntity entity) {
        if (entity == null) return null;
        return switch (entity) {
            case RESIDENTIAL -> BuildingType.RESIDENTIAL;
            case OFFICE -> BuildingType.OFFICE;
            case INDUSTRIAL -> BuildingType.INDUSTRIAL;
        };
    }

    public static ClientTypeEntity toClientTypeEntity(ClientType domain) {
        if (domain == null) return null;
        return switch (domain) {
            case INDIVIDUAL -> ClientTypeEntity.INDIVIDUAL;
            case COMPANY -> ClientTypeEntity.COMPANY;
        };
    }

    public static ClientType toClientType(ClientTypeEntity entity) {
        if (entity == null) return null;
        return switch (entity) {
            case INDIVIDUAL -> ClientType.INDIVIDUAL;
            case COMPANY -> ClientType.COMPANY;
        };
    }

    public static RiskLevelEntity toRiskLevelEntity(RiskLevel domain) {
        if (domain == null) return null;
        return switch (domain) {
            case COUNTRY -> RiskLevelEntity.COUNTRY;
            case COUNTY -> RiskLevelEntity.COUNTY;
            case CITY -> RiskLevelEntity.CITY;
            case BUILDING_TYPE -> RiskLevelEntity.BUILDING_TYPE;
        };
    }

    public static RiskLevel toRiskLevel(RiskLevelEntity entity) {
        if (entity == null) return null;
        return switch (entity) {
            case COUNTRY -> RiskLevel.COUNTRY;
            case COUNTY -> RiskLevel.COUNTY;
            case CITY -> RiskLevel.CITY;
            case BUILDING_TYPE -> RiskLevel.BUILDING_TYPE;
        };
    }

    public static FeeConfigTypeEntity toFeeConfigTypeEntity(FeeConfigurationType domain) {
        if (domain == null) return null;
        return switch (domain) {
            case BROKER_COMMISSION -> FeeConfigTypeEntity.BROKER_COMMISSION;
            case RISK_ADJUSTMENT -> FeeConfigTypeEntity.RISK_ADJUSTMENT;
            case ADMIN_FEE -> FeeConfigTypeEntity.ADMIN_FEE;
        };
    }

    public static FeeConfigurationType toFeeConfigurationType(FeeConfigTypeEntity entity) {
        if (entity == null) return null;
        return switch (entity) {
            case BROKER_COMMISSION -> FeeConfigurationType.BROKER_COMMISSION;
            case RISK_ADJUSTMENT -> FeeConfigurationType.RISK_ADJUSTMENT;
            case ADMIN_FEE -> FeeConfigurationType.ADMIN_FEE;
        };
    }
}
