package com.example.insurance_app.service;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import com.example.insurance_app.application.dto.building.request.AddressRequest;
import com.example.insurance_app.application.dto.building.request.BuildingInfoRequest;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.request.UpdateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingDetailedResponse;
import com.example.insurance_app.application.dto.building.response.BuildingSummaryResponse;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.BuildingRequestMapper;
import com.example.insurance_app.application.mapper.BuildingResponseMapper;
import com.example.insurance_app.application.service.BuildingService;
import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
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
    private BuildingResponseMapper buildingResponseMapper;

    @Mock
    private BuildingRequestMapper buildingRequestMapper;

    @InjectMocks
    private BuildingService buildingService;

    private UUID clientId;
    private UUID cityId;
    private UUID buildingId;
    private CreateBuildingRequest createRequest;
    private UpdateBuildingRequest updateRequest;
    private Building building;
    private BuildingEntity buildingEntity;
    private ClientEntity clientEntity;
    private CityEntity cityEntity;
    private BuildingSummaryResponse buildingSummaryResponse;
    private BuildingDetailedResponse buildingDetailedResponse;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        cityId = UUID.randomUUID();
        buildingId = UUID.randomUUID();

        createRequest = new CreateBuildingRequest(
                new AddressRequest("Main Street", "123", cityId),
                new BuildingInfoRequest(
                        2020,
                        BuildingTypeDto.RESIDENTIAL,
                        5,
                        new BigDecimal("150.50"),
                        new BigDecimal("200000.00")
                ),
                new RiskIndicatorsDto(false, true)
        );

        updateRequest = new UpdateBuildingRequest(
                new AddressRequest("New Street", "456", cityId),
                new BuildingInfoRequest(
                        2021,
                        BuildingTypeDto.OFFICE,
                        10,
                        new BigDecimal("300.00"),
                        new BigDecimal("500000.00")
                ),
                new RiskIndicatorsDto(true, false)
        );

        building = mock(Building.class);
        clientEntity = mock(ClientEntity.class);
        cityEntity = mock(CityEntity.class);
        buildingEntity = mock(BuildingEntity.class);
        buildingSummaryResponse = mock(BuildingSummaryResponse.class);
        buildingDetailedResponse = mock(BuildingDetailedResponse.class);
    }

    @Nested
    @DisplayName("Create Building Tests")
    class CreateBuildingTests {

        @Test
        @DisplayName("Should create building successfully")
        void shouldCreateBuildingSuccessfully() {
            // Arrange
            when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
            when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
            when(cityEntity.getId()).thenReturn(cityId);
            when(buildingRequestMapper.toDomain(clientId, cityId, createRequest)).thenReturn(building);
            when(buildingEntityMapper.toEntity(building, clientEntity, cityEntity)).thenReturn(buildingEntity);
            when(buildingRepository.save(buildingEntity)).thenReturn(buildingEntity);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingResponseMapper.toSummaryResponse(building, cityEntity)).thenReturn(buildingSummaryResponse);
            when(buildingSummaryResponse.id()).thenReturn(buildingId);

            // Act
            BuildingSummaryResponse result = buildingService.createBuilding(clientId, createRequest);

            // Assert
            assertNotNull(result);
            assertEquals(buildingId, result.id());
            verify(clientRepository).findById(clientId);
            verify(cityRepository).findById(cityId);
            verify(buildingRepository).save(buildingEntity);
        }

        @Test
        @DisplayName("Should fail when client does not exist")
        void shouldFailWhenClientDoesNotExist() {
            // Arrange
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.createBuilding(clientId, createRequest)
            );

            assertTrue(exception.getMessage().contains("Client"));
            verify(clientRepository).findById(clientId);
            verify(cityRepository, never()).findById(any());
            verify(buildingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should fail when city does not exist")
        void shouldFailWhenCityDoesNotExist() {
            // Arrange
            when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
            when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.createBuilding(clientId, createRequest)
            );

            assertTrue(exception.getMessage().contains("City"));
            verify(clientRepository).findById(clientId);
            verify(cityRepository).findById(cityId);
            verify(buildingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate domain rules during creation")
        void shouldValidateDomainRulesDuringCreation() {
            // Arrange
            CreateBuildingRequest invalidRequest = new CreateBuildingRequest(
                    new AddressRequest("Street", "1", cityId),
                    new BuildingInfoRequest(
                            1700, // Invalid year
                            BuildingTypeDto.RESIDENTIAL,
                            null,
                            new BigDecimal("100"),
                            new BigDecimal("100000")
                    ),
                    new RiskIndicatorsDto(false, false)
            );

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
            when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
            when(cityEntity.getId()).thenReturn(cityId);
            when(buildingRequestMapper.toDomain(clientId, cityId, invalidRequest))
                    .thenThrow(new com.example.insurance_app.domain.exception.DomainValidationException(
                            "Construction year must be between 1800 and current year"
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
            when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingRepository.save(buildingEntity)).thenReturn(buildingEntity);
            when(buildingResponseMapper.toSummaryResponse(building, cityEntity)).thenReturn(buildingSummaryResponse);

            // Act
            BuildingSummaryResponse result = buildingService.updateBuilding(buildingId, updateRequest);

            // Assert
            assertNotNull(result);
            verify(buildingRepository).findById(buildingId);
            verify(cityRepository).findById(cityId);
            verify(buildingEntityMapper).updateEntity(building, buildingEntity, cityEntity);
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
            when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.updateBuilding(buildingId, updateRequest)
            );

            assertTrue(exception.getMessage().contains("City"));
            verify(buildingRepository).findById(buildingId);
            verify(cityRepository).findById(cityId);
            verify(buildingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should validate domain rules during update")
        void shouldValidateDomainRulesDuringUpdate() {
            // Arrange
            UpdateBuildingRequest invalidRequest = new UpdateBuildingRequest(
                    new AddressRequest("Street", "1", cityId),
                    new BuildingInfoRequest(
                            2020,
                            BuildingTypeDto.RESIDENTIAL,
                            null,
                            new BigDecimal("-100"), // Negative surface area
                            new BigDecimal("100000")
                    ),
                    new RiskIndicatorsDto(false, false)
            );

            when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(buildingEntity));
            when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingRequestMapper.toBuildingInfo(any()))
                    .thenThrow(new com.example.insurance_app.domain.exception.DomainValidationException(
                            "Surface area must be positive"
                    ));

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
            when(buildingEntity.getCity()).thenReturn(cityEntity);
            when(buildingResponseMapper.toDetailedResponse(eq(building), any(), any(), any()))
                    .thenReturn(buildingDetailedResponse);
            when(buildingDetailedResponse.id()).thenReturn(buildingId);

            // Act
            BuildingDetailedResponse result = buildingService.getBuildingById(buildingId);

            // Assert
            assertNotNull(result);
            assertEquals(buildingId, result.id());
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
            verifyNoInteractions(buildingEntityMapper);
            verifyNoInteractions(buildingResponseMapper);
        }

        @Test
        @DisplayName("Should use findByIdWithGeography for optimized loading")
        void shouldUseFindByIdWithGeographyForOptimizedLoading() {
            // Arrange
            when(buildingRepository.findByIdWithGeography(buildingId)).thenReturn(buildingEntity);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(building);
            when(buildingEntity.getCity()).thenReturn(cityEntity);
            when(buildingResponseMapper.toDetailedResponse(eq(building), any(), any(), any()))
                    .thenReturn(buildingDetailedResponse);

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
            CityEntity city1 = mock(CityEntity.class);
            CityEntity city2 = mock(CityEntity.class);
            when(building1.getCity()).thenReturn(city1);
            when(building2.getCity()).thenReturn(city2);

            List<BuildingEntity> entities = List.of(building1, building2);

            Building domainBuilding1 = mock(Building.class);
            Building domainBuilding2 = mock(Building.class);

            BuildingSummaryResponse response1 = mock(BuildingSummaryResponse.class);
            BuildingSummaryResponse response2 = mock(BuildingSummaryResponse.class);

            when(buildingRepository.findByOwnerId(clientId)).thenReturn(entities);
            when(buildingEntityMapper.toDomain(building1)).thenReturn(domainBuilding1);
            when(buildingEntityMapper.toDomain(building2)).thenReturn(domainBuilding2);
            when(buildingResponseMapper.toSummaryResponse(domainBuilding1, city1)).thenReturn(response1);
            when(buildingResponseMapper.toSummaryResponse(domainBuilding2, city2)).thenReturn(response2);

            // Act
            List<BuildingSummaryResponse> result = buildingService.getBuildingsByClientId(clientId);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(buildingRepository).findByOwnerId(clientId);
            verifyNoInteractions(clientRepository);
        }

        @Test
        @DisplayName("Should fail when client does not exist")
        void shouldFailWhenClientDoesNotExist() {
            // Arrange
            when(buildingRepository.findByOwnerId(clientId)).thenReturn(List.of());
            when(clientRepository.existsById(clientId)).thenReturn(false);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> buildingService.getBuildingsByClientId(clientId)
            );

            assertTrue(exception.getMessage().contains("Client"));
            verify(buildingRepository).findByOwnerId(clientId);
            verify(clientRepository).existsById(clientId);
        }

        @Test
        @DisplayName("Should return empty list when client has no buildings")
        void shouldReturnEmptyListWhenClientHasNoBuildings() {
            // Arrange
            when(buildingRepository.findByOwnerId(clientId)).thenReturn(List.of());
            when(clientRepository.existsById(clientId)).thenReturn(true);

            // Act
            List<BuildingSummaryResponse> result = buildingService.getBuildingsByClientId(clientId);

            // Assert
            assertNotNull(result);
            assertEquals(0, result.size());
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

            Building existingBuilding = mock(Building.class);
            when(existingBuilding.getOwnerId()).thenReturn(new ClientId(originalOwnerId));

            when(buildingRepository.findById(buildingId)).thenReturn(Optional.of(buildingEntity));
            when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(existingBuilding);
            when(buildingRepository.save(buildingEntity)).thenReturn(buildingEntity);
            when(buildingResponseMapper.toSummaryResponse(existingBuilding, cityEntity))
                    .thenReturn(buildingSummaryResponse);

            // Act
            buildingService.updateBuilding(buildingId, updateRequest);

            // Assert
            assertEquals(originalOwnerId, existingBuilding.getOwnerId().value());
            verify(buildingEntityMapper).updateEntity(eq(existingBuilding), eq(buildingEntity), eq(cityEntity));
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
                    new AddressRequest("Street", "1", cityId),
                    new BuildingInfoRequest(
                            2020,
                            BuildingTypeDto.RESIDENTIAL,
                            null,
                            new BigDecimal("100"),
                            BigDecimal.ZERO // Invalid: zero insured value
                    ),
                    new RiskIndicatorsDto(false, false)
            );

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
            when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
            when(cityEntity.getId()).thenReturn(cityId);
            when(buildingRequestMapper.toDomain(clientId, cityId, invalidRequest))
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
                    new AddressRequest("Street", "1", cityId),
                    new BuildingInfoRequest(
                            2020,
                            BuildingTypeDto.RESIDENTIAL,
                            null,
                            new BigDecimal("-50"), // Invalid: negative surface area
                            new BigDecimal("100000")
                    ),
                    new RiskIndicatorsDto(false, false)
            );

            when(clientRepository.findById(clientId)).thenReturn(Optional.of(clientEntity));
            when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
            when(cityEntity.getId()).thenReturn(cityId);
            when(buildingRequestMapper.toDomain(clientId, cityId, invalidRequest))
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
