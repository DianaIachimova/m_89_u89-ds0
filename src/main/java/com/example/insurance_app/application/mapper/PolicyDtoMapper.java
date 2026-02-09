package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.policy.response.PolicyResponse;
import com.example.insurance_app.application.dto.policy.response.PolicySummaryResponse;
import com.example.insurance_app.domain.model.policy.Policy;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import org.springframework.stereotype.Component;

@Component
public class PolicyDtoMapper {

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
}
