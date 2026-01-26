package com.example.insurance_app.application.service;

import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.request.UpdateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingResponse;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.BuildingDtoMapper;
import com.example.insurance_app.domain.model.Building;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.BuildingEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.building.BuildingRepository;
import com.example.insurance_app.infrastructure.persistence.repository.client.ClientRepository;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BuildingService {

    private static final Logger logger = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final ClientRepository clientRepository;
    private final CityRepository cityRepository;
    private final BuildingEntityMapper buildingEntityMapper;
    private final BuildingDtoMapper buildingDtoMapper;

    public BuildingService(BuildingRepository buildingRepository,
                           ClientRepository clientRepository,
                           CityRepository cityRepository,
                           BuildingEntityMapper buildingEntityMapper,
                           BuildingDtoMapper buildingDtoMapper) {
        this.buildingRepository = buildingRepository;
        this.clientRepository = clientRepository;
        this.cityRepository = cityRepository;
        this.buildingEntityMapper = buildingEntityMapper;
        this.buildingDtoMapper = buildingDtoMapper;
    }

    public BuildingResponse createBuilding(UUID clientId, CreateBuildingRequest request) {
        logger.info("Creating building for client: {}", clientId);

        // Validate client exists
        if (!clientRepository.existsById(clientId)) {
            logger.warn("Client with id {} not found", clientId);
            throw new ResourceNotFoundException("Client", "id", clientId);
        }

        // Validate city exists
        if (!cityRepository.existsById(request.cityId())) {
            logger.warn("City with id {} not found", request.cityId());
            throw new ResourceNotFoundException("City", "id", request.cityId());
        }

        // Convert DTO to domain model (validates business rules)
        Building building = buildingDtoMapper.toDomain(request, clientId);

        // Convert domain to entity and save
        BuildingEntity entity = buildingEntityMapper.toEntity(building);
        BuildingEntity savedEntity = buildingRepository.save(entity);

        // Convert back to domain with ID
        Building savedBuilding = buildingEntityMapper.toDomain(savedEntity);

        logger.info("Building created successfully with id: {}", savedEntity.getId());

        return buildingDtoMapper.toResponse(savedBuilding, savedEntity);
    }

    @Transactional
    public BuildingResponse updateBuilding(UUID buildingId, UpdateBuildingRequest request) {
        logger.info("Updating building with id: {}", buildingId);

        // Find existing building
        BuildingEntity existingEntity = buildingRepository.findById(buildingId)
                .orElseThrow(() -> {
                    logger.warn("Building with id {} not found", buildingId);
                    return new ResourceNotFoundException("Building", "id", buildingId);
                });

        // Validate city exists
        if (!cityRepository.existsById(request.cityId())) {
            logger.warn("City with id {} not found", request.cityId());
            throw new ResourceNotFoundException("City", "id", request.cityId());
        }

        // Convert to domain
        Building existingBuilding = buildingEntityMapper.toDomain(existingEntity);

        // Update domain model (validates business rules)
        existingBuilding.updateInformation(
                request.street(),
                request.streetNumber(),
                request.cityId(),
                request.constructionYear(),
                buildingDtoMapper.toBuildingType(request.buildingType()),
                request.numberOfFloors(),
                request.surfaceArea(),
                request.insuredValue(),
                request.floodZone(),
                request.earthquakeRiskZone()
        );

        // Update entity
        buildingEntityMapper.updateEntity(existingBuilding, existingEntity);
        BuildingEntity updatedEntity = buildingRepository.save(existingEntity);

        Building updatedBuilding = buildingEntityMapper.toDomain(updatedEntity);

        logger.info("Building updated successfully with id: {}", buildingId);

        return buildingDtoMapper.toResponse(updatedBuilding, updatedEntity);
    }

    @Transactional(readOnly = true)
    public BuildingResponse getBuildingById(UUID buildingId) {
        logger.info("Fetching building with id: {}", buildingId);

        // Use query with geography fetching for complete response
        BuildingEntity entity = buildingRepository.findByIdWithGeography(buildingId);

        if (entity == null) {
            logger.warn("Building with id {} not found", buildingId);
            throw new ResourceNotFoundException("Building", "id", buildingId);
        }

        Building building = buildingEntityMapper.toDomain(entity);

        logger.info("Building fetched successfully with id: {}", buildingId);

        return buildingDtoMapper.toResponse(building, entity);
    }

    @Transactional(readOnly = true)
    public List<BuildingResponse> getBuildingsByClientId(UUID clientId) {
        logger.info("Fetching buildings for client: {}", clientId);

        // Validate client exists
        if (!clientRepository.existsById(clientId)) {
            logger.warn("Client with id {} not found", clientId);
            throw new ResourceNotFoundException("Client", "id", clientId);
        }

        List<BuildingEntity> entities = buildingRepository.findByOwnerId(clientId);

        List<BuildingResponse> responses = entities.stream()
                .map(entity -> {
                    Building building = buildingEntityMapper.toDomain(entity);
                    return buildingDtoMapper.toResponse(building, entity);
                })
                .toList();

        logger.info("Found {} buildings for client {}", responses.size(), clientId);

        return responses;
    }
}
