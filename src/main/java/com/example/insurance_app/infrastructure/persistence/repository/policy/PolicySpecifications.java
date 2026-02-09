package com.example.insurance_app.infrastructure.persistence.repository.policy;

import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PolicySpecifications {

    private PolicySpecifications() {
    }

    public static Specification<PolicyEntity> withFilters(UUID clientId,
                                                          UUID brokerId,
                                                          String status,
                                                          LocalDate startFrom,
                                                          LocalDate startTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (clientId != null) {
                predicates.add(cb.equal(root.get("client").get("id"), clientId));
            }
            if (brokerId != null) {
                predicates.add(cb.equal(root.get("broker").get("id"), brokerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), PolicyStatusEntity.valueOf(status)));
            }
            if (startFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), startFrom));
            }
            if (startTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), startTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
