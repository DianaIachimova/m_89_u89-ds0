package com.example.insurance_app.domain.metadata;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfiguration;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeDetails;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.EffectivePeriod;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeCode;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeName;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeePercentage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FeeConfiguration Domain Model Tests")
class FeeConfigurationTest {

    private static final FeeCode CODE = FeeCode.of("BASE_FEE");
    private static final FeeName NAME = FeeName.of("Base Fee");
    private static final FeePercentage PERCENTAGE = FeePercentage.of(new BigDecimal("0.10"));
    private static final EffectivePeriod PERIOD = EffectivePeriod.of(
            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)
    );

    private FeeDetails createDetails() {
        return FeeDetails.of(CODE, NAME, FeeConfigurationType.ADMIN_FEE, PERCENTAGE, PERIOD);
    }

    @Nested
    @DisplayName("createNew")
    class CreateNewTests {

        @Test
        @DisplayName("Should create fee configuration with correct fields")
        void shouldCreateWithCorrectFields() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);

            assertNull(config.getId());
            assertTrue(config.isActive());
            assertEquals(CODE, config.getDetails().code());
            assertEquals(NAME, config.getDetails().name());
            assertEquals(PERCENTAGE, config.getDetails().percentage());
            assertEquals(PERIOD, config.getDetails().period());
            assertNull(config.getAudit());
        }

        @Test
        @DisplayName("Should create inactive configuration")
        void shouldCreateInactive() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), false);
            assertFalse(config.isActive());
        }

        @Test
        @DisplayName("Should reject null details")
        void shouldRejectNullDetails() {
            assertThrows(DomainValidationException.class,
                    () -> FeeConfiguration.createNew(null, true));
        }
    }

    @Nested
    @DisplayName("updateDetails")
    class UpdateDetailsTests {

        @Test
        @DisplayName("Should update name, percentage and period end")
        void shouldUpdateDetails() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);
            FeeName newName = FeeName.of("Updated Fee");
            FeePercentage newPct = FeePercentage.of(new BigDecimal("0.20"));
            LocalDate newEnd = LocalDate.of(2027, 6, 30);

            config.updateDetails(newName, newPct, newEnd);

            assertEquals(newName, config.getDetails().name());
            assertEquals(newPct, config.getDetails().percentage());
            assertEquals(newEnd, config.getDetails().period().to());
            assertEquals(PERIOD.from(), config.getDetails().period().from());
        }

        @Test
        @DisplayName("Should reject null percentage on update")
        void shouldRejectNullPercentage() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);
            assertThrows(DomainValidationException.class,
                    () -> config.updateDetails(NAME, null, null));
        }

        @Test
        @DisplayName("Should reject null name on update")
        void shouldRejectNullName() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);
            assertThrows(DomainValidationException.class,
                    () -> config.updateDetails(null, PERCENTAGE, null));
        }
    }

    @Nested
    @DisplayName("deactivate")
    class DeactivateTests {

        @Test
        @DisplayName("Should set active to false")
        void shouldDeactivate() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);

            config.deactivate();

            assertFalse(config.isActive());
        }

        @Test
        @DisplayName("Should set end date to now when end date is null")
        void shouldSetEndDateWhenNull() {
            EffectivePeriod openPeriod = EffectivePeriod.of(LocalDate.of(2026, 1, 1), null);
            FeeDetails details = FeeDetails.of(CODE, NAME, FeeConfigurationType.ADMIN_FEE, PERCENTAGE, openPeriod);
            FeeConfiguration config = FeeConfiguration.createNew(details, true);

            config.deactivate();

            assertFalse(config.isActive());
        }
    }

    @Nested
    @DisplayName("isValidOn")
    class IsValidOnTests {

        @Test
        @DisplayName("Should return true when active and date within period")
        void shouldReturnTrueWhenActiveAndInPeriod() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);
            assertTrue(config.isValidOn(LocalDate.of(2026, 6, 15)));
        }

        @Test
        @DisplayName("Should return false when inactive")
        void shouldReturnFalseWhenInactive() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), false);
            assertFalse(config.isValidOn(LocalDate.of(2026, 6, 15)));
        }

        @Test
        @DisplayName("Should return false when date before period")
        void shouldReturnFalseWhenBeforePeriod() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);
            assertFalse(config.isValidOn(LocalDate.of(2025, 12, 31)));
        }

        @Test
        @DisplayName("Should return false when date after period")
        void shouldReturnFalseWhenAfterPeriod() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);
            assertFalse(config.isValidOn(LocalDate.of(2027, 1, 1)));
        }

        @Test
        @DisplayName("Should return true on period boundaries")
        void shouldReturnTrueOnBoundaries() {
            FeeConfiguration config = FeeConfiguration.createNew(createDetails(), true);
            assertTrue(config.isValidOn(LocalDate.of(2026, 1, 1)));
            assertTrue(config.isValidOn(LocalDate.of(2026, 12, 31)));
        }
    }
}
