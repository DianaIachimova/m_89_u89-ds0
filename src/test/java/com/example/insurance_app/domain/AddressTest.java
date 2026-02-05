package com.example.insurance_app.domain;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.client.vo.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Value Object Tests")
class AddressTest {

    @Nested
    @DisplayName("Address Creation Tests")
    class AddressCreationTests {

        @Test
        @DisplayName("Should create valid address with all fields")
        void shouldCreateValidAddressWithAllFields() {
            // Act
            Address address = new Address(
                    "Main Street",
                    "Bucharest",
                    "Bucuresti",
                    "123456",
                    "Romania"
            );

            // Assert
            assertNotNull(address);
            assertEquals("Main Street", address.street());
            assertEquals("Bucharest", address.city());
            assertEquals("Bucuresti", address.county());
            assertEquals("123456", address.postalCode());
            assertEquals("Romania", address.country());
        }

        @Test
        @DisplayName("Should create address without optional fields")
        void shouldCreateAddressWithoutOptionalFields() {
            // Act
            Address address = new Address(
                    "Main Street",
                    "Bucharest",
                    null, // County is optional
                    null, // Postal code is optional
                    "Romania"
            );

            // Assert
            assertNotNull(address);
            assertEquals("Main Street", address.street());
            assertEquals("Bucharest", address.city());
            assertNull(address.county());
            assertNull(address.postalCode());
            assertEquals("Romania", address.country());
        }

        @Test
        @DisplayName("Should normalize street by trimming")
        void shouldNormalizeStreet() {
            // Act
            Address address = new Address(
                    "  Main Street  ",
                    "Bucharest",
                    null,
                    null,
                    "Romania"
            );

            // Assert
            assertEquals("Main Street", address.street());
        }

        @Test
        @DisplayName("Should normalize postal code by removing spaces")
        void shouldNormalizePostalCode() {
            // Act
            Address address = new Address(
                    "Main Street",
                    "Bucharest",
                    null,
                    "12 34 56",
                    "Romania"
            );

            // Assert
            assertEquals("123456", address.postalCode());
        }

        @Test
        @DisplayName("Should set postal code to null when blank")
        void shouldSetPostalCodeToNullWhenBlank() {
            // Act
            Address address = new Address(
                    "Main Street",
                    "Bucharest",
                    null,
                    "   ",
                    "Romania"
            );

            // Assert
            assertNull(address.postalCode());
        }

        @Test
        @DisplayName("Should set county to null when blank")
        void shouldSetCountyToNullWhenBlank() {
            // Act
            Address address = new Address(
                    "Main Street",
                    "Bucharest",
                    "   ",
                    null,
                    "Romania"
            );

            // Assert
            assertNull(address.county());
        }
    }

    @Nested
    @DisplayName("Address Validation Tests")
    class AddressValidationTests {

        @Test
        @DisplayName("Should fail when street is null")
        void shouldFailWhenStreetIsNull() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Address(null, "Bucharest", null, null, "Romania")
            );
            assertTrue(exception.getMessage().contains("Street"));
        }

        @Test
        @DisplayName("Should fail when street is blank")
        void shouldFailWhenStreetIsBlank() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Address("   ", "Bucharest", null, null, "Romania")
            );
            assertTrue(exception.getMessage().contains("Street"));
        }

        @Test
        @DisplayName("Should fail when city is null")
        void shouldFailWhenCityIsNull() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Address("Main Street", null, null, null, "Romania")
            );
            assertTrue(exception.getMessage().contains("City"));
        }

        @Test
        @DisplayName("Should fail when city is blank")
        void shouldFailWhenCityIsBlank() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Address("Main Street", "   ", null, null, "Romania")
            );
            assertTrue(exception.getMessage().contains("City"));
        }

        @Test
        @DisplayName("Should fail when country is null")
        void shouldFailWhenCountryIsNull() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Address("Main Street", "Bucharest", null, null, null)
            );
            assertTrue(exception.getMessage().contains("Country"));
        }

        @Test
        @DisplayName("Should fail when country is blank")
        void shouldFailWhenCountryIsBlank() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Address("Main Street", "Bucharest", null, null, "   ")
            );
            assertTrue(exception.getMessage().contains("Country"));
        }

        @Test
        @DisplayName("Should fail when postal code is not 6 digits")
        void shouldFailWhenPostalCodeIsNot6Digits() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Address("Main Street", "Bucharest", null, "12345", "Romania")
            );
            assertTrue(exception.getMessage().contains("Postal code"));
        }

        @Test
        @DisplayName("Should fail when postal code contains non-digits")
        void shouldFailWhenPostalCodeContainsNonDigits() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Address("Main Street", "Bucharest", null, "12345A", "Romania")
            );
            assertTrue(exception.getMessage().contains("Postal code"));
        }

        @Test
        @DisplayName("Should accept valid 6-digit postal code")
        void shouldAcceptValid6DigitPostalCode() {
            // Act
            Address address = new Address(
                    "Main Street",
                    "Bucharest",
                    null,
                    "123456",
                    "Romania"
            );

            // Assert
            assertEquals("123456", address.postalCode());
        }
    }
}
