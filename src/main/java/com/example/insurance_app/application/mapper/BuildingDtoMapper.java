package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingResponse;
import com.example.insurance_app.application.dto.geogrophy.CityResponse;
import com.example.insurance_app.application.dto.geogrophy.CountryResponse;
import com.example.insurance_app.application.dto.geogrophy.CountyResponse;
import com.example.insurance_app.domain.model.Building;
import com.example.insurance_app.domain.model.BuildingType;
import com.example.insurance_app.domain.model.vo.ClientId;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountryEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountyEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BuildingDtoMapper {

    public Building toDomain(CreateBuildingRequest request, UUID ownerId) {
        if (request == null) {
            return null;
        }

        ClientId clientId = ownerId != null ? new ClientId(ownerId) : null;
        BuildingType buildingType = toBuildingType(request.buildingType());

        return new Building(
                clientId,
                request.street(),
                request.streetNumber(),
                request.cityId(),
                request.constructionYear(),
                buildingType,
                request.numberOfFloors(),
                request.surfaceArea(),
                request.insuredValue(),
                request.floodZone(),
                request.earthquakeRiskZone()
        );
    }

    public BuildingResponse toResponse(Building domain, BuildingEntity entity) {
        if (domain == null || entity == null) {
            return null;
        }

        CityEntity cityEntity = entity.getCity();
        CountyEntity countyEntity = cityEntity != null ? cityEntity.getCounty() : null;
        CountryEntity countryEntity = countyEntity != null ? countyEntity.getCountry() : null;

        return new BuildingResponse(
                domain.getId() != null ? domain.getId().value() : null,
                domain.getOwnerId() != null ? domain.getOwnerId().value() : null,
                entity.getOwner() != null ? entity.getOwner().getName() : null,
                domain.getStreet(),
                domain.getStreetNumber(),
                toCityResponse(cityEntity),
                toCountyResponse(countyEntity),
                toCountryResponse(countryEntity),
                domain.getConstructionYear(),
                toBuildingTypeDto(domain.getBuildingType()),
                domain.getNumberOfFloors(),
                domain.getSurfaceArea(),
                domain.getInsuredValue(),
                domain.getFloodZone(),
                domain.getEarthquakeRiskZone(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
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

    private BuildingTypeDto toBuildingTypeDto(BuildingType domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case RESIDENTIAL -> BuildingTypeDto.RESIDENTIAL;
            case OFFICE -> BuildingTypeDto.OFFICE;
            case INDUSTRIAL -> BuildingTypeDto.INDUSTRIAL;
        };
    }

    private CityResponse toCityResponse(CityEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CityResponse(entity.getId(), entity.getName());
    }

    private CountyResponse toCountyResponse(CountyEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CountyResponse(entity.getId(), entity.getName(), entity.getCode());
    }

    private CountryResponse toCountryResponse(CountryEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CountryResponse(entity.getId(), entity.getName());
    }
}
