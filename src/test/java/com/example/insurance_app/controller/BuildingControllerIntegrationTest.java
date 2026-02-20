package com.example.insurance_app.controller;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import com.example.insurance_app.application.dto.building.request.AddressRequest;
import com.example.insurance_app.application.dto.building.request.BuildingInfoRequest;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.request.UpdateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingDetailedResponse;
import com.example.insurance_app.application.dto.building.response.BuildingSummaryResponse;
import com.example.insurance_app.application.dto.client.ClientTypeDto;
import com.example.insurance_app.application.dto.client.request.ContactInfoRequest;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.service.BuildingService;
import com.example.insurance_app.application.service.ClientService;
import com.example.insurance_app.infrastructure.persistence.repository.building.BuildingRepository;
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

        // Act
        BuildingSummaryResponse building = buildingService.createBuilding(clientId, request);

        // Assert
        assertNotNull(building);
        assertNotNull(building.id());
        assertEquals("Main Street", building.address().street());
        assertEquals("123", building.address().streetNumber());
        assertEquals("Sector 1", building.address().city().name());
        assertEquals(2020, building.buildingInfo().constructionYear());
        assertEquals(BuildingTypeDto.RESIDENTIAL, building.buildingInfo().buildingType());
        assertEquals(5, building.buildingInfo().numberOfFloors());
        assertEquals(new BigDecimal("150.50"), building.buildingInfo().surfaceArea());
        assertEquals(new BigDecimal("200000.00"), building.buildingInfo().insuredValue());
        assertFalse(building.riskIndicators().floodZone());
        assertTrue(building.riskIndicators().earthquakeRiskZone());

        // Verify persisted in database
        assertTrue(buildingRepository.existsById(building.id()));
    }

    @Test
    @DisplayName("Should get building by ID with full geography")
    void shouldGetBuildingByIdWithFullGeography() {
        // Arrange - Create building first
        CreateBuildingRequest createRequest = new CreateBuildingRequest(
                new AddressRequest("Test Street", "456", cityId),
                new BuildingInfoRequest(
                        2021,
                        BuildingTypeDto.OFFICE,
                        10,
                        new BigDecimal("300.00"),
                        new BigDecimal("500000.00")
                ),
                new RiskIndicatorsDto(true, false)
        );

        BuildingSummaryResponse createdBuilding = buildingService.createBuilding(clientId, createRequest);

        // Act - Get building by ID
        BuildingDetailedResponse building = buildingService.getBuildingById(createdBuilding.id());

        // Assert
        assertNotNull(building);
        assertEquals(createdBuilding.id(), building.id());
        assertEquals("Test Street", building.address().street());
        assertEquals("456", building.address().streetNumber());
        assertEquals("Sector 1", building.address().city().name());
        assertEquals("Bucuresti", building.address().county().name());
        assertEquals("B", building.address().county().code());
        assertEquals("Romania", building.address().country().name());
        assertEquals(BuildingTypeDto.OFFICE, building.buildingInfo().buildingType());
        assertEquals(10, building.buildingInfo().numberOfFloors());
    }

    @Test
    @DisplayName("Should get buildings by client ID")
    void shouldGetBuildingsByClientId() {
        // Arrange - Create two buildings
        CreateBuildingRequest request1 = new CreateBuildingRequest(
                new AddressRequest("Street 1", "1", cityId),
                new BuildingInfoRequest(2020, BuildingTypeDto.RESIDENTIAL, 3, new BigDecimal("100"), new BigDecimal("100000")),
                new RiskIndicatorsDto(false, false)
        );

        CreateBuildingRequest request2 = new CreateBuildingRequest(
                new AddressRequest("Street 2", "2", cityId),
                new BuildingInfoRequest(2021, BuildingTypeDto.OFFICE, 5, new BigDecimal("200"), new BigDecimal("200000")),
                new RiskIndicatorsDto(true, true)
        );

        buildingService.createBuilding(clientId, request1);
        buildingService.createBuilding(clientId, request2);

        // Act - Get all buildings for client
        List<BuildingSummaryResponse> buildings = buildingService.getBuildingsByClientId(clientId);

        // Assert
        assertNotNull(buildings);
        assertEquals(2, buildings.size());
    }

    @Test
    @DisplayName("Should update building successfully and persist changes")
    void shouldUpdateBuildingSuccessfullyAndPersistChanges() {
        // Arrange - Create building
        CreateBuildingRequest createRequest = new CreateBuildingRequest(
                new AddressRequest("Old Street", "1", cityId),
                new BuildingInfoRequest(2020, BuildingTypeDto.RESIDENTIAL, 3, new BigDecimal("100"), new BigDecimal("100000")),
                new RiskIndicatorsDto(false, false)
        );

        BuildingSummaryResponse createdBuilding = buildingService.createBuilding(clientId, createRequest);

        // Act - Update building
        UpdateBuildingRequest updateRequest = new UpdateBuildingRequest(
                new AddressRequest("New Street", "2B", cityId),
                new BuildingInfoRequest(2021, BuildingTypeDto.OFFICE, 7, new BigDecimal("250.00"), new BigDecimal("300000.00")),
                new RiskIndicatorsDto(true, true)
        );

        BuildingSummaryResponse updatedBuilding = buildingService.updateBuilding(createdBuilding.id(), updateRequest);

        // Assert
        assertNotNull(updatedBuilding);
        assertEquals(createdBuilding.id(), updatedBuilding.id());
        assertEquals("New Street", updatedBuilding.address().street());
        assertEquals("2B", updatedBuilding.address().streetNumber());
        assertEquals(2021, updatedBuilding.buildingInfo().constructionYear());
        assertEquals(BuildingTypeDto.OFFICE, updatedBuilding.buildingInfo().buildingType());
        assertEquals(7, updatedBuilding.buildingInfo().numberOfFloors());
        assertEquals(new BigDecimal("250.00"), updatedBuilding.buildingInfo().surfaceArea());
        assertEquals(new BigDecimal("300000.00"), updatedBuilding.buildingInfo().insuredValue());
        assertTrue(updatedBuilding.riskIndicators().floodZone());
        assertTrue(updatedBuilding.riskIndicators().earthquakeRiskZone());

        // Verify changes persisted in database
        BuildingDetailedResponse retrievedBuilding = buildingService.getBuildingById(createdBuilding.id());
        assertEquals("New Street", retrievedBuilding.address().street());
        assertEquals(BuildingTypeDto.OFFICE, retrievedBuilding.buildingInfo().buildingType());
    }

    @Test
    @DisplayName("Should fail to create building when client does not exist")
    void shouldFailToCreateBuildingWhenClientDoesNotExist() {
        // Arrange
        UUID nonExistentClientId = UUID.randomUUID();
        CreateBuildingRequest request = new CreateBuildingRequest(
                new AddressRequest("Street", "1", cityId),
                new BuildingInfoRequest(2020, BuildingTypeDto.RESIDENTIAL, null, new BigDecimal("100"), new BigDecimal("100000")),
                new RiskIndicatorsDto(false, false)
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
                new AddressRequest("Street", "1", nonExistentCityId),
                new BuildingInfoRequest(2020, BuildingTypeDto.RESIDENTIAL, null, new BigDecimal("100"), new BigDecimal("100000")),
                new RiskIndicatorsDto(false, false)
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
                new AddressRequest("Street", "1", cityId),
                new BuildingInfoRequest(2020, BuildingTypeDto.RESIDENTIAL, null, new BigDecimal("100"), new BigDecimal("100000")),
                new RiskIndicatorsDto(false, false)
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
        List<BuildingSummaryResponse> buildings = buildingService.getBuildingsByClientId(clientId);

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
                new AddressRequest("Street 1", "1", cityId),
                new BuildingInfoRequest(2020, BuildingTypeDto.RESIDENTIAL, null, new BigDecimal("100"), new BigDecimal("100000")),
                new RiskIndicatorsDto(false, false)
        );
        BuildingSummaryResponse residential = buildingService.createBuilding(clientId, residentialRequest);
        assertEquals(BuildingTypeDto.RESIDENTIAL, residential.buildingInfo().buildingType());

        // Test OFFICE
        CreateBuildingRequest officeRequest = new CreateBuildingRequest(
                new AddressRequest("Street 2", "2", cityId),
                new BuildingInfoRequest(2020, BuildingTypeDto.OFFICE, null, new BigDecimal("200"), new BigDecimal("200000")),
                new RiskIndicatorsDto(false, false)
        );
        BuildingSummaryResponse office = buildingService.createBuilding(clientId, officeRequest);
        assertEquals(BuildingTypeDto.OFFICE, office.buildingInfo().buildingType());

        // Test INDUSTRIAL
        CreateBuildingRequest industrialRequest = new CreateBuildingRequest(
                new AddressRequest("Street 3", "3", cityId),
                new BuildingInfoRequest(2020, BuildingTypeDto.INDUSTRIAL, null, new BigDecimal("500"), new BigDecimal("500000")),
                new RiskIndicatorsDto(false, false)
        );
        BuildingSummaryResponse industrial = buildingService.createBuilding(clientId, industrialRequest);
        assertEquals(BuildingTypeDto.INDUSTRIAL, industrial.buildingInfo().buildingType());

        // Verify all three buildings exist
        List<BuildingSummaryResponse> buildings = buildingService.getBuildingsByClientId(clientId);
        assertEquals(3, buildings.size());
    }

    @Test
    @DisplayName("Should verify geography links are correct")
    void shouldVerifyGeographyLinksAreCorrect() {
        // Arrange
        CreateBuildingRequest request = new CreateBuildingRequest(
                new AddressRequest("Geography Test Street", "999", cityId),
                new BuildingInfoRequest(2022, BuildingTypeDto.INDUSTRIAL, 15, new BigDecimal("750.25"), new BigDecimal("1000000.00")),
                new RiskIndicatorsDto(true, true)
        );

        // Act
        BuildingSummaryResponse createdBuilding = buildingService.createBuilding(clientId, request);
        BuildingDetailedResponse detailedBuilding = buildingService.getBuildingById(createdBuilding.id());

        // Assert - Verify full geography hierarchy
        assertNotNull(detailedBuilding.address().city());
        assertEquals("Sector 1", detailedBuilding.address().city().name());
        assertNotNull(detailedBuilding.address().county());
        assertEquals("Bucuresti", detailedBuilding.address().county().name());
        assertEquals("B", detailedBuilding.address().county().code());
        assertNotNull(detailedBuilding.address().country());
        assertEquals("Romania", detailedBuilding.address().country().name());

        // Verify building is linked to correct city in database
        assertTrue(buildingRepository.existsById(createdBuilding.id()));
    }
}

