package com.example.insurance_app.application.dto.report;

import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.policy.PolicyStatus;
import com.example.insurance_app.domain.model.report.vo.ReportDateRange;

import java.time.LocalDate;

public record PolicyReportQuery(
    ReportGrouping grouping,
    LocalDate from,
    LocalDate to,
    PolicyStatus status,
    String currency,
    BuildingType buildingType
) {
    public static PolicyReportQuery from(ReportGrouping grouping, ReportFilterParams filters) {
        ReportDateRange dateRange = new ReportDateRange(filters.from(), filters.to());
        PolicyStatus status = filters.status() != null ? PolicyStatus.valueOf(filters.status()) : null;
        BuildingType buildingType = filters.buildingType() != null ? BuildingType.valueOf(filters.buildingType()) : null;

        return new PolicyReportQuery(
            grouping,
            dateRange.from(),
            dateRange.to(),
            status,
            filters.currency(),
            buildingType
        );
    }
}
