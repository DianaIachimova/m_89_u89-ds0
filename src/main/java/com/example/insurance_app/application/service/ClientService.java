package com.example.insurance_app.application.service;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.dto.client.request.UpdateClientRequest;
import com.example.insurance_app.application.dto.client.response.ClientResponse;
import com.example.insurance_app.application.exception.DuplicateIdentificationNumberException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.ClientDtoMapper;
import com.example.insurance_app.domain.model.Address;
import com.example.insurance_app.domain.model.Client;
import com.example.insurance_app.domain.model.ContactInfo;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.IdentificationNumberChangeEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.ClientEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.client.ClientRepository;
import com.example.insurance_app.infrastructure.persistence.repository.client.IdentificationNumberChangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;
    private final IdentificationNumberChangeRepository identificationNumberChangeRepository;
    private final ClientEntityMapper clientEntityMapper;
    private final ClientDtoMapper clientDtoMapper;

    public ClientService(ClientRepository clientRepository,
                         IdentificationNumberChangeRepository identificationNumberChangeRepository,
                         ClientEntityMapper clientEntityMapper,
                         ClientDtoMapper clientDtoMapper) {
        this.clientRepository = clientRepository;
        this.identificationNumberChangeRepository = identificationNumberChangeRepository;
        this.clientEntityMapper = clientEntityMapper;
        this.clientDtoMapper = clientDtoMapper;
    }

    public ClientResponse createClient(CreateClientRequest request) {
        logger.info("Creating client with identification number: {}", request.identificationNumber());

        // Check for duplicate identification number
        if (clientRepository.existsByIdentificationNumber(request.identificationNumber())) {
            logger.warn("Client with identification number {} already exists", request.identificationNumber());
            throw new DuplicateIdentificationNumberException(request.identificationNumber());
        }

        // Convert DTO to domain model (validates business rules)
        Client client = clientDtoMapper.toDomain(request);

        // Convert domain to entity and save
        ClientEntity entity = clientEntityMapper.toEntity(client);
        ClientEntity savedEntity = clientRepository.save(entity);

        // Convert back to domain with ID
        Client savedClient = clientEntityMapper.toDomain(savedEntity);

        logger.info("Client created successfully with id: {}", savedEntity.getId());

        return clientDtoMapper.toResponse(savedClient, savedEntity, List.of());
    }

    @Transactional
    public ClientResponse updateClient(UUID clientId, UpdateClientRequest request) {
        logger.info("Updating client with id: {}", clientId);

        // Find existing client
        ClientEntity existingEntity = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    logger.warn("Client with id {} not found", clientId);
                    return new ResourceNotFoundException("Client", "id", clientId);
                });

        // Convert to domain
        Client existingClient = clientEntityMapper.toDomain(existingEntity);
        String oldIdentificationNumber = existingClient.getIdentificationNumber();

        // Prepare updated data
        ContactInfo contactInfo = clientDtoMapper.toContactInfo(request.contactInfo());
        Address address = request.address() != null ? clientDtoMapper.toAddress(request.address()) : null;

        // Check if identification number is being changed and if new one is duplicate
        if (request.identificationNumber() != null &&
                !request.identificationNumber().equals(oldIdentificationNumber)) {

            if (clientRepository.existsByIdentificationNumber(request.identificationNumber())) {
                logger.warn("Cannot update: identification number {} already exists", request.identificationNumber());
                throw new DuplicateIdentificationNumberException(request.identificationNumber());
            }
        }

        // Update domain model (validates business rules)
        existingClient.updateInformation(
                request.name(),
                request.identificationNumber(),
                contactInfo,
                address
        );

        // Update entity
        clientEntityMapper.updateEntity(existingClient, existingEntity);
        ClientEntity updatedEntity = clientRepository.save(existingEntity);

        // Track identification number change if it occurred
        if (request.identificationNumber() != null &&
                !request.identificationNumber().equals(oldIdentificationNumber)) {

            logger.info("Identification number changed from {} to {}",
                    oldIdentificationNumber, request.identificationNumber());

            IdentificationNumberChangeEntity changeRecord = new IdentificationNumberChangeEntity(
                    existingEntity,
                    oldIdentificationNumber,
                    request.identificationNumber(),
                    Instant.now(),
                    "system", // TODO: Replace with actual user from SecurityContext
                    "Updated via API"
            );
            identificationNumberChangeRepository.save(changeRecord);
        }

        // Get history
        List<IdentificationNumberChangeEntity> history =
                identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId);

        Client updatedClient = clientEntityMapper.toDomain(updatedEntity);

        logger.info("Client updated successfully with id: {}", clientId);

        return clientDtoMapper.toResponse(updatedClient, updatedEntity, history);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(UUID clientId) {
        logger.info("Fetching client with id: {}", clientId);

        ClientEntity entity = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    logger.warn("Client with id {} not found", clientId);
                    return new ResourceNotFoundException("Client", "id", clientId);
                });

        Client client = clientEntityMapper.toDomain(entity);

        // Get identification number change history
        List<IdentificationNumberChangeEntity> history =
                identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId);

        logger.info("Client fetched successfully with id: {}", clientId);

        return clientDtoMapper.toResponse(client, entity, history);
    }

    @Transactional(readOnly = true)
    public PageDto<ClientResponse> searchClients(String name, String identificationNumber, Pageable pageable) {
        logger.info("Searching clients with name: {}, identificationNumber: {}", name, identificationNumber);

        Page<ClientEntity> page = clientRepository.searchClients(name, identificationNumber, pageable);

        List<ClientResponse> content = page.getContent().stream()
                .map(entity -> {
                    Client client = clientEntityMapper.toDomain(entity);
                    // For search results, don't include full history (performance optimization)
                    return clientDtoMapper.toResponse(client, entity, List.of());
                })
                .toList();

        logger.info("Found {} clients", page.getTotalElements());

        return new PageDto<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
