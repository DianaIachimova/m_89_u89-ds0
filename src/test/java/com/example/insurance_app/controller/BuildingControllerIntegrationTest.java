package com.example.insurance_app.controller;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.request.UpdateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingResponse;
import com.example.insurance_app.application.dto.client.ClientTypeDto;
import com.example.insurance_app.application.dto.client.request.ContactInfoRequest;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.service.BuildingService;
import com.example.insurance_app.application.service.ClientService;
import com.example.insurance_app.infrastructure.persistence.repository.building.BuildingRepository;
import com.example.insurance_app.infrastructure.persistence.repository.client.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Building API Integration Tests")
class BuildingControllerIntegrationTest {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ClientRepository clientRepository;

    private UUID clientId;
    private UUID cityId;

    @BeforeEach
    void setUp() {
        // Get a city ID from seeded data (from V2__geography_seed.sql)
        cityId = UUID.fromString("f1d2c3b4-5a6b-4c8d-9e0f-112233445566"); // Sector 1, Bucharest

        // Create a client first
        CreateClientRequest clientRequest = new CreateClientRequest(
                ClientTypeDto.INDIVIDUAL,
                "Test Client",
                "1234567890123",
                new ContactInfoRequest("test@example.com", "+40712345678"),
                null
        );

        clientId = clientService.createClient(clientRequest).id();
    }

