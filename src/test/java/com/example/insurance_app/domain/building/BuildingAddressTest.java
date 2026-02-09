package com.example.insurance_app.domain.building;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BuildingAddress Value Object Tests")
class BuildingAddressTest {

    @Nested
    @DisplayName("BuildingAddress Creation Tests")
    class BuildingAddressCreationTests {

        @Test
        @DisplayName("Should create valid building address")
        void shouldCreateValidBuildingAddress() {
            // Act
            BuildingAddress address = new BuildingAddress("Main Street", "123");

            // Assert
            assertNotNull(address);
            assertEquals("Main Street", address.street());
            assertEquals("123", address.streetNumber());
        }

        @Test
        @DisplayName("Should normalize street by trimming whitespace")
        void shouldNormalizeStreetByTrimmingWhitespace() {
            // Act
            BuildingAddress address = new BuildingAddress("  Main Street  ", "123");

            // Assert
            assertEquals("Main Street", address.street());
        }

        @Test
        @DisplayName("Should normalize street number by trimming whitespace")
        void shouldNormalizeStreetNumberByTrimmingWhitespace() {
            // Act
            BuildingAddress address = new BuildingAddress("Main Street", "  123A  ");

            // Assert
            assertEquals("123A", address.streetNumber());
        }

        @Test
        @DisplayName("Should create address with alphanumeric street number")
        void shouldCreateAddressWithAlphanumericStreetNumber() {
            // Act
            BuildingAddress address = new BuildingAddress("Main Street", "123A");

            // Assert
            assertEquals("123A", address.streetNumber());
        }

        @Test
        @DisplayName("Should create address with complex street number")
        void shouldCreateAddressWithComplexStreetNumber() {
            // Act
            BuildingAddress address = new BuildingAddress("Main Street", "12B-15");

            // Assert
            assertEquals("12B-15", address.streetNumber());
        }
    }

    @Nested
    @DisplayName("BuildingAddress Validation Tests")
    class BuildingAddressValidationTests {

        @Test
        @DisplayName("Should fail when street is null")
        void shouldFailWhenStreetIsNull() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingAddress(null, "123")
            );
            assertTrue(exception.getMessage().contains("Street"));
        }

        @Test
        @DisplayName("Should fail when street is blank")
        void shouldFailWhenStreetIsBlank() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingAddress("   ", "123")
            );
            assertTrue(exception.getMessage().contains("Street"));
        }

        @Test
        @DisplayName("Should fail when street is empty")
        void shouldFailWhenStreetIsEmpty() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingAddress("", "123")
            );
            assertTrue(exception.getMessage().contains("Street"));
        }

        @Test
        @DisplayName("Should fail when street number is null")
        void shouldFailWhenStreetNumberIsNull() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingAddress("Main Street", null)
            );
            assertTrue(exception.getMessage().contains("Street number"));
        }

        @Test
        @DisplayName("Should fail when street number is blank")
        void shouldFailWhenStreetNumberIsBlank() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingAddress("Main Street", "   ")
            );
            assertTrue(exception.getMessage().contains("Street number"));
        }

        @Test
        @DisplayName("Should fail when street number is empty")
        void shouldFailWhenStreetNumberIsEmpty() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new BuildingAddress("Main Street", "")
            );
            assertTrue(exception.getMessage().contains("Street number"));
        }
    }

    @Nested
    @DisplayName("BuildingAddress Equality Tests")
    class BuildingAddressEqualityTests {

        @Test
        @DisplayName("Same addresses should be equal")
        void sameAddressesShouldBeEqual() {
            // Arrange
            BuildingAddress address1 = new BuildingAddress("Main Street", "123");
            BuildingAddress address2 = new BuildingAddress("Main Street", "123");

            // Act & Assert
            assertEquals(address1, address2);
            assertEquals(address1.hashCode(), address2.hashCode());
        }

        @Test
        @DisplayName("Addresses with different streets should not be equal")
        void addressesWithDifferentStreetsShouldNotBeEqual() {
            // Arrange
            BuildingAddress address1 = new BuildingAddress("Main Street", "123");
            BuildingAddress address2 = new BuildingAddress("Second Street", "123");

            // Act & Assert
            assertNotEquals(address1, address2);
        }

        @Test
        @DisplayName("Addresses with different street numbers should not be equal")
        void addressesWithDifferentStreetNumbersShouldNotBeEqual() {
            // Arrange
            BuildingAddress address1 = new BuildingAddress("Main Street", "123");
            BuildingAddress address2 = new BuildingAddress("Main Street", "456");

            // Act & Assert
            assertNotEquals(address1, address2);
        }

        @Test
        @DisplayName("Should be equal after normalization")
        void shouldBeEqualAfterNormalization() {
            // Arrange
            BuildingAddress address1 = new BuildingAddress("  Main Street  ", "  123  ");
            BuildingAddress address2 = new BuildingAddress("Main Street", "123");

            // Act & Assert
            assertEquals(address1, address2);
        }
    }
}