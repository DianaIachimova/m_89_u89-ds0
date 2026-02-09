package com.example.insurance_app.application.service.policy.pricing;

import java.math.BigDecimal;
import java.util.List;

public record PremiumCalculationResult(
        BigDecimal finalPremium,
        List<AppliedAdjustment> appliedAdjustments
) {
}
