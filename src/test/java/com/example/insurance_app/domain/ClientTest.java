package com.example.insurance_app.domain;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.client.vo.Address;
import com.example.insurance_app.domain.model.client.Client;
import com.example.insurance_app.domain.model.client.ClientType;
import com.example.insurance_app.domain.model.client.ContactInfo;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Client Domain Model Tests")
class ClientTest {

    @Nested
    @DisplayName("Client Creation Tests")
    class ClientCreationTests {

        @Test
        @DisplayName("Should create valid individual client")
        void shouldCreateValidIndividualClient() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("john.doe@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Address address = new Address("Main St", "Bucharest", "Bucuresti", "123456", "Romania");

            // Act
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123", // Valid CNP (13 digits)
                    contactInfo,
                    address
            );

            // Assert
            assertNotNull(client);
            assertEquals(ClientType.INDIVIDUAL, client.getClientType());
            assertEquals("John Doe", client.getName());
            assertEquals("1234567890123", client.getIdentificationNumber());
            assertEquals(contactInfo, client.getContactInfo());
            assertEquals(address, client.getAddress());
            assertNull(client.getId()); // No ID for new client
        }

        @Test
        @DisplayName("Should create valid company client")
        void shouldCreateValidCompanyClient() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("contact@company.com"),
                    new PhoneNumber("+40212345678")
            );

            // Act
            Client client = new Client(
                    ClientType.COMPANY,
                    "ABC Company SRL",
                    "12345678", // Valid CUI (2-10 digits)
                    contactInfo,
                    null // Address is optional
            );

            // Assert
            assertNotNull(client);
            assertEquals(ClientType.COMPANY, client.getClientType());
            assertEquals("ABC Company SRL", client.getName());
            assertEquals("12345678", client.getIdentificationNumber());
            assertNull(client.getAddress());
        }

        @Test
        @DisplayName("Should create client with existing ID")
        void shouldCreateClientWithExistingId() {
            // Arrange
            UUID uuid = UUID.randomUUID();
            ClientId clientId = new ClientId(uuid);
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act
            Client client = new Client(
                    clientId,
                    ClientType.INDIVIDUAL,
                    "Test User",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Assert
            assertNotNull(client.getId());
            assertEquals(uuid, client.getId().value());
        }

        @Test
        @DisplayName("Should normalize name by trimming whitespace")
        void shouldNormalizeName() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "  John Doe  ", // Name with extra spaces
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Assert
            assertEquals("John Doe", client.getName());
        }

        @Test
        @DisplayName("Should normalize identification number by removing spaces")
        void shouldNormalizeIdentificationNumber() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1 2 3 4 5 6 7 8 9 0 1 2 3", // CNP with spaces
                    contactInfo,
                    null
            );

            // Assert
            assertEquals("1234567890123", client.getIdentificationNumber());
        }
    }

    @Nested
    @DisplayName("Client Validation Tests")
    class ClientValidationTests {

        @Test
        @DisplayName("Should fail when client type is null")
        void shouldFailWhenClientTypeIsNull() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(null, "John Doe", "1234567890123", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("Client type"));
        }

        @Test
        @DisplayName("Should fail when name is null")
        void shouldFailWhenNameIsNull() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(ClientType.INDIVIDUAL, null, "1234567890123", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("Name"));
        }

        @Test
        @DisplayName("Should fail when name is blank")
        void shouldFailWhenNameIsBlank() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(ClientType.INDIVIDUAL, "   ", "1234567890123", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("Name"));
        }

        @Test
        @DisplayName("Should fail when identification number is null")
        void shouldFailWhenIdentificationNumberIsNull() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(ClientType.INDIVIDUAL, "John Doe", null, contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("Identification number"));
        }

        @Test
        @DisplayName("Should fail when identification number is blank")
        void shouldFailWhenIdentificationNumberIsBlank() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(ClientType.INDIVIDUAL, "John Doe", "   ", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("Identification number"));
        }

        @Test
        @DisplayName("Should fail when contact info is null")
        void shouldFailWhenContactInfoIsNull() {
            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(ClientType.INDIVIDUAL, "John Doe", "1234567890123", null, null)
            );
            assertTrue(exception.getMessage().contains("Contact info"));
        }

        @Test
        @DisplayName("Should fail when individual client has invalid CNP")
        void shouldFailWhenIndividualClientHasInvalidCNP() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act & Assert - CNP too short
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(ClientType.INDIVIDUAL, "John Doe", "12345", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("CNP"));
        }

        @Test
        @DisplayName("Should fail when company client has invalid CUI")
        void shouldFailWhenCompanyClientHasInvalidCUI() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act & Assert - CUI too short (less than 2 digits)
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(ClientType.COMPANY, "Company", "1", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("CUI"));
        }

        @Test
        @DisplayName("Should fail when company client has CNP instead of CUI")
        void shouldFailWhenCompanyClientHasCNP() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            // Act & Assert - CNP (13 digits) for company
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Client(ClientType.COMPANY, "Company", "1234567890123", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("CUI"));
        }
    }

    @Nested
    @DisplayName("Client Update Tests")
    class ClientUpdateTests {

        @Test
        @DisplayName("Should update client information successfully")
        void shouldUpdateClientInformation() {
            // Arrange
            ContactInfo originalContactInfo = new ContactInfo(
                    new EmailAddress("old@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    originalContactInfo,
                    null
            );

            ContactInfo newContactInfo = new ContactInfo(
                    new EmailAddress("new@example.com"),
                    new PhoneNumber("+40798765432")
            );
            Address newAddress = new Address("New St", "Cluj-Napoca", "Cluj", "400001", "Romania");

            // Act
            client.updateInformation("Jane Doe", null, newContactInfo, newAddress);

            // Assert
            assertEquals("Jane Doe", client.getName());
            assertEquals("1234567890123", client.getIdentificationNumber()); // Unchanged
            assertEquals(newContactInfo, client.getContactInfo());
            assertEquals(newAddress, client.getAddress());
        }

        @Test
        @DisplayName("Should update identification number when valid")
        void shouldUpdateIdentificationNumber() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act
            client.updateInformation("John Doe", "9876543210987", contactInfo, null);

            // Assert
            assertEquals("9876543210987", client.getIdentificationNumber());
        }

        @Test
        @DisplayName("Should not update identification number when same value")
        void shouldNotUpdateWhenIdentificationNumberIsSame() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act
            client.updateInformation("John Doe", "1234567890123", contactInfo, null);

            // Assert
            assertEquals("1234567890123", client.getIdentificationNumber());
        }

        @Test
        @DisplayName("Should fail when updating to invalid identification number")
        void shouldFailWhenUpdatingToInvalidIdentificationNumber() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> client.updateInformation("John Doe", "12345", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("CNP"));
        }

        @Test
        @DisplayName("Should fail when updating with null name")
        void shouldFailWhenUpdatingWithNullName() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> client.updateInformation(null, null, contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("Name"));
        }

        @Test
        @DisplayName("Should fail when updating with null contact info")
        void shouldFailWhenUpdatingWithNullContactInfo() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> client.updateInformation("John Doe", null, null, null)
            );
            assertTrue(exception.getMessage().contains("Contact info"));
        }

        @Test
        @DisplayName("Should keep existing address when update address is null")
        void shouldKeepExistingAddressWhenUpdateIsNull() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Address originalAddress = new Address("Old St", "Bucharest", "Bucuresti", "123456", "Romania");
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    originalAddress
            );

            // Act
            client.updateInformation("John Doe", null, contactInfo, null);

            // Assert
            assertEquals(originalAddress, client.getAddress());
        }
    }

    @Nested
    @DisplayName("Client Equality Tests")
    class ClientEqualityTests {

        @Test
        @DisplayName("Should be equal when IDs are equal")
        void shouldBeEqualWhenIdsAreEqual() {
            // Arrange
            UUID uuid = UUID.randomUUID();
            ClientId clientId = new ClientId(uuid);
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            Client client1 = new Client(
                    clientId,
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            Client client2 = new Client(
                    clientId,
                    ClientType.INDIVIDUAL,
                    "Jane Doe", // Different name
                    "9876543210987", // Different identification number
                    contactInfo,
                    null
            );

            // Act & Assert
            assertEquals(client1, client2);
            assertEquals(client1.hashCode(), client2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when IDs are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            Client client1 = new Client(
                    new ClientId(UUID.randomUUID()),
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            Client client2 = new Client(
                    new ClientId(UUID.randomUUID()),
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act & Assert
            assertNotEquals(client1, client2);
        }

        @Test
        @DisplayName("Should handle null ID in equals")
        void shouldHandleNullIdInEquals() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );

            Client client1 = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            Client client2 = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act & Assert
            assertNotEquals(client1, client2); // Both have null IDs
        }
    }

    @Nested
    @DisplayName("Identification Number Change Tests")
    class IdentificationNumberChangeTests {

        @Test
        @DisplayName("Should detect identification number change")
        void shouldDetectIdentificationNumberChange() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            String oldNumber = client.getIdentificationNumber();

            // Act
            client.updateInformation("John Doe", "9876543210987", contactInfo, null);

            // Assert
            assertNotEquals(oldNumber, client.getIdentificationNumber());
            assertEquals("9876543210987", client.getIdentificationNumber());
        }

        @Test
        @DisplayName("Should validate new identification number on change")
        void shouldValidateNewIdentificationNumberOnChange() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act & Assert - Try to change to invalid CNP
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> client.updateInformation("John Doe", "123", contactInfo, null)
            );
            assertTrue(exception.getMessage().contains("CNP"));
            // Original value should remain unchanged
            assertEquals("1234567890123", client.getIdentificationNumber());
        }

        @Test
        @DisplayName("Should normalize new identification number on change")
        void shouldNormalizeNewIdentificationNumberOnChange() {
            // Arrange
            ContactInfo contactInfo = new ContactInfo(
                    new EmailAddress("test@example.com"),
                    new PhoneNumber("+40712345678")
            );
            Client client = new Client(
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    contactInfo,
                    null
            );

            // Act - Update with spaces
            client.updateInformation("John Doe", "9 8 7 6 5 4 3 2 1 0 9 8 7", contactInfo, null);

            // Assert
            assertEquals("9876543210987", client.getIdentificationNumber());
        }
    }
}
