package com.example.insurance_app.domain.model.metadata.riskfactors.vo;

import com.example.insurance_app.domain.util.DomainAssertions;
import java.util.UUID;

public record RiskFactorConfigurationId(UUID value) {
    public RiskFactorConfigurationId {
        DomainAssertions.notNull(value, "Risk Factor Configuration ID");
    }
}