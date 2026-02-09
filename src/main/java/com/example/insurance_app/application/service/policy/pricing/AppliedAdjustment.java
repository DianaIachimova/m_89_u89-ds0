package com.example.insurance_app.application.service.policy.pricing;

import java.math.BigDecimal;
import java.util.UUID;

public record AppliedAdjustment(
        String sourceType,
        UUID sourceId,
        String name,
        BigDecimal percentage
) {
}
