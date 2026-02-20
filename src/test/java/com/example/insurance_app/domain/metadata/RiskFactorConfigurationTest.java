package com.example.insurance_app.domain.metadata;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskFactorConfiguration;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.AdjustmentPercentage;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskTarget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RiskFactorConfiguration Domain Model Tests")
class RiskFactorConfigurationTest {

    private static final RiskTarget GEO_TARGET = RiskTarget.ofGeography(RiskLevel.COUNTRY, UUID.randomUUID());
    private static final RiskTarget BUILDING_TYPE_TARGET = RiskTarget.ofBuildingType(BuildingType.RESIDENTIAL);
    private static final AdjustmentPercentage PERCENTAGE = AdjustmentPercentage.of(new BigDecimal("0.05"));

    @Nested
    @DisplayName("createNew")
    class CreateNewTests {

        @Test
        @DisplayName("Should create with geography target")
        void shouldCreateWithGeoTarget() {
            RiskFactorConfiguration config = RiskFactorConfiguration.createNew(GEO_TARGET, PERCENTAGE, true);

            assertNull(config.getId());
            assertEquals(GEO_TARGET, config.getTarget());
            assertEquals(PERCENTAGE, config.getPercentage());
            assertTrue(config.isActive());
            assertNull(config.getAudit());
        }

        @Test
        @DisplayName("Should create with building type target")
        void shouldCreateWithBuildingTypeTarget() {
            RiskFactorConfiguration config = RiskFactorConfiguration.createNew(BUILDING_TYPE_TARGET, PERCENTAGE, false);

            assertEquals(BUILDING_TYPE_TARGET, config.getTarget());
            assertFalse(config.isActive());
        }

        @Test
        @DisplayName("Should reject null target")
        void shouldRejectNullTarget() {
            assertThrows(DomainValidationException.class,
                    () -> RiskFactorConfiguration.createNew(null, PERCENTAGE, true));
        }

        @Test
        @DisplayName("Should reject null percentage")
        void shouldRejectNullPercentage() {
            assertThrows(DomainValidationException.class,
                    () -> RiskFactorConfiguration.createNew(GEO_TARGET, null, true));
        }
    }

    @Nested
    @DisplayName("updatePercentage")
    class UpdatePercentageTests {

        @Test
        @DisplayName("Should update percentage")
        void shouldUpdatePercentage() {
            RiskFactorConfiguration config = RiskFactorConfiguration.createNew(GEO_TARGET, PERCENTAGE, true);
            AdjustmentPercentage newPct = AdjustmentPercentage.of(new BigDecimal("0.10"));

            config.updatePercentage(newPct);

            assertEquals(newPct, config.getPercentage());
        }

        @Test
        @DisplayName("Should reject null percentage on update")
        void shouldRejectNullOnUpdate() {
            RiskFactorConfiguration config = RiskFactorConfiguration.createNew(GEO_TARGET, PERCENTAGE, true);

            assertThrows(DomainValidationException.class, () -> config.updatePercentage(null));
        }
    }

    @Nested
    @DisplayName("activate / deactivate")
    class StatusTests {

        @Test
        @DisplayName("Should activate inactive config")
        void shouldActivate() {
            RiskFactorConfiguration config = RiskFactorConfiguration.createNew(GEO_TARGET, PERCENTAGE, false);

            config.activate();

            assertTrue(config.isActive());
        }

        @Test
        @DisplayName("Should deactivate active config")
        void shouldDeactivate() {
            RiskFactorConfiguration config = RiskFactorConfiguration.createNew(GEO_TARGET, PERCENTAGE, true);

            config.deactivate();

            assertFalse(config.isActive());
        }

        @Test
        @DisplayName("Activate is idempotent")
        void activateIdempotent() {
            RiskFactorConfiguration config = RiskFactorConfiguration.createNew(GEO_TARGET, PERCENTAGE, true);
            config.activate();
            assertTrue(config.isActive());
        }

        @Test
        @DisplayName("Deactivate is idempotent")
        void deactivateIdempotent() {
            RiskFactorConfiguration config = RiskFactorConfiguration.createNew(GEO_TARGET, PERCENTAGE, false);
            config.deactivate();
            assertFalse(config.isActive());
        }
    }

    @Nested
    @DisplayName("RiskTarget validation")
    class RiskTargetTests {

        @Test
        @DisplayName("BUILDING_TYPE requires buildingType, rejects referenceId")
        void buildingTypeValidation() {
            UUID refId = UUID.randomUUID();
            assertThrows(DomainValidationException.class,
                    () -> new RiskTarget(RiskLevel.BUILDING_TYPE, refId, null));
            assertThrows(DomainValidationException.class,
                    () -> new RiskTarget(RiskLevel.BUILDING_TYPE, null, null));
        }

        @Test
        @DisplayName("Geographic levels require referenceId, reject buildingType")
        void geoLevelValidation() {
            assertThrows(DomainValidationException.class,
                    () -> new RiskTarget(RiskLevel.COUNTRY, null, null));
            UUID refId = UUID.randomUUID();
            assertThrows(DomainValidationException.class,
                    () -> new RiskTarget(RiskLevel.COUNTRY, refId, BuildingType.RESIDENTIAL));
        }
    }
}

