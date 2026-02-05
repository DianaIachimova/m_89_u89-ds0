package com.example.insurance_app.application.service;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.dto.client.request.UpdateClientRequest;
import com.example.insurance_app.application.dto.client.response.ClientResponse;
import com.example.insurance_app.application.exception.DuplicateIdentificationNumberException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.ClientDtoMapper;
import com.example.insurance_app.domain.model.client.Client;
import com.example.insurance_app.domain.model.client.ContactInfo;
import com.example.insurance_app.domain.model.client.vo.Address;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.IdentificationNumberChangeEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.ClientEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.client.ClientRepository;
import com.example.insurance_app.infrastructure.persistence.repository.client.IdentificationNumberChangeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service

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

    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        logger.info("Creating client with identification number: {}", request.identificationNumber());

        identificationNumberExists(request.identificationNumber());
        Client client = clientDtoMapper.toDomain(request);

        // Convert domain to entity and save
        ClientEntity entity = clientEntityMapper.toEntity(client);
        ClientEntity savedEntity = clientRepository.save(entity);
        Client savedClient = clientEntityMapper.toDomain(savedEntity);

        logger.info("Client created successfully with id: {}", savedEntity.getId());
        return clientDtoMapper.toResponse(savedClient, savedEntity, List.of());
    }

    @Transactional
    public ClientResponse updateClient(UUID clientId, UpdateClientRequest request) {
        logger.info("Updating client with id: {}", clientId);

        ClientEntity existingEntity = requireClientEntity(clientId);
        Client existingClient = clientEntityMapper.toDomain(existingEntity);

        String oldIdentificationNumber = existingClient.getIdentificationNumber();
        String newIdentificationNumber = request.identificationNumber();

        // Check if identification number is being changed and if new one is duplicate
        boolean isNumberChanged = isIdentificationNumberChanged(oldIdentificationNumber, newIdentificationNumber);
        if (isNumberChanged) {
            identificationNumberExists(newIdentificationNumber);
        }

        // Prepare updated data
        ContactInfo contactInfo = clientDtoMapper.toContactInfo(request.contactInfo());
        Address address = request.address() != null ? clientDtoMapper.toAddress(request.address()) : null;

        existingClient.updateInformation(
                request.name(),
                request.identificationNumber(),
                contactInfo,
                address
        );

        clientEntityMapper.updateEntity(existingClient, existingEntity);
        ClientEntity updatedEntity = clientRepository.save(existingEntity);

        // Track identification number change if it occurred
        if(isNumberChanged) {
            recordIdentificationNumberChange(existingEntity, oldIdentificationNumber, newIdentificationNumber);
            logger.info("Identification number changed from {} to {}", oldIdentificationNumber, request.identificationNumber());
        }

        List<IdentificationNumberChangeEntity> history =
                identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId);

        Client updatedClient = clientEntityMapper.toDomain(updatedEntity);

        logger.info("Client updated successfully with id: {}", clientId);
        return clientDtoMapper.toResponse(updatedClient, updatedEntity, history);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(UUID clientId) {
        logger.info("Fetching client with id: {}", clientId);

        ClientEntity entity = requireClientEntity(clientId);
        Client client = clientEntityMapper.toDomain(entity);

        List<IdentificationNumberChangeEntity> history =
                identificationNumberChangeRepository.findByClientIdOrderByChangedAtDesc(clientId);

        logger.info("Client fetched successfully with id: {}", clientId);
        return clientDtoMapper.toResponse(client, entity, history);
    }

    @Transactional(readOnly = true)
    public PageDto<ClientResponse> searchClients(String name, String identificationNumber, Pageable pageable) {
        logger.info("Searching clients with name: {}, identificationNumber: {}", name, identificationNumber);

        var page = clientRepository.searchClients(name, identificationNumber, pageable);

        List<ClientResponse> content = page.getContent().stream()
                .map(entity -> {
                    Client client = clientEntityMapper.toDomain(entity);
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

    private void identificationNumberExists(String identificationNumber) {
        if (clientRepository.existsByIdentificationNumber(identificationNumber)) {
            throw new DuplicateIdentificationNumberException(identificationNumber);
        }
    }

    private ClientEntity requireClientEntity(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
    }

    private boolean isIdentificationNumberChanged(String oldIdNumber, String newIdNumber) {
        return newIdNumber != null && !newIdNumber.equals(oldIdNumber);
    }

    private void recordIdentificationNumberChange(ClientEntity clientEntity, String oldIdNumber, String newIdNumber) {
        IdentificationNumberChangeEntity changeRecord = new IdentificationNumberChangeEntity(
                clientEntity,
                oldIdNumber,
                newIdNumber,
                Instant.now(),
                "system",
                "Updated via API"
        );
        identificationNumberChangeRepository.save(changeRecord);
    }


}
