package com.example.insurance_app.application.service.policy.pricing;

import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.domain.util.DomainAssertions;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskFactorConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.FeeConfigRepository;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.RiskFactorConfigRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
public class PremiumCalculator {

    private static final int MONEY_SCALE = 2;
    private static final Map<String, Predicate<RiskIndicators>> RISK_INDICATOR_MAP = Map.of(
            "EARTHQUAKE_ZONE", RiskIndicators::earthquakeZone,
            "FLOOD_ZONE", RiskIndicators::floodZone
    );

    private final FeeConfigRepository feeRepo;
    private final RiskFactorConfigRepository riskRepo;

    public PremiumCalculator(FeeConfigRepository feeRepo,
                             RiskFactorConfigRepository riskRepo) {
        this.feeRepo = feeRepo;
        this.riskRepo = riskRepo;
    }

    public PremiumCalculationResult calculate(BigDecimal basePremium,
                                               PricingContext context,
                                               LocalDate policyStartDate) {

        List<AppliedAdjustment> adjustments = new ArrayList<>();

        BigDecimal brokerPct = collectBrokerCommission(context, adjustments);
        BigDecimal riskPct = collectRiskFactors(context, adjustments);
        BigDecimal baseFeesPct = collectBaseFees(policyStartDate, adjustments);
        BigDecimal riskAdjFeesPct = collectRiskAdjustmentFees(
                policyStartDate, context.riskIndicators(), adjustments);

        BigDecimal totalPct = brokerPct.add(riskPct).add(baseFeesPct).add(riskAdjFeesPct);
        BigDecimal finalPremium = basePremium
                .multiply(BigDecimal.ONE.add(totalPct))
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        DomainAssertions.check(finalPremium.compareTo(BigDecimal.ZERO) > 0,
                "Calculated final premium must be positive");

        return new PremiumCalculationResult(finalPremium, List.copyOf(adjustments));
    }

    private BigDecimal collectBrokerCommission(PricingContext context,
                                              List<AppliedAdjustment> out) {

        BigDecimal brokerCommission = context.brokerCommissionPercentage();
        if (brokerCommission == null)
            return BigDecimal.ZERO;

        out.add(new AppliedAdjustment(
                "BROKER_COMMISSION",
                context.brokerId(),
                "Broker commission",
                brokerCommission));
        return brokerCommission;
    }

    private BigDecimal collectRiskFactors(PricingContext context,
                                          List<AppliedAdjustment> out) {
        var all = riskRepo.findAllActiveByTargets(
                context.countryId(),
                context.countyId(),
                context.cityId(),
                context.buildingType());

        all.sort(Comparator
                .comparing(RiskFactorConfigurationEntity::getLevel)
                .thenComparing(RiskFactorConfigurationEntity::getId));

        BigDecimal sum = BigDecimal.ZERO;
        for (var rf : all) {
            sum = sum.add(rf.getAdjustmentPercentage());
            out.add(new AppliedAdjustment(
                    "RISK_FACTOR",
                    rf.getId(),
                    rf.getLevel() + ":" + resolveRefName(rf),
                    rf.getAdjustmentPercentage()));
        }
        return sum;
    }

    private BigDecimal collectBaseFees(LocalDate date, List<AppliedAdjustment> out) {
        var fees = feeRepo.findActiveOnDateExcludingType(date, FeeConfigTypeEntity.RISK_ADJUSTMENT);

        fees.sort(Comparator
                .comparing(FeeConfigurationEntity::getType)
                .thenComparing(FeeConfigurationEntity::getCode));

        BigDecimal sum = BigDecimal.ZERO;
        for (var fee : fees) {
            sum = sum.add(fee.getPercentage());
            out.add(new AppliedAdjustment(
                    "FEE_CONFIGURATION",
                    fee.getId(),
                    fee.getName(),
                    fee.getPercentage()));
        }
        return sum;
    }

    private BigDecimal collectRiskAdjustmentFees(LocalDate date,
                                                  RiskIndicators indicators,
                                                  List<AppliedAdjustment> out) {
        if (indicators == null) {
            return BigDecimal.ZERO;
        }

        var riskFees = feeRepo.findActiveOnDateByType(date, FeeConfigTypeEntity.RISK_ADJUSTMENT);
        riskFees.sort(Comparator.comparing(FeeConfigurationEntity::getCode));

        BigDecimal sum = BigDecimal.ZERO;
        for (var fee : riskFees) {
            var extractor = RISK_INDICATOR_MAP.get(fee.getCode());
            if (extractor != null && extractor.test(indicators)) {
                sum = sum.add(fee.getPercentage());
                out.add(new AppliedAdjustment(
                        "FEE_RISK_ADJUSTMENT",
                        fee.getId(),
                        fee.getName(),
                        fee.getPercentage()));
            }
        }
        return sum;
    }

    private String resolveRefName(RiskFactorConfigurationEntity rf) {
        return rf.getReferenceId() != null
                ? rf.getReferenceId().toString()
                : String.valueOf(rf.getBuildingType());
    }
}
