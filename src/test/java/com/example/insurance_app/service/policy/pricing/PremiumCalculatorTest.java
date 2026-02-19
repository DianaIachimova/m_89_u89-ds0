package com.example.insurance_app.service.policy.pricing;

import com.example.insurance_app.application.service.policy.pricing.AppliedAdjustment;
import com.example.insurance_app.application.service.policy.pricing.PricingContext;
import com.example.insurance_app.application.service.policy.pricing.PremiumCalculationResult;
import com.example.insurance_app.application.service.policy.pricing.PremiumCalculator;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskFactorConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskLevelEntity;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.FeeConfigRepository;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.RiskFactorConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PremiumCalculator Unit Tests")
class PremiumCalculatorTest {

    @Mock
    private FeeConfigRepository feeRepo;

    @Mock
    private RiskFactorConfigRepository riskRepo;

    @InjectMocks
    private PremiumCalculator premiumCalculator;

    private static final UUID COUNTRY_ID = UUID.randomUUID();
    private static final UUID COUNTY_ID = UUID.randomUUID();
    private static final UUID CITY_ID = UUID.randomUUID();
    private static final UUID BROKER_ID = UUID.randomUUID();
    private static final LocalDate POLICY_START = LocalDate.of(2026, 6, 1);

    private PricingContext buildContext(RiskIndicators riskIndicators) {
        return buildContext(riskIndicators, null, BROKER_ID);
    }

    private PricingContext buildContext(RiskIndicators riskIndicators,
                                        BigDecimal brokerCommissionPct,
                                        UUID brokerId) {
        return new PricingContext(
                COUNTRY_ID, COUNTY_ID, CITY_ID, BuildingTypeEntity.RESIDENTIAL, riskIndicators,
                brokerId, brokerCommissionPct);
    }

    private RiskFactorConfigurationEntity riskFactor(BigDecimal pct) {
        return new RiskFactorConfigurationEntity(
                UUID.randomUUID(), RiskLevelEntity.COUNTRY, COUNTRY_ID, null, pct, true
        );
    }

    private FeeConfigurationEntity baseFee(String code, String name, BigDecimal pct) {
        return new FeeConfigurationEntity(
                UUID.randomUUID(), code, name, FeeConfigTypeEntity.ADMIN_FEE, pct, null, true
        );
    }

    private FeeConfigurationEntity riskAdjFee(String code, String name, BigDecimal pct) {
        return new FeeConfigurationEntity(
                UUID.randomUUID(), code, name, FeeConfigTypeEntity.RISK_ADJUSTMENT, pct, null, true
        );
    }

