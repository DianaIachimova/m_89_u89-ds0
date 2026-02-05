package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.infrastructure.persistence.entity.building.*;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class BuildingEntityMapper {

    public Building toDomain(BuildingEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Building(
                new BuildingId(entity.getId()),
                new ClientId(extractOwnerId(entity)),
                toAddress(entity.getAddress()),
                extractCityId(entity),
                toBuildingInfo(entity.getBuildingInfo()),
                toRiskIndicators(entity.getRisk())
        );
    }

    private static UUID extractOwnerId(BuildingEntity entity) {
        ClientEntity owner = entity.getOwner();
        return owner != null ? owner.getId() : null;
    }

    private static UUID extractCityId(BuildingEntity entity) {
        CityEntity city = entity.getCity();
        return city != null ? city.getId() : null;
    }

    public BuildingEntity toEntity(Building domain, ClientEntity owner, CityEntity city) {
        if (domain == null) {
            return null;
        }

        return new BuildingEntity(
                domain.getId() != null ? domain.getId().value() : null,
                owner,
                city,
                toAddressEmbeddable(domain.getAddress()),
                toBuildingInfoEmbeddable(domain.getBuildingInfo()),
                toRiskIndicatorsEmbeddable(domain.getRiskIndicators())

        );
    }

    public void updateEntity(Building domain, BuildingEntity entity, CityEntity city) {
        if (domain == null || entity == null || city == null) {
            return;
        }
        entity.setCity(city);
        entity.setAddress(toAddressEmbeddable(domain.getAddress()));
        entity.setBuildingInfo(toBuildingInfoEmbeddable(domain.getBuildingInfo()));
        entity.setRisk(toRiskIndicatorsEmbeddable(domain.getRiskIndicators()));
    }

    private BuildingInfo toBuildingInfo(BuildingInfoEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }

        return new BuildingInfo(
                embeddable.getConstructionYear(),
                toBuildingType(embeddable.getBuildingType()),
                embeddable.getNumberOfFloors(),
                embeddable.getSurfaceArea(),
                embeddable.getInsuredValue()
        );
    }

    private BuildingInfoEmbeddable toBuildingInfoEmbeddable(BuildingInfo info) {
        if (info == null) {
            return null;
        }

        return new BuildingInfoEmbeddable(
                info.constructionYear(),
                toBuildingTypeEntity(info.type()),
                info.numberOfFloors(),
                info.surfaceArea(),
                info.insuredValue()
        );
    }

    private AddressEmbeddable toAddressEmbeddable(BuildingAddress address) {
        if (address == null) {
            return null;
        }
        return new AddressEmbeddable(
                address.street(),
                address.streetNumber()
        );
    }

    private BuildingAddress toAddress(AddressEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return new BuildingAddress(
                embeddable.getStreet(),
                embeddable.getStreetNumber()
        );
    }

    private RiskIndicators toRiskIndicators(RiskIndicatorsEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return new RiskIndicators(
                embeddable.isFloodZone(),
                embeddable.isEarthquakeRiskZone()
        );
    }

    private RiskIndicatorsEmbeddable toRiskIndicatorsEmbeddable(RiskIndicators risk) {
        if (risk == null) {
            return null;
        }
        return new RiskIndicatorsEmbeddable(
                risk.floodZone(),
                risk.earthquakeZone()
        );
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
