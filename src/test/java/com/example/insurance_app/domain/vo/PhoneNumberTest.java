package com.example.insurance_app.domain.vo;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PhoneNumber Value Object Tests")
class PhoneNumberTest {

    @Nested
    @DisplayName("Valid Phone Number Tests")
    class ValidPhoneNumberTests {

        @Test
        @DisplayName("Should create valid phone number with country code")
        void shouldCreateValidPhoneNumberWithCountryCode() {
            // Act
            PhoneNumber phone = new PhoneNumber("+40712345678");

            // Assert
            assertNotNull(phone);
            assertEquals("+40712345678", phone.value());
        }

        @Test
        @DisplayName("Should create valid phone number without country code")
        void shouldCreateValidPhoneNumberWithoutCountryCode() {
            // Act
            PhoneNumber phone = new PhoneNumber("0712345678");

            // Assert
            assertEquals("0712345678", phone.value());
        }

        @Test
        @DisplayName("Should remove spaces from phone number")
        void shouldRemoveSpacesFromPhoneNumber() {
            // Act
            PhoneNumber phone = new PhoneNumber("+40 712 345 678");

            // Assert
            assertEquals("+40712345678", phone.value());
        }

        @Test
        @DisplayName("Should accept minimum length phone number")
        void shouldAcceptMinimumLengthPhoneNumber() {
            // Act
            PhoneNumber phone = new PhoneNumber("123456"); // 6 digits

            // Assert
            assertEquals("123456", phone.value());
        }

        @Test
        @DisplayName("Should accept maximum length phone number")
        void shouldAcceptMaximumLengthPhoneNumber() {
            // Act
            PhoneNumber phone = new PhoneNumber("12345678901234567890"); // 20 digits

            // Assert
            assertEquals("12345678901234567890", phone.value());
        }
    }

    @Nested
    @DisplayName("Invalid Phone Number Tests")
    class InvalidPhoneNumberTests {

        @Test
        @DisplayName("Should fail when phone number is null")
        void shouldFailWhenPhoneNumberIsNull() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new PhoneNumber(null)
            );
            assertTrue(exception.getMessage().contains("phone"));
        }

        @Test
        @DisplayName("Should fail when phone number is blank")
        void shouldFailWhenPhoneNumberIsBlank() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new PhoneNumber("   ")
            );
            assertTrue(exception.getMessage().contains("phone"));
        }

        @Test
        @DisplayName("Should fail when phone number is too short")
        void shouldFailWhenPhoneNumberIsTooShort() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new PhoneNumber("12345") // 5 digits
            );
            assertTrue(exception.getMessage().contains("phone length"));
        }

        @Test
        @DisplayName("Should fail when phone number is too long")
        void shouldFailWhenPhoneNumberIsTooLong() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new PhoneNumber("123456789012345678901") // 21 digits
            );
            assertTrue(exception.getMessage().contains("phone length"));
        }

        @Test
        @DisplayName("Should fail when phone number contains letters")
        void shouldFailWhenPhoneNumberContainsLetters() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new PhoneNumber("071234ABCD")
            );
            assertTrue(exception.getMessage().contains("phone format"));
        }

        @Test
        @DisplayName("Should fail when phone number contains special characters")
        void shouldFailWhenPhoneNumberContainsSpecialCharacters() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new PhoneNumber("0712-345-678")
            );
            assertTrue(exception.getMessage().contains("phone format"));
        }
    }
}
