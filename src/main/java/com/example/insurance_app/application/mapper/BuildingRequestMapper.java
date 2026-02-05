package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import com.example.insurance_app.application.dto.building.request.AddressRequest;
import com.example.insurance_app.application.dto.building.request.BuildingInfoRequest;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BuildingRequestMapper {
    public Building toDomain(UUID ownerId, UUID cityId, CreateBuildingRequest request) {
        if (request == null) return null;

        return new Building(
                new ClientId(ownerId),
                toBuildingAddress(request.address()),
                cityId,
                toBuildingInfo(request.buildingDetails()),
                toRiskIndicators(request.riskIndicators())
        );
    }

    public BuildingAddress toBuildingAddress(AddressRequest dto) {
        if (dto == null) return null;
        return new BuildingAddress(dto.street(), dto.streetNumber());
    }

    public BuildingInfo toBuildingInfo(BuildingInfoRequest dto) {
        if (dto == null) return null;

        return new BuildingInfo(
                dto.constructionYear(),
                toBuildingType(dto.buildingType()),
                dto.numberOfFloors(),
                dto.surfaceArea(),
                dto.insuredValue()
        );
    }

    public RiskIndicators toRiskIndicators(RiskIndicatorsDto dto) {
        if (dto == null) return null;
        return new RiskIndicators(dto.floodZone(), dto.earthquakeRiskZone());
    }

    public BuildingType toBuildingType(BuildingTypeDto dto) {
        if (dto == null) {
            return null;
        }
        return switch (dto) {
            case RESIDENTIAL -> BuildingType.RESIDENTIAL;
            case OFFICE -> BuildingType.OFFICE;
            case INDUSTRIAL -> BuildingType.INDUSTRIAL;
        };
    }





}
