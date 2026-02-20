package com.example.insurance_app.domain.model.report.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.time.LocalDate;

public record ReportDateRange(LocalDate from, LocalDate to) {
    public ReportDateRange {
        DomainAssertions.notNull(from, "From date");
        DomainAssertions.notNull(to, "To date");
        DomainAssertions.check(!from.isAfter(to), "From date must not be after to date");
    }
}