    @Test
    @DisplayName("Should create building successfully and persist to database")
    void shouldCreateBuildingSuccessfullyAndPersistToDatabase() {
        // Arrange
        CreateBuildingRequest request = new CreateBuildingRequest(
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

        // Act
        BuildingResponse building = buildingService.createBuilding(clientId, request);

        // Assert
        assertNotNull(building);
        assertNotNull(building.id());
        assertEquals(clientId, building.ownerId());
        assertEquals("Test Client", building.ownerName());
        assertEquals("Main Street", building.street());
        assertEquals("123", building.streetNumber());
        assertEquals("Sector 1", building.city().name());
        assertEquals("Bucuresti", building.county().name());
        assertEquals("Romania", building.country().name());
        assertEquals(2020, building.constructionYear());
        assertEquals(BuildingTypeDto.RESIDENTIAL, building.buildingType());
        assertEquals(5, building.numberOfFloors());
        assertEquals(new BigDecimal("150.50"), building.surfaceArea());
        assertEquals(new BigDecimal("200000.00"), building.insuredValue());
        assertFalse(building.floodZone());
        assertTrue(building.earthquakeRiskZone());
        assertNotNull(building.createdAt());
        assertNotNull(building.updatedAt());

        // Verify persisted in database
        assertTrue(buildingRepository.existsById(building.id()));
    }

    @Test
    @DisplayName("Should get building by ID with full geography")
    void shouldGetBuildingByIdWithFullGeography() {
        // Arrange - Create building first
        CreateBuildingRequest createRequest = new CreateBuildingRequest(
                "Test Street",
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

        BuildingResponse createdBuilding = buildingService.createBuilding(clientId, createRequest);

        // Act - Get building by ID
        BuildingResponse building = buildingService.getBuildingById(createdBuilding.id());

        // Assert
        assertNotNull(building);
        assertEquals(createdBuilding.id(), building.id());
        assertEquals("Test Street", building.street());
        assertEquals("456", building.streetNumber());
        assertEquals("Sector 1", building.city().name());
        assertEquals("Bucuresti", building.county().name());
        assertEquals("B", building.county().code());
        assertEquals("Romania", building.country().name());
        assertEquals(BuildingTypeDto.OFFICE, building.buildingType());
        assertEquals(10, building.numberOfFloors());
    }

    @Test
    @DisplayName("Should get buildings by client ID")
    void shouldGetBuildingsByClientId() {
        // Arrange - Create two buildings
        CreateBuildingRequest request1 = new CreateBuildingRequest(
                "Street 1", "1", cityId, 2020, BuildingTypeDto.RESIDENTIAL,
                3, new BigDecimal("100"), new BigDecimal("100000"), false, false
        );

        CreateBuildingRequest request2 = new CreateBuildingRequest(
                "Street 2", "2", cityId, 2021, BuildingTypeDto.OFFICE,
                5, new BigDecimal("200"), new BigDecimal("200000"), true, true
        );

        buildingService.createBuilding(clientId, request1);
        buildingService.createBuilding(clientId, request2);

        // Act - Get all buildings for client
        List<BuildingResponse> buildings = buildingService.getBuildingsByClientId(clientId);

        // Assert
        assertNotNull(buildings);
        assertEquals(2, buildings.size());
        assertEquals(clientId, buildings.get(0).ownerId());
        assertEquals(clientId, buildings.get(1).ownerId());
    }

    @Test
    @DisplayName("Should update building successfully and persist changes")
    void shouldUpdateBuildingSuccessfullyAndPersistChanges() {
        // Arrange - Create building
        CreateBuildingRequest createRequest = new CreateBuildingRequest(
                "Old Street", "1", cityId, 2020, BuildingTypeDto.RESIDENTIAL,
                3, new BigDecimal("100"), new BigDecimal("100000"), false, false
        );

        BuildingResponse createdBuilding = buildingService.createBuilding(clientId, createRequest);

        // Act - Update building
        UpdateBuildingRequest updateRequest = new UpdateBuildingRequest(
                "New Street",
                "2B",
                cityId,
                2021,
                BuildingTypeDto.OFFICE,
                7,
                new BigDecimal("250.00"),
                new BigDecimal("300000.00"),
                true,
                true
        );

        BuildingResponse updatedBuilding = buildingService.updateBuilding(createdBuilding.id(), updateRequest);

        // Assert
        assertNotNull(updatedBuilding);
        assertEquals(createdBuilding.id(), updatedBuilding.id());
        assertEquals("New Street", updatedBuilding.street());
        assertEquals("2B", updatedBuilding.streetNumber());
        assertEquals(2021, updatedBuilding.constructionYear());
        assertEquals(BuildingTypeDto.OFFICE, updatedBuilding.buildingType());
        assertEquals(7, updatedBuilding.numberOfFloors());
        assertEquals(new BigDecimal("250.00"), updatedBuilding.surfaceArea());
        assertEquals(new BigDecimal("300000.00"), updatedBuilding.insuredValue());
        assertTrue(updatedBuilding.floodZone());
        assertTrue(updatedBuilding.earthquakeRiskZone());
        assertEquals(clientId, updatedBuilding.ownerId()); // Owner should not change

        // Verify changes persisted in database
        BuildingResponse retrievedBuilding = buildingService.getBuildingById(createdBuilding.id());
        assertEquals("New Street", retrievedBuilding.street());
        assertEquals(BuildingTypeDto.OFFICE, retrievedBuilding.buildingType());
    }

    @Test
    @DisplayName("Should fail to create building when client does not exist")
    void shouldFailToCreateBuildingWhenClientDoesNotExist() {
        // Arrange
        UUID nonExistentClientId = UUID.randomUUID();
        CreateBuildingRequest request = new CreateBuildingRequest(
                "Street", "1", cityId, 2020, BuildingTypeDto.RESIDENTIAL,
                null, new BigDecimal("100"), new BigDecimal("100000"), false, false
        );

        // Act & Assert
        assertThrows(
                com.example.insurance_app.application.exception.ResourceNotFoundException.class,
                () -> buildingService.createBuilding(nonExistentClientId, request)
        );
    }

    @Test
    @DisplayName("Should fail to create building when city does not exist")
    void shouldFailToCreateBuildingWhenCityDoesNotExist() {
        // Arrange
        UUID nonExistentCityId = UUID.randomUUID();
        CreateBuildingRequest request = new CreateBuildingRequest(
                "Street", "1", nonExistentCityId, 2020, BuildingTypeDto.RESIDENTIAL,
                null, new BigDecimal("100"), new BigDecimal("100000"), false, false
        );

        // Act & Assert
        assertThrows(
                com.example.insurance_app.application.exception.ResourceNotFoundException.class,
                () -> buildingService.createBuilding(clientId, request)
        );
    }

    @Test
    @DisplayName("Should fail to get building when building does not exist")
    void shouldFailToGetBuildingWhenBuildingDoesNotExist() {
        // Arrange
        UUID nonExistentBuildingId = UUID.randomUUID();

        // Act & Assert
        assertThrows(
                com.example.insurance_app.application.exception.ResourceNotFoundException.class,
                () -> buildingService.getBuildingById(nonExistentBuildingId)
        );
    }

    @Test
    @DisplayName("Should fail to update building when building does not exist")
    void shouldFailToUpdateBuildingWhenBuildingDoesNotExist() {
        // Arrange
        UUID nonExistentBuildingId = UUID.randomUUID();
        UpdateBuildingRequest request = new UpdateBuildingRequest(
                "Street", "1", cityId, 2020, BuildingTypeDto.RESIDENTIAL,
                null, new BigDecimal("100"), new BigDecimal("100000"), false, false
        );

        // Act & Assert
        assertThrows(
                com.example.insurance_app.application.exception.ResourceNotFoundException.class,
                () -> buildingService.updateBuilding(nonExistentBuildingId, request)
        );
    }

    @Test
    @DisplayName("Should return empty list when client has no buildings")
    void shouldReturnEmptyListWhenClientHasNoBuildings() {
        // Act
        List<BuildingResponse> buildings = buildingService.getBuildingsByClientId(clientId);

        // Assert
        assertNotNull(buildings);
        assertEquals(0, buildings.size());
    }

    @Test
    @DisplayName("Should fail to get buildings when client does not exist")
    void shouldFailToGetBuildingsWhenClientDoesNotExist() {
        // Arrange
        UUID nonExistentClientId = UUID.randomUUID();

        // Act & Assert
        assertThrows(
                com.example.insurance_app.application.exception.ResourceNotFoundException.class,
                () -> buildingService.getBuildingsByClientId(nonExistentClientId)
        );
    }

    @Test
    @DisplayName("Should create building with all building types")
    void shouldCreateBuildingWithAllBuildingTypes() {
        // Test RESIDENTIAL
        CreateBuildingRequest residentialRequest = new CreateBuildingRequest(
                "Street 1", "1", cityId, 2020, BuildingTypeDto.RESIDENTIAL,
                null, new BigDecimal("100"), new BigDecimal("100000"), false, false
        );
        BuildingResponse residential = buildingService.createBuilding(clientId, residentialRequest);
        assertEquals(BuildingTypeDto.RESIDENTIAL, residential.buildingType());

        // Test OFFICE
        CreateBuildingRequest officeRequest = new CreateBuildingRequest(
                "Street 2", "2", cityId, 2020, BuildingTypeDto.OFFICE,
                null, new BigDecimal("200"), new BigDecimal("200000"), false, false
        );
        BuildingResponse office = buildingService.createBuilding(clientId, officeRequest);
        assertEquals(BuildingTypeDto.OFFICE, office.buildingType());

        // Test INDUSTRIAL
        CreateBuildingRequest industrialRequest = new CreateBuildingRequest(
                "Street 3", "3", cityId, 2020, BuildingTypeDto.INDUSTRIAL,
                null, new BigDecimal("500"), new BigDecimal("500000"), false, false
        );
        BuildingResponse industrial = buildingService.createBuilding(clientId, industrialRequest);
        assertEquals(BuildingTypeDto.INDUSTRIAL, industrial.buildingType());

        // Verify all three buildings exist
        List<BuildingResponse> buildings = buildingService.getBuildingsByClientId(clientId);
        assertEquals(3, buildings.size());
    }

    @Test
    @DisplayName("Should verify geography links are correct")
    void shouldVerifyGeographyLinksAreCorrect() {
        // Arrange
        CreateBuildingRequest request = new CreateBuildingRequest(
                "Geography Test Street",
                "999",
                cityId,
                2022,
                BuildingTypeDto.INDUSTRIAL,
                15,
                new BigDecimal("750.25"),
                new BigDecimal("1000000.00"),
                true,
                true
        );

        // Act
        BuildingResponse createdBuilding = buildingService.createBuilding(clientId, request);

        // Assert - Verify full geography hierarchy
        assertNotNull(createdBuilding.city());
        assertEquals("Sector 1", createdBuilding.city().name());
        assertNotNull(createdBuilding.county());
        assertEquals("Bucuresti", createdBuilding.county().name());
        assertEquals("B", createdBuilding.county().code());
        assertNotNull(createdBuilding.country());
        assertEquals("Romania", createdBuilding.country().name());

        // Verify building is linked to correct city in database
        assertTrue(buildingRepository.existsById(createdBuilding.id()));
    }
}
