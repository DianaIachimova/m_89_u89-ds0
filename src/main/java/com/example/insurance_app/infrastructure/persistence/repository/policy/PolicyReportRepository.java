package com.example.insurance_app.infrastructure.persistence.repository.policy;

import com.example.insurance_app.application.dto.report.PolicyReportQuery;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.repository.policy.projection.PolicyReportProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.example.insurance_app.infrastructure.persistence.mapper.EnumEntityMapper.toBuildingTypeEntity;
import static com.example.insurance_app.infrastructure.persistence.mapper.EnumEntityMapper.toPolicyStatusEntity;

@Repository
public class PolicyReportRepository {

    private final EntityManager entityManager;

    public PolicyReportRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<PolicyReportProjection> generateReport(PolicyReportQuery query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PolicyReportProjection> cq = cb.createQuery(PolicyReportProjection.class);
        Root<PolicyEntity> policy = cq.from(PolicyEntity.class);

        Join<PolicyEntity, BuildingEntity> building = policy.join("building", JoinType.INNER);
        Join<PolicyEntity, CurrencyEntity> currency = policy.join("currency", JoinType.INNER);

        Expression<String> groupKey = ReportGroupingJpa.resolve(query.grouping(), policy, building);
        Expression<String> currencyCodeExpr = currency.get("code");

        Expression<Long> policyCount = cb.count(policy);
        Expression<BigDecimal> totalFinalPremium = cb.sum(policy.get("policyDetails").get("finalPremium"));
        Expression<BigDecimal> totalFinalPremiumInBaseCurrency = cb.sum(
            cb.prod(
                policy.get("policyDetails").get("finalPremium"),
                currency.get("exchangeRateToBase")
            )
        );

        cq.select(cb.construct(
            PolicyReportProjection.class,
            groupKey,
            currencyCodeExpr,
            policyCount,
            totalFinalPremium,
            totalFinalPremiumInBaseCurrency
        ));

        List<Predicate> predicates = buildPredicates(cb, policy, building, query);
        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        cq.groupBy(groupKey, currencyCodeExpr);
        cq.orderBy(cb.asc(groupKey), cb.asc(currencyCodeExpr));

        return entityManager.createQuery(cq).getResultList();
    }

    private List<Predicate> buildPredicates(
            CriteriaBuilder cb,
            Root<PolicyEntity> policy,
            Join<PolicyEntity, BuildingEntity> building,
            PolicyReportQuery query) {

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.greaterThanOrEqualTo(policy.get("policyDetails").get("startDate"), query.from()));
        predicates.add(cb.lessThanOrEqualTo(policy.get("policyDetails").get("endDate"), query.to()));

        if (query.status() != null) {
            predicates.add(cb.equal(policy.get("status"), toPolicyStatusEntity(query.status())));
        }

        if (query.currency() != null) {
            predicates.add(cb.equal(policy.join("currency").get("code"), query.currency()));
        }

        if (query.buildingType() != null) {
            predicates.add(cb.equal(building.get("buildingInfo").get("buildingType"), toBuildingTypeEntity(query.buildingType())));
        }

        return predicates;
    }
}