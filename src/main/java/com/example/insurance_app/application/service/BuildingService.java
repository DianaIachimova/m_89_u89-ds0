package com.example.insurance_app.application.service;

import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.request.UpdateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingDetailedResponse;
import com.example.insurance_app.application.dto.building.response.BuildingSummaryResponse;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.BuildingRequestMapper;
import com.example.insurance_app.application.mapper.BuildingResponseMapper;
import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountryEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountyEntity;
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
public class BuildingService {

    private static final Logger logger = LoggerFactory.getLogger(BuildingService.class);

    private final BuildingRepository buildingRepository;
    private final ClientRepository clientRepository;
    private final CityRepository cityRepository;

    private final BuildingEntityMapper buildingEntityMapper;
    private final BuildingResponseMapper buildingResponseMapper;
    private final BuildingRequestMapper  buildingRequestMapper;

    public BuildingService(BuildingRepository buildingRepository,
                           ClientRepository clientRepository,
                           CityRepository cityRepository,
                           BuildingEntityMapper buildingEntityMapper,
                           BuildingResponseMapper buildingResponseMapper,
                           BuildingRequestMapper  buildingRequestMapper) {
        this.buildingRepository = buildingRepository;
        this.clientRepository = clientRepository;
        this.cityRepository = cityRepository;
        this.buildingEntityMapper = buildingEntityMapper;
        this.buildingResponseMapper = buildingResponseMapper;
        this.buildingRequestMapper = buildingRequestMapper;
    }
    @Transactional
    public BuildingSummaryResponse createBuilding(UUID clientId, CreateBuildingRequest request) {
        logger.info("Creating building for client: {}", clientId);

        ClientEntity owner = requireClient(clientId);
        CityEntity city = requireCity(request.address().cityId());

        Building building = buildingRequestMapper.toDomain(clientId, city.getId(), request);

        // Convert domain to entity and save
        BuildingEntity entity = buildingEntityMapper.toEntity(building, owner, city);
        BuildingEntity savedEntity = buildingRepository.save(entity);

        // Convert back to domain with ID
        Building savedBuilding = buildingEntityMapper.toDomain(savedEntity);

        logger.info("Building created successfully with id: {}", savedEntity.getId());

        return buildingResponseMapper.toSummaryResponse(savedBuilding, city);
    }

    @Transactional
    public BuildingSummaryResponse updateBuilding(UUID buildingId, UpdateBuildingRequest request) {
        logger.info("Updating building with id: {}", buildingId);

        BuildingEntity existingEntity = requireBuilding(buildingId);
        CityEntity city =requireCity(request.address().cityId());

        // Convert to domain
        Building existingBuilding = buildingEntityMapper.toDomain(existingEntity);

        BuildingAddress address = buildingRequestMapper.toBuildingAddress(request.address());
        BuildingInfo info = buildingRequestMapper.toBuildingInfo(request.buildingDetails());
        RiskIndicators risk = buildingRequestMapper.toRiskIndicators(request.riskIndicators());

        // Update domain model (validates business rules)
        existingBuilding.updateInformation(address, city.getId(), info, risk);

        // Update entity
        buildingEntityMapper.updateEntity(existingBuilding, existingEntity, city);
        BuildingEntity updatedEntity = buildingRepository.save(existingEntity);

        Building updatedBuilding = buildingEntityMapper.toDomain(updatedEntity);

        logger.info("Building updated successfully with id: {}", buildingId);

        return buildingResponseMapper.toSummaryResponse(updatedBuilding, city);
    }

    @Transactional(readOnly = true)
    public BuildingDetailedResponse getBuildingById(UUID buildingId) {
        logger.info("Fetching building with id: {}", buildingId);

        // Use query with geography fetching for complete response
        BuildingEntity entity = buildingRepository.findByIdWithGeography(buildingId);
        if (entity == null) {
            throw new ResourceNotFoundException("Building", "id", buildingId);
        }

        Building building = buildingEntityMapper.toDomain(entity);

        CityEntity city = entity.getCity();
        CountyEntity county = city !=null ? city.getCounty() : null;
        CountryEntity country = county != null ? county.getCountry() : null;


        logger.info("Building fetched successfully with id: {}", buildingId);
        return buildingResponseMapper.toDetailedResponse(building, city, county, country);
    }

    @Transactional(readOnly = true)
    public List<BuildingSummaryResponse> getBuildingsByClientId(UUID clientId) {
        logger.info("Fetching buildings for client: {}", clientId);

        List<BuildingEntity> entities = buildingRepository.findByOwnerId(clientId);

        if(entities.isEmpty() && !clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", "id", clientId);
        }

        List<BuildingSummaryResponse> responses = entities.stream()
                .map(entity -> {
                    Building building = buildingEntityMapper.toDomain(entity);
                    return buildingResponseMapper.toSummaryResponse(building, entity.getCity());
                })
                .toList();

        logger.info("Found {} buildings for client {}", responses.size(), clientId);
        return responses;
    }

    private ClientEntity requireClient(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
    }


    private CityEntity requireCity(UUID cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", cityId));
    }

    private BuildingEntity requireBuilding(UUID buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building", "id", buildingId));
    }
}

