package com.example.insurance_app.service;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.client.ClientTypeDto;
import com.example.insurance_app.application.dto.client.request.ContactInfoRequest;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.dto.client.request.UpdateClientRequest;
import com.example.insurance_app.application.dto.client.response.ClientResponse;
import com.example.insurance_app.application.exception.DuplicateIdentificationNumberException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.ClientDtoMapper;
import com.example.insurance_app.application.service.ClientService;
import com.example.insurance_app.domain.model.client.Client;
import com.example.insurance_app.domain.model.client.ClientType;
import com.example.insurance_app.domain.model.client.ContactInfo;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ContactInfoEmbeddable;
import com.example.insurance_app.infrastructure.persistence.entity.client.IdentificationNumberChangeEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.ClientEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.client.ClientRepository;
import com.example.insurance_app.infrastructure.persistence.repository.client.IdentificationNumberChangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService Unit Tests")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private IdentificationNumberChangeRepository identificationNumberChangeRepository;

    @Mock
    private ClientEntityMapper clientEntityMapper;

    @Mock
    private ClientDtoMapper clientDtoMapper;

    @InjectMocks
    private ClientService clientService;

    private CreateClientRequest createRequest;
    private UpdateClientRequest updateRequest;
    private Client client;
    private ClientEntity clientEntity;
    private ClientResponse clientResponse;

    @BeforeEach
    void setUp() {
        // Setup common test data
        ContactInfoRequest contactInfoRequest = new ContactInfoRequest(
                "test@example.com",
                "+40712345678"
        );

        createRequest = new CreateClientRequest(
                ClientTypeDto.INDIVIDUAL,
                "John Doe",
                "1234567890123",
                contactInfoRequest,
                null
        );

        updateRequest = new UpdateClientRequest(
                "Jane Doe",
                null,
                contactInfoRequest,
                null
        );

        ContactInfo contactInfo = new ContactInfo(
                new EmailAddress("test@example.com"),
                new PhoneNumber("+40712345678")
        );

        client = new Client(
                ClientType.INDIVIDUAL,
                "John Doe",
                "1234567890123",
                contactInfo,
                null
        );

        clientEntity = new ClientEntity(
                UUID.randomUUID(),
                ClientTypeEntity.INDIVIDUAL,
                "John Doe",
                "1234567890123",
                new ContactInfoEmbeddable("test@example.com", "+40712345678"),
                null
        );

        clientResponse = new ClientResponse(
                clientEntity.getId(),
                ClientTypeDto.INDIVIDUAL,
                "John Doe",
                "1234567890123",
                null,
                null,
                Instant.now(),
                Instant.now(),
                List.of()
        );
    }

    @Nested
    @DisplayName("Create Client Tests")
    class CreateClientTests {

        @Test
        @DisplayName("Should create client successfully")
        void shouldCreateClientSuccessfully() {
            // Arrange
            when(clientRepository.existsByIdentificationNumber(createRequest.identificationNumber()))
                    .thenReturn(false);
            when(clientDtoMapper.toDomain(createRequest)).thenReturn(client);
            when(clientEntityMapper.toEntity(client)).thenReturn(clientEntity);
            when(clientRepository.save(clientEntity)).thenReturn(clientEntity);
            when(clientEntityMapper.toDomain(clientEntity)).thenReturn(client);
            when(clientDtoMapper.toResponse(any(Client.class), eq(clientEntity), eq(List.of())))
                    .thenReturn(clientResponse);

            // Act
            ClientResponse result = clientService.createClient(createRequest);

            // Assert
            assertNotNull(result);
            assertEquals(clientResponse.id(), result.id());
            verify(clientRepository).existsByIdentificationNumber(createRequest.identificationNumber());
            verify(clientRepository).save(clientEntity);
            verify(clientDtoMapper).toDomain(createRequest);
            verify(clientEntityMapper).toEntity(client);
        }

        @Test
        @DisplayName("Should fail when identification number already exists")
        void shouldFailWhenIdentificationNumberAlreadyExists() {
            // Arrange
            when(clientRepository.existsByIdentificationNumber(createRequest.identificationNumber()))
                    .thenReturn(true);

            // Act & Assert
            DuplicateIdentificationNumberException exception = assertThrows(
                    DuplicateIdentificationNumberException.class,
                    () -> clientService.createClient(createRequest)
            );

            assertEquals("1234567890123", exception.getIdentificationNumber());
            verify(clientRepository).existsByIdentificationNumber(createRequest.identificationNumber());
            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate domain rules during creation")
        void shouldValidateDomainRulesDuringCreation() {
            // Arrange
            when(clientRepository.existsByIdentificationNumber(createRequest.identificationNumber()))
                    .thenReturn(false);
            when(clientDtoMapper.toDomain(createRequest))
                    .thenThrow(new com.example.insurance_app.domain.exception.DomainValidationException("Invalid CNP"));

            // Act & Assert
            assertThrows(
                    com.example.insurance_app.domain.exception.DomainValidationException.class,
                    () -> clientService.createClient(createRequest)
            );

            verify(clientRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Client Tests")
    class UpdateClientTests {

        @Test
        @DisplayName("Should update client successfully without changing identification number")
        void shouldUpdateClientSuccessfullyWithoutChangingIdentificationNumber() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            ClientEntity existingEntity = new ClientEntity(
                    clientId,
                    ClientTypeEntity.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    new ContactInfoEmbeddable("old@example.com", "+40712345678"),
                    null
            );

            Client existingClient = new Client(
                    new ClientId(clientId),
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    new ContactInfo(new EmailAddress("old@example.com"), new PhoneNumber("+40712345678")),
                    null
            );

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingEntity));
            when(clientEntityMapper.toDomain(existingEntity)).thenReturn(existingClient);
            when(clientDtoMapper.toContactInfo(updateRequest.contactInfo()))
                    .thenReturn(new ContactInfo(
                            new EmailAddress("test@example.com"),
                            new PhoneNumber("+40712345678")
                    ));
            when(clientRepository.save(existingEntity)).thenReturn(existingEntity);
            when(clientEntityMapper.toDomain(existingEntity)).thenReturn(existingClient);
            when(identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId))
                    .thenReturn(List.of());
            when(clientDtoMapper.toResponse(any(Client.class), eq(existingEntity), eq(List.of())))
                    .thenReturn(clientResponse);

            // Act
            ClientResponse result = clientService.updateClient(clientId, updateRequest);

            // Assert
            assertNotNull(result);
            verify(clientRepository).findById(clientId);
            verify(clientRepository).save(existingEntity);
            verify(identificationNumberChangeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should update client and track identification number change")
        void shouldUpdateClientAndTrackIdentificationNumberChange() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            String oldIdNumber = "1234567890123";
            String newIdNumber = "9876543210987";

            UpdateClientRequest requestWithNewIdNumber = new UpdateClientRequest(
                    "John Doe",
                    newIdNumber,
                    new ContactInfoRequest("test@example.com", "+40712345678"),
                    null
            );

            ClientEntity existingEntity = new ClientEntity(
                    clientId,
                    ClientTypeEntity.INDIVIDUAL,
                    "John Doe",
                    oldIdNumber,
                    new ContactInfoEmbeddable("test@example.com", "+40712345678"),
                    null
            );

            Client existingClient = new Client(
                    new ClientId(clientId),
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    oldIdNumber,
                    new ContactInfo(new EmailAddress("test@example.com"), new PhoneNumber("+40712345678")),
                    null
            );

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingEntity));
            when(clientEntityMapper.toDomain(existingEntity)).thenReturn(existingClient);
            when(clientDtoMapper.toContactInfo(requestWithNewIdNumber.contactInfo()))
                    .thenReturn(new ContactInfo(
                            new EmailAddress("test@example.com"),
                            new PhoneNumber("+40712345678")
                    ));
            when(clientRepository.existsByIdentificationNumber(newIdNumber)).thenReturn(false);
            when(clientRepository.save(existingEntity)).thenReturn(existingEntity);
            when(identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId))
                    .thenReturn(List.of());
            when(clientDtoMapper.toResponse(any(Client.class), eq(existingEntity), any()))
                    .thenReturn(clientResponse);

            // Act
            ClientResponse result = clientService.updateClient(clientId, requestWithNewIdNumber);

            // Assert
            assertNotNull(result);
            
            // Verify identification number change was tracked
            ArgumentCaptor<IdentificationNumberChangeEntity> changeCaptor = 
                    ArgumentCaptor.forClass(IdentificationNumberChangeEntity.class);
            verify(identificationNumberChangeRepository).save(changeCaptor.capture());
            
            IdentificationNumberChangeEntity savedChange = changeCaptor.getValue();
            assertEquals(oldIdNumber, savedChange.getOldValue());
            assertEquals(newIdNumber, savedChange.getNewValue());
            assertEquals(existingEntity, savedChange.getClient());
            assertNotNull(savedChange.getChangedAt());
            assertEquals("system", savedChange.getChangedBy());
            assertEquals("Updated via API", savedChange.getReason());
        }

        @Test
        @DisplayName("Should fail when updating non-existent client")
        void shouldFailWhenUpdatingNonExistentClient() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> clientService.updateClient(clientId, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Client"));
            verify(clientRepository).findById(clientId);
            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail when new identification number is duplicate")
        void shouldFailWhenNewIdentificationNumberIsDuplicate() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            String newIdNumber = "9876543210987";

            UpdateClientRequest requestWithNewIdNumber = new UpdateClientRequest(
                    "John Doe",
                    newIdNumber,
                    new ContactInfoRequest("test@example.com", "+40712345678"),
                    null
            );

            ClientEntity existingEntity = new ClientEntity(
                    clientId,
                    ClientTypeEntity.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    new ContactInfoEmbeddable("test@example.com", "+40712345678"),
                    null
            );

            Client existingClient = new Client(
                    new ClientId(clientId),
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    new ContactInfo(new EmailAddress("test@example.com"), new PhoneNumber("+40712345678")),
                    null
            );

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingEntity));
            when(clientEntityMapper.toDomain(existingEntity)).thenReturn(existingClient);
            when(clientDtoMapper.toContactInfo(requestWithNewIdNumber.contactInfo()))
                    .thenReturn(new ContactInfo(
                            new EmailAddress("test@example.com"),
                            new PhoneNumber("+40712345678")
                    ));
            when(clientRepository.existsByIdentificationNumber(newIdNumber)).thenReturn(true);

            // Act & Assert
            DuplicateIdentificationNumberException exception = assertThrows(
                    DuplicateIdentificationNumberException.class,
                    () -> clientService.updateClient(clientId, requestWithNewIdNumber)
            );

            assertEquals(newIdNumber, exception.getIdentificationNumber());
            verify(clientRepository, never()).save(any());
            verify(identificationNumberChangeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Client Tests")
    class GetClientTests {

        @Test
        @DisplayName("Should get client by ID successfully")
        void shouldGetClientByIdSuccessfully() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            List<IdentificationNumberChangeEntity> history = List.of();

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
            when(clientEntityMapper.toDomain(clientEntity)).thenReturn(client);
            when(identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId))
                    .thenReturn(history);
            when(clientDtoMapper.toResponse(client, clientEntity, history))
                    .thenReturn(clientResponse);

            // Act
            ClientResponse result = clientService.getClientById(clientId);

            // Assert
            assertNotNull(result);
            assertEquals(clientResponse.id(), result.id());
            verify(clientRepository).findById(clientId);
            verify(identificationNumberChangeRepository).findByClientIdOrderByChangedAtDesc(clientId);
        }

        @Test
        @DisplayName("Should fail when client not found by ID")
        void shouldFailWhenClientNotFoundById() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> clientService.getClientById(clientId)
            );

            assertTrue(exception.getMessage().contains("Client"));
            verify(clientRepository).findById(clientId);
            verify(identificationNumberChangeRepository, never()).findByClientIdOrderByChangedAtDesc(any());
        }

        @Test
        @DisplayName("Should include identification number history when getting client")
        void shouldIncludeIdentificationNumberHistoryWhenGettingClient() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            IdentificationNumberChangeEntity change1 = new IdentificationNumberChangeEntity(
                    clientEntity,
                    "1111111111111",
                    "1234567890123",
                    Instant.now(),
                    "admin",
                    "Corrected CNP"
            );
            List<IdentificationNumberChangeEntity> history = List.of(change1);

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
            when(clientEntityMapper.toDomain(clientEntity)).thenReturn(client);
            when(identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId))
                    .thenReturn(history);
            when(clientDtoMapper.toResponse(client, clientEntity, history))
                    .thenReturn(clientResponse);

            // Act
            ClientResponse result = clientService.getClientById(clientId);

            // Assert
            assertNotNull(result);
            verify(identificationNumberChangeRepository).findByClientIdOrderByChangedAtDesc(clientId);
            verify(clientDtoMapper).toResponse(client, clientEntity, history);
        }
    }

    @Nested
    @DisplayName("Search Clients Tests")
    class SearchClientsTests {

        @Test
        @DisplayName("Should search clients with pagination")
        void shouldSearchClientsWithPagination() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 20);
            Page<ClientEntity> page = new PageImpl<>(List.of(clientEntity), pageable, 1);

            when(clientRepository.searchClients(null, null, pageable)).thenReturn(page);
            when(clientEntityMapper.toDomain(clientEntity)).thenReturn(client);
            when(clientDtoMapper.toResponse(any(Client.class), eq(clientEntity), eq(List.of())))
                    .thenReturn(clientResponse);

            // Act
            PageDto<ClientResponse> result = clientService.searchClients(null, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.totalElements());
            assertEquals(1, result.content().size());
            assertEquals(0, result.page());
            assertEquals(20, result.size());
            verify(clientRepository).searchClients(null, null, pageable);
        }

        @Test
        @DisplayName("Should search clients by name")
        void shouldSearchClientsByName() {
            // Arrange
            String name = "John";
            Pageable pageable = PageRequest.of(0, 20);
            Page<ClientEntity> page = new PageImpl<>(List.of(clientEntity), pageable, 1);

            when(clientRepository.searchClients(name, null, pageable)).thenReturn(page);
            when(clientEntityMapper.toDomain(clientEntity)).thenReturn(client);
            when(clientDtoMapper.toResponse(any(Client.class), eq(clientEntity), eq(List.of())))
                    .thenReturn(clientResponse);

            // Act
            PageDto<ClientResponse> result = clientService.searchClients(name, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.content().size());
            verify(clientRepository).searchClients(name, null, pageable);
        }

        @Test
        @DisplayName("Should search clients by identification number")
        void shouldSearchClientsByIdentificationNumber() {
            // Arrange
            String idNumber = "1234567890123";
            Pageable pageable = PageRequest.of(0, 20);
            Page<ClientEntity> page = new PageImpl<>(List.of(clientEntity), pageable, 1);

            when(clientRepository.searchClients(null, idNumber, pageable)).thenReturn(page);
            when(clientEntityMapper.toDomain(clientEntity)).thenReturn(client);
            when(clientDtoMapper.toResponse(any(Client.class), eq(clientEntity), eq(List.of())))
                    .thenReturn(clientResponse);

            // Act
            PageDto<ClientResponse> result = clientService.searchClients(null, idNumber, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.content().size());
            verify(clientRepository).searchClients(null, idNumber, pageable);
        }

        @Test
        @DisplayName("Should return empty page when no clients found")
        void shouldReturnEmptyPageWhenNoClientsFound() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 20);
            Page<ClientEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(clientRepository.searchClients(null, null, pageable)).thenReturn(emptyPage);

            // Act
            PageDto<ClientResponse> result = clientService.searchClients(null, null, pageable);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.totalElements());
            assertEquals(0, result.content().size());
            verify(clientRepository).searchClients(null, null, pageable);
        }

        @Test
        @DisplayName("Should not include history in search results for performance")
        void shouldNotIncludeHistoryInSearchResults() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 20);
            Page<ClientEntity> page = new PageImpl<>(List.of(clientEntity), pageable, 1);

            when(clientRepository.searchClients(null, null, pageable)).thenReturn(page);
            when(clientEntityMapper.toDomain(clientEntity)).thenReturn(client);
            when(clientDtoMapper.toResponse(any(Client.class), eq(clientEntity), eq(List.of())))
                    .thenReturn(clientResponse);

            // Act
            clientService.searchClients(null, null, pageable);

            // Assert
            verify(identificationNumberChangeRepository, never()).findByClientIdOrderByChangedAtDesc(any());
            verify(clientDtoMapper).toResponse(any(Client.class), eq(clientEntity), eq(List.of()));
        }
    }

    @Nested
    @DisplayName("Identification Number Change Tracking Tests")
    class IdentificationNumberChangeTrackingTests {

        @Test
        @DisplayName("Should not track change when identification number is not provided")
        void shouldNotTrackChangeWhenIdentificationNumberIsNotProvided() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            UpdateClientRequest requestWithoutIdNumber = new UpdateClientRequest(
                    "Jane Doe",
                    null, // No identification number change
                    new ContactInfoRequest("test@example.com", "+40712345678"),
                    null
            );

            ClientEntity existingEntity = new ClientEntity(
                    clientId,
                    ClientTypeEntity.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    new ContactInfoEmbeddable("test@example.com", "+40712345678"),
                    null
            );

            Client existingClient = new Client(
                    new ClientId(clientId),
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    "1234567890123",
                    new ContactInfo(new EmailAddress("test@example.com"), new PhoneNumber("+40712345678")),
                    null
            );

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingEntity));
            when(clientEntityMapper.toDomain(existingEntity)).thenReturn(existingClient);
            when(clientDtoMapper.toContactInfo(requestWithoutIdNumber.contactInfo()))
                    .thenReturn(new ContactInfo(
                            new EmailAddress("test@example.com"),
                            new PhoneNumber("+40712345678")
                    ));
            when(clientRepository.save(existingEntity)).thenReturn(existingEntity);
            when(identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId))
                    .thenReturn(List.of());
            when(clientDtoMapper.toResponse(any(Client.class), eq(existingEntity), any()))
                    .thenReturn(clientResponse);

            // Act
            clientService.updateClient(clientId, requestWithoutIdNumber);

            // Assert
            verify(identificationNumberChangeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should not track change when identification number is same")
        void shouldNotTrackChangeWhenIdentificationNumberIsSame() {
            // Arrange
            UUID clientId = UUID.randomUUID();
            String sameIdNumber = "1234567890123";

            UpdateClientRequest requestWithSameIdNumber = new UpdateClientRequest(
                    "Jane Doe",
                    sameIdNumber,
                    new ContactInfoRequest("test@example.com", "+40712345678"),
                    null
            );

            ClientEntity existingEntity = new ClientEntity(
                    clientId,
                    ClientTypeEntity.INDIVIDUAL,
                    "John Doe",
                    sameIdNumber,
                    new ContactInfoEmbeddable("test@example.com", "+40712345678"),
                    null
            );

            Client existingClient = new Client(
                    new ClientId(clientId),
                    ClientType.INDIVIDUAL,
                    "John Doe",
                    sameIdNumber,
                    new ContactInfo(new EmailAddress("test@example.com"), new PhoneNumber("+40712345678")),
                    null
            );

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingEntity));
            when(clientEntityMapper.toDomain(existingEntity)).thenReturn(existingClient);
            when(clientDtoMapper.toContactInfo(requestWithSameIdNumber.contactInfo()))
                    .thenReturn(new ContactInfo(
                            new EmailAddress("test@example.com"),
                            new PhoneNumber("+40712345678")
                    ));
            when(clientRepository.save(existingEntity)).thenReturn(existingEntity);
            when(identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId))
                    .thenReturn(List.of());
            when(clientDtoMapper.toResponse(any(Client.class), eq(existingEntity), any()))
                    .thenReturn(clientResponse);

            // Act
            clientService.updateClient(clientId, requestWithSameIdNumber);

            // Assert
            verify(identificationNumberChangeRepository, never()).save(any());
        }
    }
}
