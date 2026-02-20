package com.example.insurance_app.domain.building;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BuildingInfo Value Object Tests")
class BuildingInfoTest {

    @Nested
    @DisplayName("BuildingInfo Creation Tests")
    class BuildingInfoCreationTests {

        @Test
        @DisplayName("Should create valid building info with all fields")
        void shouldCreateValidBuildingInfoWithAllFields() {
            // Act
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    5,
                    new BigDecimal("150.50"),
                    new BigDecimal("200000.00")
            );

            // Assert
            assertNotNull(info);
            assertEquals(2020, info.constructionYear());
            assertEquals(BuildingType.RESIDENTIAL, info.type());
            assertEquals(5, info.numberOfFloors());
            assertEquals(new BigDecimal("150.50"), info.surfaceArea());
            assertEquals(new BigDecimal("200000.00"), info.insuredValue());
        }

        @Test
        @DisplayName("Should create building info without number of floors")
        void shouldCreateBuildingInfoWithoutNumberOfFloors() {
            // Act
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    null,
                    new BigDecimal("150.50"),
                    new BigDecimal("200000.00")
            );

            // Assert
            assertNotNull(info);
            assertNull(info.numberOfFloors());
        }

        @Test
        @DisplayName("Should create building info with all building types")
        void shouldCreateBuildingInfoWithAllBuildingTypes() {
            // RESIDENTIAL
            BuildingInfo residential = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    5,
                    new BigDecimal("150.00"),
                    new BigDecimal("200000.00")
            );
            assertEquals(BuildingType.RESIDENTIAL, residential.type());

            // OFFICE
            BuildingInfo office = new BuildingInfo(
                    2020,
                    BuildingType.OFFICE,
                    10,
                    new BigDecimal("500.00"),
                    new BigDecimal("1000000.00")
            );
            assertEquals(BuildingType.OFFICE, office.type());

            // INDUSTRIAL
            BuildingInfo industrial = new BuildingInfo(
                    2020,
                    BuildingType.INDUSTRIAL,
                    2,
                    new BigDecimal("1000.00"),
                    new BigDecimal("5000000.00")
            );
            assertEquals(BuildingType.INDUSTRIAL, industrial.type());
        }
    }

    @Nested
    @DisplayName("BuildingInfo Validation Tests - Required Fields")
    class BuildingInfoRequiredFieldsTests {

        @Test
        @DisplayName("Should fail when construction year is null")
        void shouldFailWhenConstructionYearIsNull() {
            BigDecimal surface = new BigDecimal("150.00");
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(null, BuildingType.RESIDENTIAL, 5, surface, insured)
            );
            assertTrue(exception.getMessage().contains("constructionYear"));
        }

        @Test
        @DisplayName("Should fail when building type is null")
        void shouldFailWhenBuildingTypeIsNull() {
            BigDecimal surface = new BigDecimal("150.00");
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, null, 5, surface, insured)
            );
            assertTrue(exception.getMessage().contains("BuildingType"));
        }

        @Test
        @DisplayName("Should fail when surface area is null")
        void shouldFailWhenSurfaceAreaIsNull() {
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.RESIDENTIAL, 5, null, insured)
            );
            assertTrue(exception.getMessage().contains("surfaceArea"));
        }

        @Test
        @DisplayName("Should fail when insured value is null")
        void shouldFailWhenInsuredValueIsNull() {
            BigDecimal surface = new BigDecimal("150.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.RESIDENTIAL, 5, surface, null)
            );
            assertTrue(exception.getMessage().contains("insuredValue"));
        }
    }

    @Nested
    @DisplayName("BuildingInfo Validation Tests - Construction Year")
    class ConstructionYearValidationTests {

        @Test
        @DisplayName("Should fail when construction year is too old (before 1800)")
        void shouldFailWhenConstructionYearIsTooOld() {
            BigDecimal surface = new BigDecimal("150.00");
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(1799, BuildingType.RESIDENTIAL, 5, surface, insured)
            );
            assertTrue(exception.getMessage().contains("constructionYear"));
        }

        @Test
        @DisplayName("Should fail when construction year is in the future")
        void shouldFailWhenConstructionYearIsInFuture() {
            int futureYear = java.time.Year.now().getValue() + 1;
            BigDecimal surface = new BigDecimal("150.00");
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(futureYear, BuildingType.RESIDENTIAL, 5, surface, insured)
            );
            assertTrue(exception.getMessage().contains("constructionYear"));
        }

        @Test
        @DisplayName("Should accept construction year of 1800")
        void shouldAcceptConstructionYearOf1800() {
            // Act
            BuildingInfo info = new BuildingInfo(
                    1800,
                    BuildingType.RESIDENTIAL,
                    2,
                    new BigDecimal("100.00"),
                    new BigDecimal("50000.00")
            );

            // Assert
            assertEquals(1800, info.constructionYear());
        }

        @Test
        @DisplayName("Should accept current year as construction year")
        void shouldAcceptCurrentYearAsConstructionYear() {
            // Arrange
            int currentYear = java.time.Year.now().getValue();

            // Act
            BuildingInfo info = new BuildingInfo(
                    currentYear,
                    BuildingType.RESIDENTIAL,
                    5,
                    new BigDecimal("150.00"),
                    new BigDecimal("200000.00")
            );

            // Assert
            assertEquals(currentYear, info.constructionYear());
        }
    }

    @Nested
    @DisplayName("BuildingInfo Validation Tests - Number of Floors")
    class NumberOfFloorsValidationTests {

        @Test
        @DisplayName("Should fail when number of floors is less than 1")
        void shouldFailWhenNumberOfFloorsIsLessThan1() {
            BigDecimal surface = new BigDecimal("150.00");
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.RESIDENTIAL, 0, surface, insured)
            );
            assertTrue(exception.getMessage().contains("numberOfFloors"));
        }

        @Test
        @DisplayName("Should fail when number of floors is negative")
        void shouldFailWhenNumberOfFloorsIsNegative() {
            BigDecimal surface = new BigDecimal("150.00");
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.RESIDENTIAL, -5, surface, insured)
            );
            assertTrue(exception.getMessage().contains("numberOfFloors"));
        }

        @Test
        @DisplayName("Should fail when number of floors exceeds 200")
        void shouldFailWhenNumberOfFloorsExceeds200() {
            BigDecimal surface = new BigDecimal("10000.00");
            BigDecimal insured = new BigDecimal("50000000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.OFFICE, 201, surface, insured)
            );
            assertTrue(exception.getMessage().contains("numberOfFloors"));
        }

        @Test
        @DisplayName("Should accept 1 floor")
        void shouldAccept1Floor() {
            // Act
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    1,
                    new BigDecimal("150.00"),
                    new BigDecimal("200000.00")
            );

            // Assert
            assertEquals(1, info.numberOfFloors());
        }

        @Test
        @DisplayName("Should accept 200 floors")
        void shouldAccept200Floors() {
            // Act
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.OFFICE,
                    200,
                    new BigDecimal("20000.00"),
                    new BigDecimal("100000000.00")
            );

            // Assert
            assertEquals(200, info.numberOfFloors());
        }
    }

    @Nested
    @DisplayName("BuildingInfo Validation Tests - Surface Area")
    class SurfaceAreaValidationTests {

        @Test
        @DisplayName("Should fail when surface area is zero")
        void shouldFailWhenSurfaceAreaIsZero() {
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.RESIDENTIAL, 5, BigDecimal.ZERO, insured)
            );
            assertTrue(exception.getMessage().contains("surfaceArea"));
        }

        @Test
        @DisplayName("Should fail when surface area is negative")
        void shouldFailWhenSurfaceAreaIsNegative() {
            BigDecimal surface = new BigDecimal("-100.00");
            BigDecimal insured = new BigDecimal("200000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.RESIDENTIAL, 5, surface, insured)
            );
            assertTrue(exception.getMessage().contains("surfaceArea"));
        }

        @Test
        @DisplayName("Should accept minimum positive surface area")
        void shouldAcceptMinimumPositiveSurfaceArea() {
            // Act
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    1,
                    new BigDecimal("0.01"),
                    new BigDecimal("10000.00")
            );

            // Assert
            assertEquals(new BigDecimal("0.01"), info.surfaceArea());
        }
    }

    @Nested
    @DisplayName("BuildingInfo Validation Tests - Insured Value")
    class InsuredValueValidationTests {

        @Test
        @DisplayName("Should fail when insured value is zero")
        void shouldFailWhenInsuredValueIsZero() {
            BigDecimal surface = new BigDecimal("150.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.RESIDENTIAL, 5, surface, BigDecimal.ZERO)
            );
            assertTrue(exception.getMessage().contains("insuredValue"));
        }

        @Test
        @DisplayName("Should fail when insured value is negative")
        void shouldFailWhenInsuredValueIsNegative() {
            BigDecimal surface = new BigDecimal("150.00");
            BigDecimal insured = new BigDecimal("-100000.00");
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingInfo(2020, BuildingType.RESIDENTIAL, 5, surface, insured)
            );
            assertTrue(exception.getMessage().contains("insuredValue"));
        }

        @Test
        @DisplayName("Should accept minimum positive insured value")
        void shouldAcceptMinimumPositiveInsuredValue() {
            // Act
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    1,
                    new BigDecimal("50.00"),
                    new BigDecimal("0.01")
            );

            // Assert
            assertEquals(new BigDecimal("0.01"), info.insuredValue());
        }
    }
}
