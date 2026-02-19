package com.example.insurance_app.controller;

import com.example.insurance_app.application.dto.geography.CityResponse;
import com.example.insurance_app.application.dto.geography.CountryResponse;
import com.example.insurance_app.application.dto.geography.CountyResponse;
import com.example.insurance_app.application.service.GeographyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@DisplayName("Geography API Integration Tests")
class GeographyControllerIntegrationTest {

    @Autowired
    private GeographyService geographyService;

    private static final UUID ROMANIA_ID = UUID.fromString("3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11");
    private static final UUID BUCURESTI_COUNTY_ID = UUID.fromString("a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9");
    private static final UUID SECTOR1_CITY_ID = UUID.fromString("f1d2c3b4-5a6b-4c8d-9e0f-112233445566");

    @Test
    @DisplayName("Should get all countries")
    void shouldGetAllCountries() {
        // Act
        List<CountryResponse> countries = geographyService.getCountries();

        // Assert
        assertNotNull(countries);
        assertFalse(countries.isEmpty());
        assertTrue(countries.stream().anyMatch(c -> c.name().equals("Romania")));
        assertTrue(countries.stream().anyMatch(c -> c.id().equals(ROMANIA_ID)));
    }

    @Test
    @DisplayName("Should get counties by country ID")
    void shouldGetCountiesByCountryId() {
        // Act
        List<CountyResponse> counties = geographyService.getCountiesByCountryId(ROMANIA_ID);

        // Assert
        assertNotNull(counties);
        assertFalse(counties.isEmpty());
        assertTrue(counties.stream().anyMatch(c -> c.name().equals("Bucuresti")));
        assertTrue(counties.stream().anyMatch(c -> c.code().equals("B")));
        assertTrue(counties.stream().anyMatch(c -> c.id().equals(BUCURESTI_COUNTY_ID)));
    }

    @Test
    @DisplayName("Should get cities by county ID")
    void shouldGetCitiesByCountyId() {
        // Act
        List<CityResponse> cities = geographyService.getCitiesByCountyId(BUCURESTI_COUNTY_ID);

        // Assert
        assertNotNull(cities);
        assertFalse(cities.isEmpty());
        assertTrue(cities.stream().anyMatch(c -> c.name().equals("Sector 1")));
        assertTrue(cities.stream().anyMatch(c -> c.id().equals(SECTOR1_CITY_ID)));
    }

    @Test
    @DisplayName("Should fail to get counties when country does not exist")
    void shouldFailToGetCountiesWhenCountryDoesNotExist() {
        // Arrange
        UUID nonExistentCountryId = UUID.randomUUID();

        // Act & Assert
        assertThrows(
                com.example.insurance_app.application.exception.ResourceNotFoundException.class,
                () -> geographyService.getCountiesByCountryId(nonExistentCountryId)
        );
    }

    @Test
    @DisplayName("Should fail to get cities when county does not exist")
    void shouldFailToGetCitiesWhenCountyDoesNotExist() {
        // Arrange
        UUID nonExistentCountyId = UUID.randomUUID();

        // Act & Assert
        assertThrows(
                com.example.insurance_app.application.exception.ResourceNotFoundException.class,
                () -> geographyService.getCitiesByCountyId(nonExistentCountyId)
        );
    }

    @Test
    @DisplayName("Should return countries ordered by name")
    void shouldReturnCountriesOrderedByName() {
        // Act
        List<CountryResponse> countries = geographyService.getCountries();

        // Assert
        assertNotNull(countries);
        if (countries.size() > 1) {
            for (int i = 0; i < countries.size() - 1; i++) {
                String current = countries.get(i).name();
                String next = countries.get(i + 1).name();
                assertTrue(current.compareTo(next) <= 0, 
                        "Countries should be ordered by name: " + current + " vs " + next);
            }
        }
    }

    @Test
    @DisplayName("Should return counties ordered by name")
    void shouldReturnCountiesOrderedByName() {
        // Act
        List<CountyResponse> counties = geographyService.getCountiesByCountryId(ROMANIA_ID);

        // Assert
        assertNotNull(counties);
        if (counties.size() > 1) {
            for (int i = 0; i < counties.size() - 1; i++) {
                String current = counties.get(i).name();
                String next = counties.get(i + 1).name();
                assertTrue(current.compareTo(next) <= 0,
                        "Counties should be ordered by name: " + current + " vs " + next);
            }
        }
    }

    @Test
    @DisplayName("Should return cities ordered by name")
    void shouldReturnCitiesOrderedByName() {
        // Act
        List<CityResponse> cities = geographyService.getCitiesByCountyId(BUCURESTI_COUNTY_ID);

        // Assert
        assertNotNull(cities);
        if (cities.size() > 1) {
            for (int i = 0; i < cities.size() - 1; i++) {
                String current = cities.get(i).name();
                String next = cities.get(i + 1).name();
                assertTrue(current.compareTo(next) <= 0,
                        "Cities should be ordered by name: " + current + " vs " + next);
            }
        }
    }

    @Test
    @DisplayName("Should verify geography data is loaded from seed")
    void shouldVerifyGeographyDataIsLoadedFromSeed() {
        // Act
        List<CountryResponse> countries = geographyService.getCountries();
        
        // Assert - Verify seed data is present
        assertNotNull(countries);
        assertEquals(1, countries.size()); // Only Romania in test-data.sql
        
        CountryResponse romania = countries.get(0);
        assertEquals("Romania", romania.name());
        assertEquals(ROMANIA_ID, romania.id());

        // Verify counties
        List<CountyResponse> counties = geographyService.getCountiesByCountryId(ROMANIA_ID);
        assertEquals(1, counties.size()); // Only Bucuresti in test-data.sql
        
        CountyResponse bucuresti = counties.get(0);
        assertEquals("Bucuresti", bucuresti.name());
        assertEquals("B", bucuresti.code());
        assertEquals(BUCURESTI_COUNTY_ID, bucuresti.id());

        // Verify cities
        List<CityResponse> cities = geographyService.getCitiesByCountyId(BUCURESTI_COUNTY_ID);
        assertEquals(1, cities.size()); // Only Sector 1 in test-data.sql
        
        CityResponse sector1 = cities.get(0);
        assertEquals("Sector 1", sector1.name());
        assertEquals(SECTOR1_CITY_ID, sector1.id());
    }
}
