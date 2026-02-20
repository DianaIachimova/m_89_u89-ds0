package com.example.insurance_app.domain.building;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Building Domain Model Tests")
class BuildingTest {

    private final UUID clientId = UUID.randomUUID();
    private final UUID cityId = UUID.randomUUID();

    private Building defaultBuilding() {
        return new Building(
                new ClientId(clientId),
                new BuildingAddress("Street", "1"),
                cityId,
                new BuildingInfo(2020, BuildingType.RESIDENTIAL, 3, new BigDecimal("100.00"), new BigDecimal("100000.00")),
                null
        );
    }

    private Building buildingWithOriginalData(RiskIndicators risk) {
        return new Building(
                new ClientId(clientId),
                new BuildingAddress("Old Street", "1"),
                cityId,
                new BuildingInfo(2020, BuildingType.RESIDENTIAL, 3, new BigDecimal("100.00"), new BigDecimal("100000.00")),
                risk
        );
    }

    @Nested
    @DisplayName("Building Creation Tests")
    class BuildingCreationTests {

        @Test
        @DisplayName("Should create building without ID (new building)")
        void shouldCreateBuildingWithoutId() {
            // Arrange
            BuildingAddress address = new BuildingAddress("Main Street", "123");
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    5,
                    new BigDecimal("150.50"),
                    new BigDecimal("200000.00")
            );
            RiskIndicators risk = new RiskIndicators(false, true);

            // Act
            Building building = new Building(
                    new ClientId(clientId),
                    address,
                    cityId,
                    info,
                    risk
            );

            // Assert
            assertNotNull(building);
            assertNull(building.getId());
            assertEquals(clientId, building.getOwnerId().value());
            assertEquals(address, building.getAddress());
            assertEquals(cityId, building.getCityId());
            assertEquals(info, building.getBuildingInfo());
            assertEquals(risk, building.getRiskIndicators());
        }

        @Test
        @DisplayName("Should create building with ID (existing building)")
        void shouldCreateBuildingWithId() {
            // Arrange
            UUID buildingId = UUID.randomUUID();
            BuildingAddress address = new BuildingAddress("Main Street", "123");
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    5,
                    new BigDecimal("150.50"),
                    new BigDecimal("200000.00")
            );
            RiskIndicators risk = new RiskIndicators(false, true);

            // Act
            Building building = new Building(
                    new BuildingId(buildingId),
                    new ClientId(clientId),
                    address,
                    cityId,
                    info,
                    risk
            );

