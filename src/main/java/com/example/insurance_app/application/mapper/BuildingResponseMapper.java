package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.building.response.*;
import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import com.example.insurance_app.application.dto.geography.CityResponse;
import com.example.insurance_app.application.dto.geography.CountryResponse;
import com.example.insurance_app.application.dto.geography.CountyResponse;
import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountryEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountyEntity;
import org.springframework.stereotype.Component;

@Component
public class BuildingResponseMapper {

    private final EnumDtoMapper enumDtoMapper;
    private final GeographyMapper geographyMapper;

    public BuildingResponseMapper(EnumDtoMapper enumDtoMapper, GeographyMapper geographyMapper) {
        this.enumDtoMapper = enumDtoMapper;
        this.geographyMapper = geographyMapper;
    }

    public BuildingSummaryResponse toSummaryResponse(Building domain, CityEntity cityEntity) {
        if (domain == null) return null;

        return new BuildingSummaryResponse(
                domain.getId().value(),
                toAddressSummary(domain.getAddress(), geographyMapper.toDto(cityEntity)),
                toBuildingInfoResponse(domain.getBuildingInfo()),
                toRiskIndicatorsDto(domain.getRiskIndicators())
        );
    }

    public BuildingDetailedResponse toDetailedResponse(
            Building domain,
            CityEntity city,
            CountyEntity county,
            CountryEntity country
    ) {
        if (domain == null) return null;

        return new BuildingDetailedResponse(
                domain.getId().value(),
                domain.getOwnerId().value(),
                toAddressDetailed(
                        domain.getAddress(),
                        geographyMapper.toDto(city),
                        geographyMapper.toDto(county),
                        geographyMapper.toDto(country)),
                toBuildingInfoResponse(domain.getBuildingInfo()),
                toRiskIndicatorsDto(domain.getRiskIndicators())
        );
    }


    private AddressDetailedResponse toAddressDetailed(
            BuildingAddress address,
            CityResponse city,
            CountyResponse county,
            CountryResponse country
    ) {
        if (address == null) return null;
        return new AddressDetailedResponse(address.street(), address.streetNumber(), city, county, country);
    }


    private AddressSummaryResponse toAddressSummary(BuildingAddress address, CityResponse city) {
        if (address == null) return null;
        return new AddressSummaryResponse(address.street(), address.streetNumber(), city);
    }

    private BuildingInfoResponse toBuildingInfoResponse(BuildingInfo domain) {
        if(domain == null) {
            return null;
        }
        return new BuildingInfoResponse(
                domain.constructionYear(),
                enumDtoMapper.toBuildingTypeDto(domain.type()),
                domain.numberOfFloors(),
                domain.surfaceArea(),
                domain.insuredValue()
        );
    }

    private RiskIndicatorsDto toRiskIndicatorsDto(RiskIndicators risk) {
        if (risk == null) {
            return null;
        }

        return new RiskIndicatorsDto(
                risk.floodZone(),
                risk.earthquakeZone()
        );
    }

}