    private void stubEmptyRepos() {
        when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>());
        when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());
    }

    @Nested
    @DisplayName("No adjustments")
    class NoAdjustmentsTests {

        @Test
        @DisplayName("No fees, no risk factors -> finalPremium = basePremium")
        void noAdjustments() {
            stubEmptyRepos();
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            PricingContext ctx = buildContext(new RiskIndicators(false, false));
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("1000.00"), result.finalPremium());
            assertTrue(result.appliedAdjustments().isEmpty());
        }
    }

    @Nested
    @DisplayName("Base fees only")
    class BaseFeesTests {

        @Test
        @DisplayName("Single 10% base fee -> basePremium * 1.10")
        void singleBaseFee() {
            when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT)))
                    .thenReturn(new ArrayList<>(List.of(baseFee("ADMIN_FEE", "Admin Fee", new BigDecimal("0.1000")))));
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            PricingContext ctx = buildContext(new RiskIndicators(false, false));
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("1100.00"), result.finalPremium());
            assertEquals(1, result.appliedAdjustments().size());
        }
    }

    @Nested
    @DisplayName("Risk factors only")
    class RiskFactorsTests {

        @Test
        @DisplayName("Two risk factors (5% + 3%) -> basePremium * 1.08")
        void multipleRiskFactors() {
            when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>(List.of(
                    riskFactor(new BigDecimal("0.0500")),
                    riskFactor(new BigDecimal("0.0300"))
            )));
            when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            PricingContext ctx = buildContext(new RiskIndicators(false, false));
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("1080.00"), result.finalPremium());
            assertEquals(2, result.appliedAdjustments().size());
        }
    }

    @Nested
    @DisplayName("Risk adjustment fees")
    class RiskAdjustmentFeesTests {

        @Test
        @DisplayName("FLOOD_ZONE risk adjustment applied when building is in flood zone")
        void floodZoneApplied() {
            when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT)))
                    .thenReturn(new ArrayList<>(List.of(riskAdjFee("FLOOD_ZONE", "Flood Zone Fee", new BigDecimal("0.0500")))));

            PricingContext ctx = buildContext(new RiskIndicators(true, false));
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("1050.00"), result.finalPremium());
            assertEquals(1, result.appliedAdjustments().size());
            assertEquals("FEE_RISK_ADJUSTMENT", result.appliedAdjustments().getFirst().sourceType());
        }

        @Test
        @DisplayName("EARTHQUAKE_ZONE risk adjustment applied when building is in earthquake zone")
        void earthquakeZoneApplied() {
            when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT)))
                    .thenReturn(new ArrayList<>(List.of(riskAdjFee("EARTHQUAKE_ZONE", "Earthquake Fee", new BigDecimal("0.0800")))));

            PricingContext ctx = buildContext(new RiskIndicators(false, true));
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("1080.00"), result.finalPremium());
        }

        @Test
        @DisplayName("Risk adjustment NOT applied when indicator is false")
        void notAppliedWhenIndicatorFalse() {
            when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT)))
                    .thenReturn(new ArrayList<>(List.of(riskAdjFee("FLOOD_ZONE", "Flood Zone Fee", new BigDecimal("0.0500")))));

            PricingContext ctx = buildContext(new RiskIndicators(false, false));
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("1000.00"), result.finalPremium());
            assertTrue(result.appliedAdjustments().isEmpty());
        }

        @Test
        @DisplayName("Null risk indicators -> risk adjustment fees skipped")
        void nullRiskIndicatorsSkipped() {
            when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            PricingContext ctx = buildContext(null);
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("1000.00"), result.finalPremium());
        }
    }

    @Nested
    @DisplayName("Combined adjustments")
    class CombinedTests {

        @Test
        @DisplayName("Base fees + risk factors + risk adjustment combined")
        void allCombined() {
            when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>(List.of(
                    riskFactor(new BigDecimal("0.0300"))
            )));
            when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT)))
                    .thenReturn(new ArrayList<>(List.of(baseFee("ADMIN_FEE", "Admin Fee", new BigDecimal("0.0500")))));
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT)))
                    .thenReturn(new ArrayList<>(List.of(riskAdjFee("FLOOD_ZONE", "Flood Zone Fee", new BigDecimal("0.0200")))));

            PricingContext ctx = buildContext(new RiskIndicators(true, false));

            // total = 0.03 + 0.05 + 0.02 = 0.10
            // final = 1000 * 1.10 = 1100.00
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("1100.00"), result.finalPremium());
            assertEquals(3, result.appliedAdjustments().size());
        }
    }

    @Nested
    @DisplayName("Rounding and edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Result rounded to 2 decimal places HALF_UP")
        void roundedCorrectly() {
            when(riskRepo.findAllActiveByTargets(any(), any(), any(), any())).thenReturn(new ArrayList<>());
            when(feeRepo.findActiveOnDateExcludingType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT)))
                    .thenReturn(new ArrayList<>(List.of(baseFee("ADMIN_FEE", "Admin Fee", new BigDecimal("0.0333")))));
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            PricingContext ctx = buildContext(new RiskIndicators(false, false));

            // 999.99 * 1.0333 = 1033.2867... -> 1033.29
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("999.99"), ctx, POLICY_START);

            BigDecimal expected = new BigDecimal("999.99")
                    .multiply(BigDecimal.ONE.add(new BigDecimal("0.0333")))
                    .setScale(2, RoundingMode.HALF_UP);
            assertEquals(expected, result.finalPremium());
        }

        @Test
        @DisplayName("Empty repos return basePremium unchanged")
        void emptyReposReturnBasePremium() {
            stubEmptyRepos();
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            PricingContext ctx = buildContext(new RiskIndicators(false, false));
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("5000.00"), ctx, POLICY_START);

            assertEquals(new BigDecimal("5000.00"), result.finalPremium());
        }
    }

    @Nested
    @DisplayName("Broker commission")
    class BrokerCommissionTests {

        @Test
        @DisplayName("Broker commission null -> no broker adjustment; final premium equals no-commission result")
        void brokerCommissionNullNoAdjustment() {
            stubEmptyRepos();
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            PricingContext ctxNoBroker = buildContext(new RiskIndicators(false, false));
            PremiumCalculationResult resultNoBroker = premiumCalculator.calculate(new BigDecimal("1000.00"), ctxNoBroker, POLICY_START);

            PricingContext ctxWithNullCommission = buildContext(new RiskIndicators(false, false), null, BROKER_ID);
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctxWithNullCommission, POLICY_START);

            assertNoBrokerAdjustment(result);
            assertEquals(resultNoBroker.finalPremium(), result.finalPremium());
            assertEquals(new BigDecimal("1000.00"), result.finalPremium());
        }

        @Test
        @DisplayName("Broker commission present (5%) -> one BROKER_COMMISSION adjustment, final = base * (1 + 0.05)")
        void brokerCommissionPresent() {
            stubEmptyRepos();
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            UUID brokerId = UUID.randomUUID();
            BigDecimal brokerPct = new BigDecimal("0.05");
            PricingContext ctx = buildContext(new RiskIndicators(false, false), brokerPct, brokerId);
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertBrokerAdjustment(result, brokerPct, brokerId, new BigDecimal("1050.00"));
        }

        @Test
        @DisplayName("Broker commission 0 -> adjustment added with 0%, final unchanged from other factors only")
        void brokerCommissionZero() {
            stubEmptyRepos();
            when(feeRepo.findActiveOnDateByType(any(), eq(FeeConfigTypeEntity.RISK_ADJUSTMENT))).thenReturn(new ArrayList<>());

            UUID brokerId = UUID.randomUUID();
            PricingContext ctx = buildContext(new RiskIndicators(false, false), BigDecimal.ZERO, brokerId);
            PremiumCalculationResult result = premiumCalculator.calculate(new BigDecimal("1000.00"), ctx, POLICY_START);

            assertBrokerAdjustment(result, BigDecimal.ZERO, brokerId, new BigDecimal("1000.00"));
        }

        private void assertNoBrokerAdjustment(PremiumCalculationResult result) {
            boolean hasBroker = result.appliedAdjustments().stream()
                    .anyMatch(a -> "BROKER_COMMISSION".equals(a.sourceType()));
            assertFalse(hasBroker, "Expected no BROKER_COMMISSION adjustment");
        }

        private void assertBrokerAdjustment(PremiumCalculationResult result,
                                            BigDecimal expectedPct, UUID expectedBrokerId,
                                            BigDecimal expectedFinalPremium) {
            List<AppliedAdjustment> brokerAdjs = result.appliedAdjustments().stream()
                    .filter(a -> "BROKER_COMMISSION".equals(a.sourceType()))
                    .toList();
            assertEquals(1, brokerAdjs.size());
            assertEquals("BROKER_COMMISSION", brokerAdjs.getFirst().sourceType());
            assertEquals(expectedBrokerId, brokerAdjs.getFirst().sourceId());
            assertEquals("Broker commission", brokerAdjs.getFirst().name());
            assertEquals(0, expectedPct.compareTo(brokerAdjs.getFirst().percentage()));
            assertEquals(0, expectedFinalPremium.compareTo(result.finalPremium()));
        }
    }
}
