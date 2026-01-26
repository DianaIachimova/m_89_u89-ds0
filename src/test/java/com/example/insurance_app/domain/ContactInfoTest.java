package com.example.insurance_app.domain;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.ContactInfo;
import com.example.insurance_app.domain.model.vo.EmailAddress;
import com.example.insurance_app.domain.model.vo.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ContactInfo Value Object Tests")
class ContactInfoTest {

    @Nested
    @DisplayName("ContactInfo Creation Tests")
    class ContactInfoCreationTests {

        @Test
        @DisplayName("Should create valid contact info")
        void shouldCreateValidContactInfo() {
            // Arrange
            EmailAddress email = new EmailAddress("test@example.com");
            PhoneNumber phone = new PhoneNumber("+40712345678");

            // Act
            ContactInfo contactInfo = new ContactInfo(email, phone);

            // Assert
            assertNotNull(contactInfo);
            assertEquals(email, contactInfo.email());
            assertEquals(phone, contactInfo.phone());
            assertEquals("test@example.com", contactInfo.getEmail());
            assertEquals("+40712345678", contactInfo.getPhone());
        }
    }

    @Nested
    @DisplayName("ContactInfo Validation Tests")
    class ContactInfoValidationTests {

        @Test
        @DisplayName("Should fail when email is null")
        void shouldFailWhenEmailIsNull() {
            // Arrange
            PhoneNumber phone = new PhoneNumber("+40712345678");

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new ContactInfo(null, phone)
            );
            assertTrue(exception.getMessage().contains("email"));
        }

        @Test
        @DisplayName("Should fail when phone is null")
        void shouldFailWhenPhoneIsNull() {
            // Arrange
            EmailAddress email = new EmailAddress("test@example.com");

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new ContactInfo(email, null)
            );
            assertTrue(exception.getMessage().contains("phone"));
        }
    }
}
