package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BuildingEntityMapper {

    private final BuildingAddressEmbeddableMapper addressMapper;
    private final BuildingInfoEmbeddableMapper buildingInfoMapper;
    private final BuildingRiskIndicatorsMapper riskIndicatorsMapper;

    public BuildingEntityMapper(BuildingAddressEmbeddableMapper addressMapper,
                                BuildingInfoEmbeddableMapper buildingInfoMapper,
                                BuildingRiskIndicatorsMapper riskIndicatorsMapper) {
        this.addressMapper = addressMapper;
        this.buildingInfoMapper = buildingInfoMapper;
        this.riskIndicatorsMapper = riskIndicatorsMapper;
    }

    public Building toDomain(BuildingEntity entity) {
        if (entity == null) return null;

        return new Building(
                new BuildingId(entity.getId()),
                new ClientId(extractOwnerId(entity)),
                addressMapper.toDomain(entity.getAddress()),
                extractCityId(entity),
                buildingInfoMapper.toDomain(entity.getBuildingInfo()),
                riskIndicatorsMapper.toDomain(entity.getRisk())
        );
    }

    public BuildingEntity toEntity(Building domain, ClientEntity owner, CityEntity city) {
        if (domain == null) return null;

        return new BuildingEntity(
                domain.getId() != null ? domain.getId().value() : null,
                owner,
                city,
                addressMapper.toEmbeddable(domain.getAddress()),
                buildingInfoMapper.toEmbeddable(domain.getBuildingInfo()),
                riskIndicatorsMapper.toEmbeddable(domain.getRiskIndicators())
        );
    }

    public void updateEntity(Building domain, BuildingEntity entity, CityEntity city) {
        if (domain == null || entity == null || city == null) return;
        entity.setCity(city);
        entity.setAddress(addressMapper.toEmbeddable(domain.getAddress()));
        entity.setBuildingInfo(buildingInfoMapper.toEmbeddable(domain.getBuildingInfo()));
        entity.setRisk(riskIndicatorsMapper.toEmbeddable(domain.getRiskIndicators()));
    }

    private static UUID extractOwnerId(BuildingEntity entity) {
        ClientEntity owner = entity.getOwner();
        return owner != null ? owner.getId() : null;
    }

    private static UUID extractCityId(BuildingEntity entity) {
        CityEntity city = entity.getCity();
        return city != null ? city.getId() : null;
    }
}
