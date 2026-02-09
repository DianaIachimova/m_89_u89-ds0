package com.example.insurance_app.domain.model.policy.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record PolicyPremium(
        PremiumAmount base,
        PremiumAmount finalAmount
) {
    public PolicyPremium {
        DomainAssertions.notNull(base, "Base premium");
        DomainAssertions.notNull(finalAmount, "Final premium");
    }
}
