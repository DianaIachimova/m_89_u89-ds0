package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.Building;
import com.example.insurance_app.domain.model.BuildingType;
import com.example.insurance_app.domain.model.vo.BuildingId;
import com.example.insurance_app.domain.model.vo.ClientId;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.repository.client.ClientRepository;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CityRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BuildingEntityMapper {

    private final ClientRepository clientRepository;
    private final CityRepository cityRepository;

    public BuildingEntityMapper(ClientRepository clientRepository, CityRepository cityRepository) {
        this.clientRepository = clientRepository;
        this.cityRepository = cityRepository;
    }

    public Building toDomain(BuildingEntity entity) {
        if (entity == null) {
            return null;
        }

        BuildingId buildingId = entity.getId() != null ? new BuildingId(entity.getId()) : null;
        ClientId ownerId = entity.getOwner() != null && entity.getOwner().getId() != null
                ? new ClientId(entity.getOwner().getId())
                : null;
        UUID cityId = entity.getCity() != null ? entity.getCity().getId() : null;
        BuildingType buildingType = toBuildingType(entity.getBuildingType());

        return new Building(
                buildingId,
                ownerId,
                entity.getStreet(),
                entity.getStreetNumber(),
                cityId,
                entity.getConstructionYear(),
                buildingType,
                entity.getNumberOfFloors(),
                entity.getSurfaceArea(),
                entity.getInsuredValue(),
                entity.getFloodZone(),
                entity.getEarthquakeRiskZone()
        );
    }

    public BuildingEntity toEntity(Building domain) {
        if (domain == null) {
            return null;
        }

        ClientEntity owner = null;
        if (domain.getOwnerId() != null && domain.getOwnerId().value() != null) {
            owner = clientRepository.findById(domain.getOwnerId().value())
                    .orElseThrow(() -> new IllegalArgumentException("Client not found: " + domain.getOwnerId().value()));
        }

        CityEntity city = null;
        if (domain.getCityId() != null) {
            city = cityRepository.findById(domain.getCityId())
                    .orElseThrow(() -> new IllegalArgumentException("City not found: " + domain.getCityId()));
        }

        return new BuildingEntity(
                domain.getId() != null ? domain.getId().value() : null,
                owner,
                domain.getStreet(),
                domain.getStreetNumber(),
                city,
                domain.getConstructionYear(),
                toBuildingTypeEntity(domain.getBuildingType()),
                domain.getNumberOfFloors(),
                domain.getSurfaceArea(),
                domain.getInsuredValue(),
                domain.getFloodZone(),
                domain.getEarthquakeRiskZone()
        );
    }

    public void updateEntity(Building domain, BuildingEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setStreet(domain.getStreet());
        entity.setStreetNumber(domain.getStreetNumber());
        entity.setConstructionYear(domain.getConstructionYear());
        entity.setBuildingType(toBuildingTypeEntity(domain.getBuildingType()));
        entity.setNumberOfFloors(domain.getNumberOfFloors());
        entity.setSurfaceArea(domain.getSurfaceArea());
        entity.setInsuredValue(domain.getInsuredValue());
        entity.setFloodZone(domain.getFloodZone());
        entity.setEarthquakeRiskZone(domain.getEarthquakeRiskZone());

        // Update city if changed
        if (domain.getCityId() != null) {
            CityEntity city = cityRepository.findById(domain.getCityId())
                    .orElseThrow(() -> new IllegalArgumentException("City not found: " + domain.getCityId()));
            entity.setCity(city);
        }
    }

    private BuildingType toBuildingType(BuildingTypeEntity entity) {
        if (entity == null) {
            return null;
        }
        return switch (entity) {
            case RESIDENTIAL -> BuildingType.RESIDENTIAL;
            case OFFICE -> BuildingType.OFFICE;
            case INDUSTRIAL -> BuildingType.INDUSTRIAL;
        };
    }

    private BuildingTypeEntity toBuildingTypeEntity(BuildingType domain) {
        if (domain == null) {
            return null;
        }
        return switch (domain) {
            case RESIDENTIAL -> BuildingTypeEntity.RESIDENTIAL;
            case OFFICE -> BuildingTypeEntity.OFFICE;
            case INDUSTRIAL -> BuildingTypeEntity.INDUSTRIAL;
        };
    }
}
