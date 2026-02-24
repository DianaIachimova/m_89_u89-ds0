package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.policy.vo.*;
import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.broker.vo.BrokerId;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyId;
import com.example.insurance_app.domain.model.policy.Policy;
import com.example.insurance_app.domain.model.policy.PolicyStatus;
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

    private final EnumEntityMapper enumEntityMapper;

    public PolicyEntityMapper(EnumEntityMapper enumEntityMapper) {
        this.enumEntityMapper = enumEntityMapper;
    }

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
                enumEntityMapper.toPolicyStatus(entity.getStatus()),
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
                enumEntityMapper.toPolicyStatusEntity(domain.getStatus()),
                details);
    }


    public void updateEntity(Policy domain, PolicyEntity entity) {
        if (domain == null || entity == null) return;

        entity.setStatus(enumEntityMapper.toPolicyStatusEntity(domain.getStatus()));
        PolicyDetailsEmbeddable details = entity.getPolicyDetails();
        details.setFinalPremium(domain.getFinalPremium().value());

        if (domain.getCancellationInfo() != null) {
            details.setCancelledAt(domain.getCancellationInfo().cancelledAt());
            details.setCancellationReason(domain.getCancellationInfo().reason());
        }
    }

}
