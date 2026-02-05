package com.example.insurance_app.domain.building;

import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RiskIndicators Value Object Tests")
class RiskIndicatorsTest {

    @Nested
    @DisplayName("RiskIndicators Creation Tests")
    class RiskIndicatorsCreationTests {

        @Test
        @DisplayName("Should create risk indicators with both zones set to true")
        void shouldCreateRiskIndicatorsWithBothZonesTrue() {
            // Act
            RiskIndicators risk = new RiskIndicators(true, true);

            // Assert
            assertNotNull(risk);
            assertTrue(risk.floodZone());
            assertTrue(risk.earthquakeZone());
        }

        @Test
        @DisplayName("Should create risk indicators with both zones set to false")
        void shouldCreateRiskIndicatorsWithBothZonesFalse() {
            // Act
            RiskIndicators risk = new RiskIndicators(false, false);

            // Assert
            assertNotNull(risk);
            assertFalse(risk.floodZone());
            assertFalse(risk.earthquakeZone());
        }

        @Test
        @DisplayName("Should create risk indicators with only flood zone")
        void shouldCreateRiskIndicatorsWithOnlyFloodZone() {
            // Act
            RiskIndicators risk = new RiskIndicators(true, false);

            // Assert
            assertNotNull(risk);
            assertTrue(risk.floodZone());
            assertFalse(risk.earthquakeZone());
        }

        @Test
        @DisplayName("Should create risk indicators with only earthquake zone")
        void shouldCreateRiskIndicatorsWithOnlyEarthquakeZone() {
            // Act
            RiskIndicators risk = new RiskIndicators(false, true);

            // Assert
            assertNotNull(risk);
            assertFalse(risk.floodZone());
            assertTrue(risk.earthquakeZone());
        }

        @Test
        @DisplayName("Should create risk indicators with null flood zone")
        void shouldCreateRiskIndicatorsWithNullFloodZone() {
            // Act
            RiskIndicators risk = new RiskIndicators(null, true);

            // Assert
            assertNotNull(risk);
            assertNull(risk.floodZone());
            assertTrue(risk.earthquakeZone());
        }

        @Test
        @DisplayName("Should create risk indicators with null earthquake zone")
        void shouldCreateRiskIndicatorsWithNullEarthquakeZone() {
            // Act
            RiskIndicators risk = new RiskIndicators(true, null);

            // Assert
            assertNotNull(risk);
            assertTrue(risk.floodZone());
            assertNull(risk.earthquakeZone());
        }

        @Test
        @DisplayName("Should create risk indicators with both zones null")
        void shouldCreateRiskIndicatorsWithBothZonesNull() {
            // Act
            RiskIndicators risk = new RiskIndicators(null, null);

            // Assert
            assertNotNull(risk);
            assertNull(risk.floodZone());
            assertNull(risk.earthquakeZone());
        }
    }

    @Nested
    @DisplayName("RiskIndicators Equality Tests")
    class RiskIndicatorsEqualityTests {

        @Test
        @DisplayName("Same risk indicators should be equal")
        void sameRiskIndicatorsShouldBeEqual() {
            // Arrange
            RiskIndicators risk1 = new RiskIndicators(true, true);
            RiskIndicators risk2 = new RiskIndicators(true, true);

            // Act & Assert
            assertEquals(risk1, risk2);
            assertEquals(risk1.hashCode(), risk2.hashCode());
        }

        @Test
        @DisplayName("Risk indicators with different flood zones should not be equal")
        void riskIndicatorsWithDifferentFloodZonesShouldNotBeEqual() {
            // Arrange
            RiskIndicators risk1 = new RiskIndicators(true, true);
            RiskIndicators risk2 = new RiskIndicators(false, true);

            // Act & Assert
            assertNotEquals(risk1, risk2);
        }

        @Test
        @DisplayName("Risk indicators with different earthquake zones should not be equal")
        void riskIndicatorsWithDifferentEarthquakeZonesShouldNotBeEqual() {
            // Arrange
            RiskIndicators risk1 = new RiskIndicators(true, true);
            RiskIndicators risk2 = new RiskIndicators(true, false);

            // Act & Assert
            assertNotEquals(risk1, risk2);
        }

        @Test
        @DisplayName("Risk indicators with null values should be equal")
        void riskIndicatorsWithNullValuesShouldBeEqual() {
            // Arrange
            RiskIndicators risk1 = new RiskIndicators(null, null);
            RiskIndicators risk2 = new RiskIndicators(null, null);

            // Act & Assert
            assertEquals(risk1, risk2);
        }
    }

    @Nested
    @DisplayName("RiskIndicators Business Logic Tests")
    class RiskIndicatorsBusinessLogicTests {

        @Test
        @DisplayName("Building in high risk area should have both zones true")
        void buildingInHighRiskAreaShouldHaveBothZonesTrue() {
            // Act
            RiskIndicators highRisk = new RiskIndicators(true, true);

            // Assert
            assertTrue(highRisk.floodZone());
            assertTrue(highRisk.earthquakeZone());
        }

        @Test
        @DisplayName("Building in safe area should have both zones false")
        void buildingInSafeAreaShouldHaveBothZonesFalse() {
            // Act
            RiskIndicators safeArea = new RiskIndicators(false, false);

            // Assert
            assertFalse(safeArea.floodZone());
            assertFalse(safeArea.earthquakeZone());
        }

        @Test
        @DisplayName("Building near river should only have flood zone true")
        void buildingNearRiverShouldOnlyHaveFloodZoneTrue() {
            // Act
            RiskIndicators nearRiver = new RiskIndicators(true, false);

            // Assert
            assertTrue(nearRiver.floodZone());
            assertFalse(nearRiver.earthquakeZone());
        }

        @Test
        @DisplayName("Building on fault line should only have earthquake zone true")
        void buildingOnFaultLineShouldOnlyHaveEarthquakeZoneTrue() {
            // Act
            RiskIndicators onFaultLine = new RiskIndicators(false, true);

            // Assert
            assertFalse(onFaultLine.floodZone());
            assertTrue(onFaultLine.earthquakeZone());
        }
    }
}
