package com.example.insurance_app.controller;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.metadata.currency.CurrencyAction;
import com.example.insurance_app.application.dto.metadata.currency.request.CreateCurrencyRequest;
import com.example.insurance_app.application.dto.metadata.currency.request.CurrencyActionRequest;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;
import com.example.insurance_app.application.dto.metadata.feeconfig.FeeConfigTypeDto;
import com.example.insurance_app.application.dto.metadata.feeconfig.request.CreateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.request.UpdateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.response.FeeConfigResponse;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskFactorAction;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskLevelDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.CreateRiskFactorRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.RiskFactorActionRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.UpdateRiskFactorPercentageRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.response.RiskFactorResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.service.metadata.CurrencyService;
import com.example.insurance_app.application.service.metadata.FeeConfigurationService;
import com.example.insurance_app.application.service.metadata.FeeConfigurationUpdateService;
import com.example.insurance_app.application.service.metadata.RiskFactorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Metadata Integration Tests")
class MetadataIntegrationTest {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private FeeConfigurationService feeConfigService;
    @Autowired
    private FeeConfigurationUpdateService feeUpdateService;
    @Autowired
    private RiskFactorService riskFactorService;

    private static final UUID COUNTRY_ID = UUID.fromString("3f9f3a1b-6c2c-4f1e-9c7c-7e5b6a9c2a11");
    private static final UUID COUNTY_ID = UUID.fromString("a14a2bd0-6b6d-4b83-8ef4-2bb7d4b1d2a9");
    private static final UUID CITY_ID = UUID.fromString("f1d2c3b4-5a6b-4c8d-9e0f-112233445566");

    @Nested
    @DisplayName("Currency Tests")
    class CurrencyTests {

        @Test
        @DisplayName("Should create and list currencies")
        void createAndList() {
            CurrencyResponse created = currencyService.createCurrency(
                    new CreateCurrencyRequest("GBP", "British Pound", new BigDecimal("5.800000"), true)
            );

            assertNotNull(created);
            assertNotNull(created.id());
            assertEquals("GBP", created.code());
            assertTrue(created.isActive());

            var page = currencyService.getAllCurrencies(PageRequest.of(0, 20));
            assertTrue(page.totalElements() >= 4);
        }

        @Test
        @DisplayName("Should reject duplicate currency code")
        void duplicateCode() {
            CreateCurrencyRequest req = new CreateCurrencyRequest("RON", "Duplicate RON", new BigDecimal("1.000000"), true);
            assertThrows(DuplicateResourceException.class,
                    () -> currencyService.createCurrency(req));
        }

        @Test
        @DisplayName("Should activate inactive currency")
        void shouldActivate() {
            UUID inactiveCurrencyId = UUID.fromString("c0c0c0c0-0000-4000-a000-000000000003");

            CurrencyResponse result = currencyService.executeAction(
                    inactiveCurrencyId, new CurrencyActionRequest(CurrencyAction.ACTIVATE)
            );

            assertTrue(result.isActive());
        }

        @Test
        @DisplayName("Should deactivate currency without active policies")
        void shouldDeactivate() {
            UUID activeCurrencyId = UUID.fromString("c0c0c0c0-0000-4000-a000-000000000002");

            CurrencyResponse result = currencyService.executeAction(
                    activeCurrencyId, new CurrencyActionRequest(CurrencyAction.DEACTIVATE)
            );

            assertFalse(result.isActive());
        }
    }

    @Nested
    @DisplayName("Fee Configuration Tests")
    class FeeConfigTests {

        @Test
        @DisplayName("Should create fee configuration")
        void shouldCreate() {
            FeeConfigResponse response = feeConfigService.create(new CreateFeeConfigRequest(
                    "BROKER_FEE_NEW", "New Broker Fee", FeeConfigTypeDto.BROKER_COMMISSION,
                    new BigDecimal("0.08"), LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31), true
            ));

            assertNotNull(response);
            assertNotNull(response.id());
            assertEquals("BROKER_FEE_NEW", response.code());
            assertTrue(response.isActive());
        }

        @Test
        @DisplayName("Should reject overlapping period")
        void overlappingPeriod() {
            CreateFeeConfigRequest req = new CreateFeeConfigRequest(
                    "ADMIN_FEE", "Duplicate Admin Fee", FeeConfigTypeDto.ADMIN_FEE,
                    new BigDecimal("0.06"), LocalDate.of(2026, 6, 1),
                    LocalDate.of(2026, 12, 31), true
            );
            assertThrows(DuplicateResourceException.class, () -> feeConfigService.create(req));
        }