            // Assert
            assertNotNull(building);
            assertEquals(buildingId, building.getId().value());
            assertEquals(clientId, building.getOwnerId().value());
        }

        @Test
        @DisplayName("Should create building without risk indicators")
        void shouldCreateBuildingWithoutRiskIndicators() {
            // Arrange
            BuildingAddress address = new BuildingAddress("Main Street", "123");
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.OFFICE,
                    10,
                    new BigDecimal("300.00"),
                    new BigDecimal("500000.00")
            );

            // Act
            Building building = new Building(
                    new ClientId(clientId),
                    address,
                    cityId,
                    info,
                    null
            );

            // Assert
            assertNotNull(building);
            assertNull(building.getRiskIndicators());
        }
    }

    @Nested
    @DisplayName("Building Validation Tests")
    class BuildingValidationTests {

        @Test
        @DisplayName("Should fail when address is null")
        void shouldFailWhenAddressIsNull() {
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    5,
                    new BigDecimal("150.50"),
                    new BigDecimal("200000.00")
            );
            ClientId ownerId = new ClientId(clientId);
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Building(ownerId, null, cityId, info, null)
            );
            assertTrue(exception.getMessage().contains("address"));
        }

        @Test
        @DisplayName("Should fail when cityId is null")
        void shouldFailWhenCityIdIsNull() {
            BuildingAddress address = new BuildingAddress("Main Street", "123");
            BuildingInfo info = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    5,
                    new BigDecimal("150.50"),
                    new BigDecimal("200000.00")
            );
            ClientId ownerId = new ClientId(clientId);
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Building(ownerId, address, null, info, null)
            );
            assertTrue(exception.getMessage().contains("cityId"));
        }

        @Test
        @DisplayName("Should fail when building info is null")
        void shouldFailWhenBuildingInfoIsNull() {
            BuildingAddress address = new BuildingAddress("Main Street", "123");
            ClientId ownerId = new ClientId(clientId);
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> new Building(ownerId, address, cityId, null, null)
            );
            assertTrue(exception.getMessage().contains("buildingInfo"));
        }
    }

    @Nested
    @DisplayName("Building Update Tests")
    class BuildingUpdateTests {

        @Test
        @DisplayName("Should update building information successfully")
        void shouldUpdateBuildingInformationSuccessfully() {
            Building building = buildingWithOriginalData(new RiskIndicators(false, false));
            BuildingAddress newAddress = new BuildingAddress("New Street", "2B");
            BuildingInfo newInfo = new BuildingInfo(2021, BuildingType.OFFICE, 7, new BigDecimal("250.00"), new BigDecimal("300000.00"));
            RiskIndicators newRisk = new RiskIndicators(true, true);
            UUID newCityId = UUID.randomUUID();

            building.updateInformation(newAddress, newCityId, newInfo, newRisk);

            assertEquals(newAddress, building.getAddress());
            assertEquals(newCityId, building.getCityId());
            assertEquals(newInfo, building.getBuildingInfo());
            assertEquals(newRisk, building.getRiskIndicators());
            assertEquals(clientId, building.getOwnerId().value());
        }

        @Test
        @DisplayName("Should update building keeping same risk indicators when null provided")
        void shouldUpdateBuildingKeepingRiskIndicatorsWhenNull() {
            RiskIndicators originalRisk = new RiskIndicators(false, true);
            Building building = buildingWithOriginalData(originalRisk);
            BuildingAddress newAddress = new BuildingAddress("New Street", "2");
            BuildingInfo newInfo = new BuildingInfo(2021, BuildingType.OFFICE, 5, new BigDecimal("200.00"), new BigDecimal("200000.00"));

            building.updateInformation(newAddress, cityId, newInfo, null);

            assertEquals(newAddress, building.getAddress());
            assertEquals(newInfo, building.getBuildingInfo());
            assertEquals(originalRisk, building.getRiskIndicators());
        }

        @Test
        @DisplayName("Should fail to update when address is null")
        void shouldFailToUpdateWhenAddressIsNull() {
            Building building = defaultBuilding();
            BuildingInfo newInfo = new BuildingInfo(
                    2021,
                    BuildingType.OFFICE,
                    5,
                    new BigDecimal("200.00"),
                    new BigDecimal("200000.00")
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> building.updateInformation(null, cityId, newInfo, null)
            );
            assertTrue(exception.getMessage().contains("address"));
        }

        @Test
        @DisplayName("Should fail to update when city ID is null")
        void shouldFailToUpdateWhenCityIdIsNull() {
            Building building = defaultBuilding();
            BuildingAddress newAddress = new BuildingAddress("New Street", "2");
            BuildingInfo newInfo = new BuildingInfo(
                    2021,
                    BuildingType.OFFICE,
                    5,
                    new BigDecimal("200.00"),
                    new BigDecimal("200000.00")
            );

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> building.updateInformation(newAddress, null, newInfo, null)
            );
            assertTrue(exception.getMessage().contains("City ID"));
        }

        @Test
        @DisplayName("Should fail to update when building info is null")
        void shouldFailToUpdateWhenBuildingInfoIsNull() {
            Building building = defaultBuilding();
            BuildingAddress newAddress = new BuildingAddress("New Street", "2");

            // Act & Assert
            DomainValidationException exception = assertThrows(
                    DomainValidationException.class,
                    () -> building.updateInformation(newAddress, cityId, null, null)
            );
            assertTrue(exception.getMessage().contains("buildingInfo"));
        }
    }

    @Nested
    @DisplayName("Building Equality Tests")
    class BuildingEqualityTests {

        @Test
        @DisplayName("Buildings with same ID should be equal")
        void buildingsWithSameIdShouldBeEqual() {
            // Arrange
            UUID buildingId = UUID.randomUUID();
            Building building1 = new Building(
                    new BuildingId(buildingId),
                    new ClientId(clientId),
                    new BuildingAddress("Street 1", "1"),
                    cityId,
                    new BuildingInfo(
                            2020,
                            BuildingType.RESIDENTIAL,
                            3,
                            new BigDecimal("100.00"),
                            new BigDecimal("100000.00")
                    ),
                    null
            );

            Building building2 = new Building(
                    new BuildingId(buildingId),
                    new ClientId(UUID.randomUUID()),
                    new BuildingAddress("Street 2", "2"),
                    UUID.randomUUID(),
                    new BuildingInfo(
                            2021,
                            BuildingType.OFFICE,
                            5,
                            new BigDecimal("200.00"),
                            new BigDecimal("200000.00")
                    ),
                    null
            );

            // Act & Assert
            assertEquals(building1, building2);
            assertEquals(building1.hashCode(), building2.hashCode());
        }

        @Test
        @DisplayName("Buildings with different IDs should not be equal")
        void buildingsWithDifferentIdsShouldNotBeEqual() {
            // Arrange

            BuildingInfo newInfo = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    3,
                    new BigDecimal("100.00"),
                    new BigDecimal("100000.00")
            );

            Building building1 = new Building(
                    new BuildingId(UUID.randomUUID()),
                    new ClientId(clientId),
                    new BuildingAddress("Street", "1"),
                    cityId,
                    newInfo,
                    null
            );

            Building building2 = new Building(
                    new BuildingId(UUID.randomUUID()),
                    new ClientId(clientId),
                    new BuildingAddress("Street", "1"),
                    cityId,
                    newInfo,
                    null
            );

            // Act & Assert
            assertNotEquals(building1, building2);
        }

        @Test
        @DisplayName("Buildings without ID should not be equal")
        void buildingsWithoutIdShouldNotBeEqual() {
            // Arrange

            BuildingInfo newInfo = new BuildingInfo(
                    2020,
                    BuildingType.RESIDENTIAL,
                    3,
                    new BigDecimal("100.00"),
                    new BigDecimal("100000.00")
            );

            Building building1 = new Building(
                    new ClientId(clientId),
                    new BuildingAddress("Street", "1"),
                    cityId,
                    newInfo,
                    null
            );

            Building building2 = new Building(
                    new ClientId(clientId),
                    new BuildingAddress("Street", "1"),
                    cityId,
                    newInfo,
                    null
            );

            // Act & Assert
            assertNotEquals(building1, building2);
        }
    }
}