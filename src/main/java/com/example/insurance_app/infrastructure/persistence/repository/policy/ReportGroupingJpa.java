package com.example.insurance_app.infrastructure.persistence.repository.policy;

import com.example.insurance_app.application.dto.report.ReportGrouping;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

public final class ReportGroupingJpa {

    private ReportGroupingJpa() {}

    public static Expression<String> resolve(
            ReportGrouping grouping,
            Root<PolicyEntity> policy,
            Join<PolicyEntity, BuildingEntity> building) {
        return switch (grouping) {
            case BY_COUNTRY -> building.join("city").join("county").join("country").get("name");
            case BY_COUNTY -> building.join("city").join("county").get("name");
            case BY_CITY -> building.join("city").get("name");
            case BY_BROKER -> policy.join("broker").get("name");
        };
    }
}
