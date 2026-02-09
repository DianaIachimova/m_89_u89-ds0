package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.broker.vo.BrokerId;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyId;
import com.example.insurance_app.domain.model.policy.Policy;
import com.example.insurance_app.domain.model.policy.PolicyStatus;
import com.example.insurance_app.domain.model.policy.vo.*;
import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import org.springframework.stereotype.Component;

@Component
public class PolicyEntityMapper {

    public Policy toDomain(PolicyEntity entity) {
        if (entity == null) return null;

        PolicyReferences refs = new PolicyReferences(
                new ClientId(entity.getClient().getId()),
                new BuildingId(entity.getBuilding().getId()),
                new BrokerId(entity.getBroker().getId()),
                new CurrencyId(entity.getCurrency().getId())
        );

        CancellationInfo cancellation = entity.getCancelledAt() != null
                ? new CancellationInfo(entity.getCancelledAt(), entity.getCancellationReason())
                : null;

        PolicyIdentity identity = new PolicyIdentity(
                new PolicyId(entity.getId()),
                new PolicyNumber(entity.getPolicyNumber()));

        PolicyPremium premium = new PolicyPremium(
                new PremiumAmount(entity.getBasePremium()),
                new PremiumAmount(entity.getFinalPremium()));

        return Policy.rehydrate(
                identity,
                refs,
                toDomainStatus(entity.getStatus()),
                new PolicyPeriod(entity.getStartDate(), entity.getEndDate()),
                premium,
                cancellation,
                new AuditInfo(entity.getCreatedAt(), entity.getUpdatedAt())
        );
    }

    public PolicyEntity toEntity(Policy domain,
                                 ClientEntity client,
                                 BuildingEntity building,
                                 BrokerEntity broker,
                                 CurrencyEntity currency) {
        if (domain == null) return null;

        PolicyEntity entity = new PolicyEntity(
                domain.getPolicyNumber().value(),
                client, building, broker, currency,
                toEntityStatus(domain.getStatus()));
        entity.setStartDate(domain.getPeriod().startDate());
        entity.setEndDate(domain.getPeriod().endDate());
        entity.setBasePremium(domain.getBasePremium().value());
        entity.setFinalPremium(domain.getFinalPremium().value());
        return entity;
    }


    public void updateEntity(Policy domain, PolicyEntity entity) {
        if (domain == null || entity == null) return;

        entity.setStatus(toEntityStatus(domain.getStatus()));
        entity.setFinalPremium(domain.getFinalPremium().value());

        if (domain.getCancellationInfo() != null) {
            entity.setCancelledAt(domain.getCancellationInfo().cancelledAt());
            entity.setCancellationReason(domain.getCancellationInfo().reason());
        }
    }

    private PolicyStatus toDomainStatus(PolicyStatusEntity entity) {
        return switch (entity) {
            case DRAFT -> PolicyStatus.DRAFT;
            case ACTIVE -> PolicyStatus.ACTIVE;
            case EXPIRED -> PolicyStatus.EXPIRED;
            case CANCELLED -> PolicyStatus.CANCELLED;
        };
    }

    private PolicyStatusEntity toEntityStatus(PolicyStatus domain) {
        return switch (domain) {
            case DRAFT -> PolicyStatusEntity.DRAFT;
            case ACTIVE -> PolicyStatusEntity.ACTIVE;
            case EXPIRED -> PolicyStatusEntity.EXPIRED;
            case CANCELLED -> PolicyStatusEntity.CANCELLED;
        };
    }
}
