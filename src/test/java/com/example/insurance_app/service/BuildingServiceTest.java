package com.example.insurance_app.service;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.request.UpdateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingResponse;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.BuildingDtoMapper;
import com.example.insurance_app.application.service.BuildingService;
import com.example.insurance_app.domain.model.Building;
import com.example.insurance_app.domain.model.BuildingType;
import com.example.insurance_app.domain.model.vo.BuildingId;
import com.example.insurance_app.domain.model.vo.ClientId;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.BuildingEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.building.BuildingRepository;
import com.example.insurance_app.infrastructure.persistence.repository.client.ClientRepository;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingService Unit Tests")
class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private BuildingEntityMapper buildingEntityMapper;

    @Mock
    private BuildingDtoMapper buildingDtoMapper;

    @InjectMocks
    private BuildingService buildingService;

    private UUID clientId;
    private UUID cityId;
    private UUID buildingId;
    private CreateBuildingRequest createRequest;
    private UpdateBuildingRequest updateRequest;
    private Building building;
    private BuildingEntity buildingEntity;
    private BuildingResponse buildingResponse;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        cityId = UUID.randomUUID();
        buildingId = UUID.randomUUID();

        createRequest = new CreateBuildingRequest(
                "Main Street",
                "123",
                cityId,
                2020,
                BuildingTypeDto.RESIDENTIAL,
                5,
                new BigDecimal("150.50"),
                new BigDecimal("200000.00"),
                false,
                true
        );

        updateRequest = new UpdateBuildingRequest(
                "New Street",
                "456",
                cityId,
                2021,
                BuildingTypeDto.OFFICE,
                10,
                new BigDecimal("300.00"),
                new BigDecimal("500000.00"),
                true,
                false
        );

        building = new Building(
                new ClientId(clientId),
                "Main Street",
                "123",
                cityId,
                2020,
                BuildingType.RESIDENTIAL,
                5,
                new BigDecimal("150.50"),
                new BigDecimal("200000.00"),
                false,
                true
        );

        buildingEntity = new BuildingEntity(
                buildingId,
                mock(ClientEntity.class),
                "Main Street",
                "123",
                mock(CityEntity.class),
                2020,
                BuildingTypeEntity.RESIDENTIAL,
                5,
                new BigDecimal("150.50"),
                new BigDecimal("200000.00"),
                false,
                true
        );

        buildingResponse = new BuildingResponse(
                buildingId,
                clientId,
                "John Doe",
                "Main Street",
                "123",
                null,
                null,
                null,
                2020,
                BuildingTypeDto.RESIDENTIAL,
                5,
                new BigDecimal("150.50"),
                new BigDecimal("200000.00"),
                false,
                true,
                Instant.now(),
                Instant.now()
        );
    }

    @Nested
    @DisplayName("Create Building Tests")
    class CreateBuildingTests {

        @Test
        @DisplayName("Should create building successfully")
        void shouldCreateBuildingSuccessfully() {
            // Arrange
            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cityRepository.existsById(cityId)).thenReturn(true);
            when(buildingDtoMapper.toDomain(createRequest, clientId)).thenReturn(building);
            when(buildingEntityMapper.toEntity(building)).thenReturn(buildingEntity);
            when(buildingRepository.save(buildingEntity)).thenReturn(buildingEntity);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingDtoMapper.toResponse(building, buildingEntity)).thenReturn(buildingResponse);

            // Act
            BuildingResponse result = buildingService.createBuilding(clientId, createRequest);

            // Assert
            assertNotNull(result);
            assertEquals(buildingResponse.id(), result.id());
            verify(clientRepository).existsById(clientId);
            verify(cityRepository).existsById(cityId);
            verify(buildingRepository).save(buildingEntity);
        }

        @Test
        @DisplayName("Should fail when client does not exist")
        void shouldFailWhenClientDoesNotExist() {
            // Arrange
            when(clientRepository.existsById(clientId)).thenReturn(false);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.createBuilding(clientId, createRequest)
            );

            assertTrue(exception.getMessage().contains("Client"));
            verify(clientRepository).existsById(clientId);
            verify(cityRepository, never()).existsById(any());
            verify(buildingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail when city does not exist")
        void shouldFailWhenCityDoesNotExist() {
            // Arrange
            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cityRepository.existsById(cityId)).thenReturn(false);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.createBuilding(clientId, createRequest)
            );

            assertTrue(exception.getMessage().contains("City"));
            verify(clientRepository).existsById(clientId);
            verify(cityRepository).existsById(cityId);
            verify(buildingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate domain rules during creation")
        void shouldValidateDomainRulesDuringCreation() {
            // Arrange
            CreateBuildingRequest invalidRequest = new CreateBuildingRequest(
                    "Street",
                    "1",
                    cityId,
                    1700, // Invalid year
                    BuildingTypeDto.RESIDENTIAL,
                    null,
                    new BigDecimal("100"),
                    new BigDecimal("100000"),
                    false,
                    false
            );

            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cityRepository.existsById(cityId)).thenReturn(true);
            when(buildingDtoMapper.toDomain(invalidRequest, clientId))
                    .thenThrow(new com.example.insurance_app.domain.exception.DomainValidationException(
                            "Construction year must be between 1800 and 2026"
                    ));

            // Act & Assert
            assertThrows(
                    com.example.insurance_app.domain.exception.DomainValidationException.class,
                    () -> buildingService.createBuilding(clientId, invalidRequest)
            );

            verify(buildingRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Building Tests")
    class UpdateBuildingTests {

        @Test
        @DisplayName("Should update building successfully")
        void shouldUpdateBuildingSuccessfully() {
            // Arrange
            when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(buildingEntity));
            when(cityRepository.existsById(updateRequest.cityId())).thenReturn(true);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingDtoMapper.toBuildingType(updateRequest.buildingType()))
                    .thenReturn(BuildingType.OFFICE);
            when(buildingRepository.save(buildingEntity)).thenReturn(buildingEntity);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingDtoMapper.toResponse(building, buildingEntity)).thenReturn(buildingResponse);

            // Act
            BuildingResponse result = buildingService.updateBuilding(buildingId, updateRequest);

            // Assert
            assertNotNull(result);
            verify(buildingRepository).findById(buildingId);
            verify(cityRepository).existsById(updateRequest.cityId());
            verify(buildingEntityMapper).updateEntity(building, buildingEntity);
            verify(buildingRepository).save(buildingEntity);
        }

        @Test
        @DisplayName("Should fail when updating non-existent building")
        void shouldFailWhenUpdatingNonExistentBuilding() {
            // Arrange
            when(buildingRepository.findById(buildingId)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.updateBuilding(buildingId, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Building"));
            verify(buildingRepository).findById(buildingId);
            verify(buildingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail when updating with non-existent city")
        void shouldFailWhenUpdatingWithNonExistentCity() {
            // Arrange
            when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(buildingEntity));
            when(cityRepository.existsById(updateRequest.cityId())).thenReturn(false);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.updateBuilding(buildingId, updateRequest)
            );

            assertTrue(exception.getMessage().contains("City"));
            verify(buildingRepository).findById(buildingId);
            verify(cityRepository).existsById(updateRequest.cityId());
            verify(buildingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate domain rules during update")
        void shouldValidateDomainRulesDuringUpdate() {
            // Arrange
            UpdateBuildingRequest invalidRequest = new UpdateBuildingRequest(
                    "Street",
                    "1",
                    cityId,
                    2020,
                    BuildingTypeDto.RESIDENTIAL,
                    null,
                    new BigDecimal("-100"), // Negative surface area
                    new BigDecimal("100000"),
                    false,
                    false
            );

            when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(buildingEntity));
            when(cityRepository.existsById(cityId)).thenReturn(true);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingDtoMapper.toBuildingType(invalidRequest.buildingType()))
                    .thenReturn(BuildingType.RESIDENTIAL);

            // Act & Assert
            assertThrows(
                    com.example.insurance_app.domain.exception.DomainValidationException.class,
                    () -> buildingService.updateBuilding(buildingId, invalidRequest)
            );

            verify(buildingRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Building Tests")
    class GetBuildingTests {

        @Test
        @DisplayName("Should get building by ID successfully")
        void shouldGetBuildingByIdSuccessfully() {
            // Arrange
            when(buildingRepository.findByIdWithGeography(buildingId)).thenReturn(buildingEntity);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingDtoMapper.toResponse(building, buildingEntity)).thenReturn(buildingResponse);

            // Act
            BuildingResponse result = buildingService.getBuildingById(buildingId);

            // Assert
            assertNotNull(result);
            assertEquals(buildingResponse.id(), result.id());
            verify(buildingRepository).findByIdWithGeography(buildingId);
        }

        @Test
        @DisplayName("Should fail when building not found by ID")
        void shouldFailWhenBuildingNotFoundById() {
            // Arrange
            when(buildingRepository.findByIdWithGeography(buildingId)).thenReturn(null);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.getBuildingById(buildingId)
            );

            assertTrue(exception.getMessage().contains("Building"));
            verify(buildingRepository).findByIdWithGeography(buildingId);
        }

        @Test
        @DisplayName("Should use findByIdWithGeography for optimized loading")
        void shouldUseFindByIdWithGeographyForOptimizedLoading() {
            // Arrange
            when(buildingRepository.findByIdWithGeography(buildingId)).thenReturn(buildingEntity);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingDtoMapper.toResponse(building, buildingEntity)).thenReturn(buildingResponse);

            // Act
            buildingService.getBuildingById(buildingId);

            // Assert
            verify(buildingRepository).findByIdWithGeography(buildingId);
            verify(buildingRepository, never()).findById(any()); // Should not use regular findById
        }
    }

    @Nested
    @DisplayName("Get Buildings By Client Tests")
    class GetBuildingsByClientTests {

        @Test
        @DisplayName("Should get buildings by client ID successfully")
        void shouldGetBuildingsByClientIdSuccessfully() {
            // Arrange
            BuildingEntity building1 = mock(BuildingEntity.class);
            BuildingEntity building2 = mock(BuildingEntity.class);
            List<BuildingEntity> entities = List.of(building1, building2);

            Building domainBuilding1 = mock(Building.class);
            Building domainBuilding2 = mock(Building.class);

            BuildingResponse response1 = mock(BuildingResponse.class);
            BuildingResponse response2 = mock(BuildingResponse.class);

            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(buildingRepository.findByOwnerId(clientId)).thenReturn(entities);
            when(buildingEntityMapper.toDomain(building1)).thenReturn(domainBuilding1);
            when(buildingEntityMapper.toDomain(building2)).thenReturn(domainBuilding2);
            when(buildingDtoMapper.toResponse(domainBuilding1, building1)).thenReturn(response1);
            when(buildingDtoMapper.toResponse(domainBuilding2, building2)).thenReturn(response2);

            // Act
            List<BuildingResponse> result = buildingService.getBuildingsByClientId(clientId);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(clientRepository).existsById(clientId);
            verify(buildingRepository).findByOwnerId(clientId);
        }

        @Test
        @DisplayName("Should fail when client does not exist")
        void shouldFailWhenClientDoesNotExist() {
            // Arrange
            when(clientRepository.existsById(clientId)).thenReturn(false);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.getBuildingsByClientId(clientId)
            );

            assertTrue(exception.getMessage().contains("Client"));
            verify(clientRepository).existsById(clientId);
            verify(buildingRepository, never()).findByOwnerId(any());
        }

        @Test
        @DisplayName("Should return empty list when client has no buildings")
        void shouldReturnEmptyListWhenClientHasNoBuildings() {
            // Arrange
            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(buildingRepository.findByOwnerId(clientId)).thenReturn(List.of());

            // Act
            List<BuildingResponse> result = buildingService.getBuildingsByClientId(clientId);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.size());
            verify(clientRepository).existsById(clientId);
            verify(buildingRepository).findByOwnerId(clientId);
        }
    }

    @Nested
    @DisplayName("Building Owner Immutability Tests")
    class BuildingOwnerImmutabilityTests {

        @Test
        @DisplayName("Should not change owner during update")
        void shouldNotChangeOwnerDuringUpdate() {
            // Arrange
            UUID originalOwnerId = UUID.randomUUID();
            ClientEntity originalOwner = mock(ClientEntity.class);

            BuildingEntity existingEntity = new BuildingEntity(
                    buildingId,
                    originalOwner,
                    "Old Street",
                    "1",
                    mock(CityEntity.class),
                    2020,
                    BuildingTypeEntity.RESIDENTIAL,
                    3,
                    new BigDecimal("100"),
                    new BigDecimal("100000"),
                    false,
                    false
            );

            Building existingBuilding = new Building(
                    new BuildingId(buildingId),
                    new ClientId(originalOwnerId),
                    "Old Street",
                    "1",
                    cityId,
                    2020,
                    BuildingType.RESIDENTIAL,
                    3,
                    new BigDecimal("100"),
                    new BigDecimal("100000"),
                    false,
                    false
            );

            when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(existingEntity));
            when(cityRepository.existsById(updateRequest.cityId())).thenReturn(true);
            when(buildingEntityMapper.toDomain(existingEntity)).thenReturn(existingBuilding);
            when(buildingDtoMapper.toBuildingType(updateRequest.buildingType()))
                    .thenReturn(BuildingType.OFFICE);
            when(buildingRepository.save(existingEntity)).thenReturn(existingEntity);
            when(buildingEntityMapper.toDomain(existingEntity)).thenReturn(existingBuilding);
            when(buildingDtoMapper.toResponse(existingBuilding, existingEntity))
                    .thenReturn(buildingResponse);

            // Act
            buildingService.updateBuilding(buildingId, updateRequest);

            // Assert
            assertEquals(originalOwnerId, existingBuilding.getOwnerId().value());
            verify(buildingEntityMapper).updateEntity(existingBuilding, existingEntity);
            // Owner should remain the same in entity
            assertEquals(originalOwner, existingEntity.getOwner());
        }
    }

    @Nested
    @DisplayName("Building Validation Integration Tests")
    class BuildingValidationIntegrationTests {

        @Test
        @DisplayName("Should validate insured value is positive")
        void shouldValidateInsuredValueIsPositive() {
            // Arrange
            CreateBuildingRequest invalidRequest = new CreateBuildingRequest(
                    "Street",
                    "1",
                    cityId,
                    2020,
                    BuildingTypeDto.RESIDENTIAL,
                    null,
                    new BigDecimal("100"),
                    BigDecimal.ZERO, // Invalid: zero insured value
                    false,
                    false
            );

            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cityRepository.existsById(cityId)).thenReturn(true);
            when(buildingDtoMapper.toDomain(invalidRequest, clientId))
                    .thenThrow(new com.example.insurance_app.domain.exception.DomainValidationException(
                            "Insured value must be positive"
                    ));

            // Act & Assert
            assertThrows(
                    com.example.insurance_app.domain.exception.DomainValidationException.class,
                    () -> buildingService.createBuilding(clientId, invalidRequest)
            );

            verify(buildingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate surface area is positive")
        void shouldValidateSurfaceAreaIsPositive() {
            // Arrange
            CreateBuildingRequest invalidRequest = new CreateBuildingRequest(
                    "Street",
                    "1",
                    cityId,
                    2020,
                    BuildingTypeDto.RESIDENTIAL,
                    null,
                    new BigDecimal("-50"), // Invalid: negative surface area
                    new BigDecimal("100000"),
                    false,
                    false
            );

            when(clientRepository.existsById(clientId)).thenReturn(true);
            when(cityRepository.existsById(cityId)).thenReturn(true);
            when(buildingDtoMapper.toDomain(invalidRequest, clientId))
                    .thenThrow(new com.example.insurance_app.domain.exception.DomainValidationException(
                            "Surface area must be positive"
                    ));

            // Act & Assert
            assertThrows(
                    com.example.insurance_app.domain.exception.DomainValidationException.class,
                    () -> buildingService.createBuilding(clientId, invalidRequest)
            );

            verify(buildingRepository, never()).save(any());
        }
    }
}
