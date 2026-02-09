package com.example.insurance_app.domain.model.policy.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.time.LocalDate;

public record PolicyPeriod(LocalDate startDate, LocalDate endDate) {
    public PolicyPeriod {
        DomainAssertions.notNull(startDate, "Start date");
        DomainAssertions.notNull(endDate, "End date");
        DomainAssertions.check(endDate.isAfter(startDate), "End date must be after start date");
    }
}
