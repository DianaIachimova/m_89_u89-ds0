package com.example.insurance_app.domain.vo;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmailAddress Value Object Tests")
class EmailAddressTest {

    @Nested
    @DisplayName("Valid Email Tests")
    class ValidEmailTests {

        @Test
        @DisplayName("Should create valid email address")
        void shouldCreateValidEmailAddress() {
            // Act
            EmailAddress email = new EmailAddress("test@example.com");

            // Assert
            assertNotNull(email);
            assertEquals("test@example.com", email.value());
        }

        @Test
        @DisplayName("Should trim whitespace from email")
        void shouldTrimWhitespace() {
            // Act
            EmailAddress email = new EmailAddress("  test@example.com  ");

            // Assert
            assertEquals("test@example.com", email.value());
        }

        @Test
        @DisplayName("Should accept email with subdomain")
        void shouldAcceptEmailWithSubdomain() {
            // Act
            EmailAddress email = new EmailAddress("user@mail.example.com");

            // Assert
            assertEquals("user@mail.example.com", email.value());
        }

        @Test
        @DisplayName("Should accept email with plus sign")
        void shouldAcceptEmailWithPlusSign() {
            // Act
            EmailAddress email = new EmailAddress("user+tag@example.com");

            // Assert
            assertEquals("user+tag@example.com", email.value());
        }
    }

    @Nested
    @DisplayName("Invalid Email Tests")
    class InvalidEmailTests {

        @Test
        @DisplayName("Should fail when email is null")
        void shouldFailWhenEmailIsNull() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new EmailAddress(null)
            );
            assertTrue(exception.getMessage().contains("email"));
        }

        @Test
        @DisplayName("Should fail when email is blank")
        void shouldFailWhenEmailIsBlank() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new EmailAddress("   ")
            );
            assertTrue(exception.getMessage().contains("email"));
        }

        @Test
        @DisplayName("Should fail when email has no @ symbol")
        void shouldFailWhenEmailHasNoAtSymbol() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new EmailAddress("testexample.com")
            );
            assertTrue(exception.getMessage().contains("email format"));
        }

        @Test
        @DisplayName("Should fail when email has no domain")
        void shouldFailWhenEmailHasNoDomain() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new EmailAddress("test@")
            );
            assertTrue(exception.getMessage().contains("email format"));
        }

        @Test
        @DisplayName("Should fail when email has no local part")
        void shouldFailWhenEmailHasNoLocalPart() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new EmailAddress("@example.com")
            );
            assertTrue(exception.getMessage().contains("email format"));
        }

        @Test
        @DisplayName("Should fail when email is too long")
        void shouldFailWhenEmailIsTooLong() {
            // Arrange
            String longEmail = "a".repeat(250) + "@example.com"; // > 254 characters

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new EmailAddress(longEmail)
            );
            assertTrue(exception.getMessage().contains("email length"));
        }
    }
}
