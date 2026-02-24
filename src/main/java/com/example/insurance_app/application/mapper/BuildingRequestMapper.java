package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import com.example.insurance_app.application.dto.building.request.AddressRequest;
import com.example.insurance_app.application.dto.building.request.BuildingInfoRequest;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = EnumDtoMapper.class)
public interface BuildingRequestMapper {

    default Building toDomain(UUID ownerId, UUID cityId, CreateBuildingRequest request) {
        if (request == null) return null;
        return new Building(
                new ClientId(ownerId),
                toBuildingAddress(request.address()),
                cityId,
                toBuildingInfo(request.buildingDetails()),
                toRiskIndicators(request.riskIndicators())
        );
    }

    BuildingAddress toBuildingAddress(AddressRequest dto);

    @Mapping(target = "type", source = "buildingType")
    BuildingInfo toBuildingInfo(BuildingInfoRequest dto);

    @Mapping(target = "earthquakeZone", source = "earthquakeRiskZone")
    RiskIndicators toRiskIndicators(RiskIndicatorsDto dto);
}
