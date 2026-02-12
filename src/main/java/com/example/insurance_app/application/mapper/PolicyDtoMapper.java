package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.building.response.BuildingDetailedResponse;
import com.example.insurance_app.application.dto.client.response.ClientResponse;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;
import com.example.insurance_app.application.dto.policy.response.PolicyDetailResponse;
import com.example.insurance_app.application.dto.policy.response.PolicyResponse;
import com.example.insurance_app.application.dto.policy.response.PolicySummaryResponse;
import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.client.Client;
import com.example.insurance_app.domain.model.policy.Policy;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountryEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.CurrencyEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PolicyDtoMapper {
    private final ClientDtoMapper clientDtoMapper;
    private final BuildingResponseMapper buildingResponseMapper;
    private final CurrencyDtoMapper currencyDtoMapper;
    private final CurrencyEntityMapper currencyEntityMapper;

    public PolicyDtoMapper(ClientDtoMapper clientDtoMapper,
                           BuildingResponseMapper buildingResponseMapper,
                           CurrencyDtoMapper currencyDtoMapper,
                           CurrencyEntityMapper currencyEntityMapper) {
        this.clientDtoMapper = clientDtoMapper;
        this.buildingResponseMapper = buildingResponseMapper;
        this.currencyDtoMapper = currencyDtoMapper;
        this.currencyEntityMapper = currencyEntityMapper;
    }

    public PolicyResponse toResponse(Policy domain, String currencyCode) {
        if (domain == null) return null;

        return new PolicyResponse(
                domain.getId() != null ? domain.getId().value() : null,
                domain.getPolicyNumber().value(),
                domain.getStatus().name(),
                domain.getReferences().clientId().value(),
                domain.getReferences().buildingId().value(),
                domain.getReferences().brokerId().value(),
                currencyCode,
                domain.getPeriod().startDate(),
                domain.getPeriod().endDate(),
                domain.getBasePremium().value(),
                domain.getFinalPremium().value(),
                domain.getCancellationInfo() != null ? domain.getCancellationInfo().cancelledAt() : null,
                domain.getCancellationInfo() != null ? domain.getCancellationInfo().reason() : null,
                domain.getAudit() != null ? domain.getAudit().createdAt() : null,
                domain.getAudit() != null ? domain.getAudit().updatedAt() : null
        );
    }

    public PolicySummaryResponse toSummaryResponse(PolicyEntity entity) {
        if (entity == null) return null;

        return new PolicySummaryResponse(
                entity.getId(),
                entity.getPolicyNumber(),
                entity.getStatus().name(),
                entity.getClient().getId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getFinalPremium(),
                entity.getCurrency().getCode()
        );
    }

    public PolicyDetailResponse toDetailResponse(Policy domain,
                                                 CurrencyEntity currencyEntity,
                                                 Client clientDomain,
                                                 ClientEntity clientEntity,
                                                 Building buildingDomain,
                                                 CityEntity city,
                                                 CountyEntity county,
                                                 CountryEntity country) {
        if (domain == null) return null;

        ClientResponse clientResponse = clientDtoMapper.toResponse(clientDomain, clientEntity, List.of());
        BuildingDetailedResponse buildingResponse = buildingResponseMapper.toDetailedResponse(
                buildingDomain, city, county, country);
        CurrencyResponse currencyResponse = currencyDtoMapper.toResponse(currencyEntityMapper.toDomain(currencyEntity));

        return new PolicyDetailResponse(
                domain.getId() != null ? domain.getId().value() : null,
                domain.getPolicyNumber().value(),
                domain.getStatus().name(),
                clientResponse,
                buildingResponse,
                domain.getReferences().brokerId().value(),
                currencyResponse,
                domain.getPeriod().startDate(),
                domain.getPeriod().endDate(),
                domain.getBasePremium().value(),
                domain.getFinalPremium().value(),
                domain.getCancellationInfo() != null ? domain.getCancellationInfo().cancelledAt() : null,
                domain.getCancellationInfo() != null ? domain.getCancellationInfo().reason() : null,
                domain.getAudit() != null ? domain.getAudit().createdAt() : null,
                domain.getAudit() != null ? domain.getAudit().updatedAt() : null
        );
    }
}