        @Test
        @DisplayName("Should update fee configuration")
        void shouldUpdate() {
            UUID feeId = UUID.fromString("f0f0f0f0-0000-4000-a000-000000000001");

            FeeConfigResponse response = feeUpdateService.update(feeId, new UpdateFeeConfigRequest(
                    "Updated Admin Fee", new BigDecimal("0.07"), null
            ));

            assertNotNull(response);
            assertEquals("Updated Admin Fee", response.name());
        }

        @Test
        @DisplayName("Should deactivate fee configuration")
        void shouldDeactivate() {
            UUID feeId = UUID.fromString("f0f0f0f0-0000-4000-a000-000000000001");

            FeeConfigResponse response = feeUpdateService.deactivate(feeId);

            assertFalse(response.isActive());
        }

        @Test
        @DisplayName("Should list fee configurations with pagination")
        void shouldList() {
            var page = feeConfigService.listFeeConfigurations(PageRequest.of(0, 20));
            assertTrue(page.totalElements() >= 3);
        }
    }

    @Nested
    @DisplayName("Risk Factor Tests")
    class RiskFactorTests {

        @Test
        @DisplayName("Should create risk factor for COUNTY level")
        void createCountyLevel() {
            RiskFactorResponse response = riskFactorService.create(new CreateRiskFactorRequest(
                    RiskLevelDto.COUNTY, COUNTY_ID, null, new BigDecimal("0.03"), true
            ));

            assertNotNull(response);
            assertNotNull(response.id());
            assertEquals(RiskLevelDto.COUNTY, response.level());
            assertTrue(response.isActive());
        }

        @Test
        @DisplayName("Should create risk factor for CITY level")
        void createCityLevel() {
            RiskFactorResponse response = riskFactorService.create(new CreateRiskFactorRequest(
                    RiskLevelDto.CITY, CITY_ID, null, new BigDecimal("0.02"), true
            ));

            assertNotNull(response);
            assertEquals(RiskLevelDto.CITY, response.level());
        }

        @Test
        @DisplayName("Should reject invalid geography reference")
        void invalidGeoRef() {
            CreateRiskFactorRequest req = new CreateRiskFactorRequest(
                    RiskLevelDto.COUNTRY, UUID.randomUUID(), null, new BigDecimal("0.05"), false
            );
            assertThrows(ResourceNotFoundException.class, () -> riskFactorService.create(req));
        }

        @Test
        @DisplayName("Should reject active conflict for COUNTRY level")
        void activeConflictCountry() {
            CreateRiskFactorRequest req = new CreateRiskFactorRequest(
                    RiskLevelDto.COUNTRY, COUNTRY_ID, null, new BigDecimal("0.04"), true
            );
            assertThrows(DuplicateResourceException.class, () -> riskFactorService.create(req));
        }

        @Test
        @DisplayName("Should reject active conflict for BUILDING_TYPE level")
        void activeConflictBuildingType() {
            CreateRiskFactorRequest req = new CreateRiskFactorRequest(
                    RiskLevelDto.BUILDING_TYPE, null, BuildingTypeDto.RESIDENTIAL, new BigDecimal("0.02"), true
            );
            assertThrows(DuplicateResourceException.class, () -> riskFactorService.create(req));
        }

        @Test
        @DisplayName("Should update risk factor percentage")
        void shouldUpdatePercentage() {
            UUID rfId = UUID.fromString("a0a0a0a0-0000-4000-a000-000000000001");

            RiskFactorResponse response = riskFactorService.updatePercentage(rfId,
                    new UpdateRiskFactorPercentageRequest(new BigDecimal("0.08"))
            );

            assertNotNull(response);
        }

        @Test
        @DisplayName("Should deactivate risk factor")
        void shouldDeactivate() {
            UUID rfId = UUID.fromString("a0a0a0a0-0000-4000-a000-000000000001");

            RiskFactorResponse response = riskFactorService.executeAction(rfId,
                    new RiskFactorActionRequest(RiskFactorAction.DEACTIVATE)
            );

            assertFalse(response.isActive());
        }

        @Test
        @DisplayName("Should activate inactive risk factor")
        void shouldActivate() {
            RiskFactorResponse created = riskFactorService.create(new CreateRiskFactorRequest(
                    RiskLevelDto.COUNTY, COUNTY_ID, null, new BigDecimal("0.03"), false
            ));

            RiskFactorResponse response = riskFactorService.executeAction(created.id(),
                    new RiskFactorActionRequest(RiskFactorAction.ACTIVATE)
            );

            assertTrue(response.isActive());
        }

        @Test
        @DisplayName("Should list risk factors with pagination")
        void shouldList() {
            var page = riskFactorService.listRiskFactors(PageRequest.of(0, 20));
            assertTrue(page.totalElements() >= 2);
        }
    }
}
