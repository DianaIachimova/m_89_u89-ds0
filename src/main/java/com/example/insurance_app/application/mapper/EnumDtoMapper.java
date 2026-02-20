package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.client.ClientTypeDto;
import com.example.insurance_app.application.dto.metadata.feeconfig.FeeConfigTypeDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskLevelDto;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.client.ClientType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;

public final class EnumDtoMapper {

    private EnumDtoMapper() {}

    public static BuildingTypeDto toBuildingTypeDto(BuildingType domain) {
        if (domain == null) return null;
        return switch (domain) {
            case RESIDENTIAL -> BuildingTypeDto.RESIDENTIAL;
            case OFFICE -> BuildingTypeDto.OFFICE;
            case INDUSTRIAL -> BuildingTypeDto.INDUSTRIAL;
        };
    }

    public static BuildingType toBuildingType(BuildingTypeDto dto) {
        if (dto == null) return null;
        return switch (dto) {
            case RESIDENTIAL -> BuildingType.RESIDENTIAL;
            case OFFICE -> BuildingType.OFFICE;
            case INDUSTRIAL -> BuildingType.INDUSTRIAL;
        };
    }

    public static ClientTypeDto toClientTypeDto(ClientType domain) {
        if (domain == null) return null;
        return switch (domain) {
            case INDIVIDUAL -> ClientTypeDto.INDIVIDUAL;
            case COMPANY -> ClientTypeDto.COMPANY;
        };
    }

    public static ClientType toClientType(ClientTypeDto dto) {
        if (dto == null) return null;
        return switch (dto) {
            case INDIVIDUAL -> ClientType.INDIVIDUAL;
            case COMPANY -> ClientType.COMPANY;
        };
    }

    public static RiskLevelDto toRiskLevelDto(RiskLevel domain) {
        if (domain == null) return null;
        return switch (domain) {
            case COUNTRY -> RiskLevelDto.COUNTRY;
            case COUNTY -> RiskLevelDto.COUNTY;
            case CITY -> RiskLevelDto.CITY;
            case BUILDING_TYPE -> RiskLevelDto.BUILDING_TYPE;
        };
    }

    public static RiskLevel toRiskLevel(RiskLevelDto dto) {
        if (dto == null) return null;
        return switch (dto) {
            case BUILDING_TYPE -> RiskLevel.BUILDING_TYPE;
            case COUNTRY -> RiskLevel.COUNTRY;
            case COUNTY -> RiskLevel.COUNTY;
            case CITY -> RiskLevel.CITY;
        };
    }

    public static FeeConfigTypeDto toFeeConfigTypeDto(FeeConfigurationType domain) {
        if (domain == null) return null;
        return switch (domain) {
            case BROKER_COMMISSION -> FeeConfigTypeDto.BROKER_COMMISSION;
            case RISK_ADJUSTMENT -> FeeConfigTypeDto.RISK_ADJUSTMENT;
            case ADMIN_FEE -> FeeConfigTypeDto.ADMIN_FEE;
        };
    }

    public static FeeConfigurationType toFeeConfigurationType(FeeConfigTypeDto dto) {
        if (dto == null) return null;
        return switch (dto) {
            case BROKER_COMMISSION -> FeeConfigurationType.BROKER_COMMISSION;
            case RISK_ADJUSTMENT -> FeeConfigurationType.RISK_ADJUSTMENT;
            case ADMIN_FEE -> FeeConfigurationType.ADMIN_FEE;
        };
    }
}
