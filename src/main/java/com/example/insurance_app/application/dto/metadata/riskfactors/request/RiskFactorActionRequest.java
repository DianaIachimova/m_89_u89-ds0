package com.example.insurance_app.application.dto.metadata.riskfactors.request;

import com.example.insurance_app.application.dto.metadata.riskfactors.RiskFactorAction;
import jakarta.validation.constraints.NotNull;

public record RiskFactorActionRequest(
        @NotNull(message = "Action is required")
        RiskFactorAction action
) {
}
