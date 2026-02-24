package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.client.ClientTypeDto;
import com.example.insurance_app.application.dto.metadata.feeconfig.FeeConfigTypeDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskLevelDto;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.client.ClientType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnumDtoMapper {

    BuildingTypeDto toBuildingTypeDto(BuildingType domain);

    BuildingType toBuildingType(BuildingTypeDto dto);

    ClientTypeDto toClientTypeDto(ClientType domain);

    ClientType toClientType(ClientTypeDto dto);

    RiskLevelDto toRiskLevelDto(RiskLevel domain);

    RiskLevel toRiskLevel(RiskLevelDto dto);

    FeeConfigTypeDto toFeeConfigTypeDto(FeeConfigurationType domain);

    FeeConfigurationType toFeeConfigurationType(FeeConfigTypeDto dto);
}
