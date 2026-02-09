package com.example.insurance_app.domain.model.policy.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.time.LocalDate;

public record CancellationInfo(LocalDate cancelledAt, String reason) {
    public CancellationInfo {
        DomainAssertions.notNull(cancelledAt, "Cancellation date");
        DomainAssertions.notBlank(reason, "Cancellation reason");
        reason = DomainAssertions.normalize(reason);
        DomainAssertions.check(reason.length() <= 500, "Cancellation reason must not exceed 500 characters");
    }
}
