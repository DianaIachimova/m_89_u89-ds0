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
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyDetailsEmbeddable;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import org.springframework.stereotype.Component;

@Component
public class PolicyEntityMapper {

    public Policy toDomain(PolicyEntity entity) {
        if (entity == null) return null;

        PolicyDetailsEmbeddable details = entity.getPolicyDetails();
        PolicyReferences refs = new PolicyReferences(
                new ClientId(entity.getClient().getId()),
                new BuildingId(entity.getBuilding().getId()),
                new BrokerId(entity.getBroker().getId()),
                new CurrencyId(entity.getCurrency().getId())
        );

        CancellationInfo cancellation = details.getCancelledAt() != null
                ? new CancellationInfo(details.getCancelledAt(), details.getCancellationReason())
                : null;

        PolicyIdentity identity = new PolicyIdentity(
                new PolicyId(entity.getId()),
                new PolicyNumber(entity.getPolicyNumber()));

        PolicyPremium premium = new PolicyPremium(
                new PremiumAmount(details.getBasePremium()),
                new PremiumAmount(details.getFinalPremium()));

        return Policy.rehydrate(
                identity,
                refs,
                toDomainStatus(entity.getStatus()),
                new PolicyPeriod(details.getStartDate(), details.getEndDate()),
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

        PolicyDetailsEmbeddable details = new PolicyDetailsEmbeddable(
                domain.getPeriod().startDate(),
                domain.getPeriod().endDate(),
                domain.getBasePremium().value(),
                domain.getFinalPremium().value(),
                null,
                null
        );

        return new PolicyEntity(
                domain.getPolicyNumber().value(),
                client, building, broker, currency,
                toEntityStatus(domain.getStatus()),
                details);
    }


    public void updateEntity(Policy domain, PolicyEntity entity) {
        if (domain == null || entity == null) return;

        entity.setStatus(toEntityStatus(domain.getStatus()));
        PolicyDetailsEmbeddable details = entity.getPolicyDetails();
        details.setFinalPremium(domain.getFinalPremium().value());

        if (domain.getCancellationInfo() != null) {
            details.setCancelledAt(domain.getCancellationInfo().cancelledAt());
            details.setCancellationReason(domain.getCancellationInfo().reason());
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
