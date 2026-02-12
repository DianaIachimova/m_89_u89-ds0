package com.example.insurance_app.controller;

import com.example.insurance_app.application.dto.broker.request.CreateBrokerRequest;
import com.example.insurance_app.application.dto.broker.request.UpdateBrokerRequest;
import com.example.insurance_app.application.dto.broker.response.BrokerResponse;
import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import com.example.insurance_app.application.dto.building.request.AddressRequest;
import com.example.insurance_app.application.dto.building.request.BuildingInfoRequest;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.client.ClientTypeDto;
import com.example.insurance_app.application.dto.client.request.ContactInfoRequest;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.dto.policy.request.CreatePolicyRequest;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.service.BuildingService;
import com.example.insurance_app.application.service.ClientService;
import com.example.insurance_app.application.service.broker.BrokerService;
import com.example.insurance_app.application.service.policy.PolicyService;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.infrastructure.persistence.repository.broker.BrokerRepository;
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
@DisplayName("Broker API Integration Tests")
class BrokerControllerIntegrationTest {

    @Autowired
    private BrokerService brokerService;
    @Autowired
    private BrokerRepository brokerRepository;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private BuildingService buildingService;

    private static final UUID CITY_ID = UUID.fromString("f1d2c3b4-5a6b-4c8d-9e0f-112233445566");
    private static final UUID CURRENCY_ID = UUID.fromString("c0c0c0c0-0000-4000-a000-000000000001");

    @Nested
    @DisplayName("Create broker")
    class CreateTests {

        @Test
        @DisplayName("Should create broker and persist to database")
        void shouldCreateBroker() {
            CreateBrokerRequest request = new CreateBrokerRequest(
                    "BRK-NEW-001", "New Broker", "newbroker@test.com",
                    "+40712345680", new BigDecimal("0.75"), true
            );

            BrokerResponse response = brokerService.create(request);

            assertNotNull(response);
            assertNotNull(response.id());
            assertEquals("BRK-NEW-001", response.brokerCode());
            assertEquals("New Broker", response.name());
            assertEquals("newbroker@test.com", response.email());
            assertEquals("ACTIVE", response.status());
            assertTrue(brokerRepository.existsById(response.id()));
        }

        @Test
        @DisplayName("Should reject duplicate broker code")
        void duplicateBrokerCode() {
            CreateBrokerRequest request = new CreateBrokerRequest(
                    "BRK-TEST-001", "Duplicate Code Broker", "unique@test.com",
                    null, null, true
            );

            assertThrows(DuplicateResourceException.class, () -> brokerService.create(request));
        }

        @Test
        @DisplayName("Should reject duplicate email")
        void duplicateEmail() {
            CreateBrokerRequest request = new CreateBrokerRequest(
                    "BRK-UNIQUE-001", "Unique Code Broker", "broker.active@test.com",
                    null, null, true
            );

            assertThrows(DuplicateResourceException.class, () -> brokerService.create(request));
        }
    }

    @Nested
    @DisplayName("Update broker")
    class UpdateTests {

        @Test
        @DisplayName("Should update broker details")
        void shouldUpdate() {
            UUID brokerId = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000001");
            UpdateBrokerRequest request = new UpdateBrokerRequest(
                    "Updated Broker Name", "updated.broker@test.com",
                    "+40799999999", new BigDecimal("0.12")
            );

            BrokerResponse response = brokerService.update(brokerId, request);

            assertEquals("Updated Broker Name", response.name());
            assertEquals("updated.broker@test.com", response.email());
        }

        @Test
        @DisplayName("Should reject update with duplicate email")
        void duplicateEmailOnUpdate() {
            UUID brokerId = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000001");
            UpdateBrokerRequest request = new UpdateBrokerRequest(
                    "Updated Name", "broker.inactive@test.com",
                    null, null
            );

            assertThrows(DuplicateResourceException.class, () -> brokerService.update(brokerId, request));
        }

        @Test
        @DisplayName("Should throw when updating non-existent broker")
        void notFound() {
            UUID nonExistentId = UUID.randomUUID();
            UpdateBrokerRequest req = new UpdateBrokerRequest("Name", "email@test.com", null, null);
            assertThrows(ResourceNotFoundException.class,
                    () -> brokerService.update(nonExistentId, req));
        }
    }

    @Nested
    @DisplayName("Activate / Deactivate")
    class StatusTests {

        @Test
        @DisplayName("Should activate inactive broker")
        void shouldActivate() {
            UUID inactiveBrokerId = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000002");
            BrokerResponse response = brokerService.activate(inactiveBrokerId);
            assertEquals("ACTIVE", response.status());
        }

        @Test
        @DisplayName("Should deactivate active broker")
        void shouldDeactivate() {
            UUID activeBrokerId = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000001");
            BrokerResponse response = brokerService.deactivate(activeBrokerId);
            assertEquals("INACTIVE", response.status());
        }

        @Test
        @DisplayName("Should throw when activating already active broker")
        void alreadyActive() {
            UUID activeBrokerId = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000001");
            assertThrows(DomainValidationException.class, () -> brokerService.activate(activeBrokerId));
        }

        @Test
        @DisplayName("Should throw when deactivating already inactive broker")
        void alreadyInactive() {
            UUID inactiveBrokerId = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000002");
            assertThrows(DomainValidationException.class, () -> brokerService.deactivate(inactiveBrokerId));
        }
    }

    @Nested
    @DisplayName("Deactivated broker policy rejection")
    class DeactivatedBrokerPolicyTests {

        @Test
        @DisplayName("Should reject policy creation with deactivated broker")
        void shouldRejectPolicyWithDeactivatedBroker() {
            BrokerResponse broker = brokerService.create(new CreateBrokerRequest(
                    "BRK-DEACT-001", "Deactivation Test Broker", "deact@test.com",
                    "+40712345681", new BigDecimal("0.05"), true
            ));

            brokerService.deactivate(broker.id());

            var client = clientService.createClient(new CreateClientRequest(
                    ClientTypeDto.INDIVIDUAL, "Deact Test Client", "1112223334445",
                    new ContactInfoRequest("deactclient@test.com", "+40712345682"), null
            ));

            var building = buildingService.createBuilding(client.id(), new CreateBuildingRequest(
                    new AddressRequest("Deact Street", "1", CITY_ID),
                    new BuildingInfoRequest(2020, BuildingTypeDto.RESIDENTIAL, 3,
                            new BigDecimal("100.00"), new BigDecimal("100000.00")),
                    new RiskIndicatorsDto(false, false)
            ));

            CreatePolicyRequest req = new CreatePolicyRequest(
                    client.id(), building.id(), broker.id(), CURRENCY_ID,
                    new BigDecimal("1000.00"), LocalDate.now().plusDays(1), LocalDate.now().plusYears(1)
            );

            assertThrows(DomainValidationException.class, () -> policyService.createDraft(req));
        }
    }

    @Nested
    @DisplayName("Get and list")
    class QueryTests {

        @Test
        @DisplayName("Should get broker by ID")
        void getById() {
            UUID brokerId = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000001");
            BrokerResponse response = brokerService.getById(brokerId);

            assertNotNull(response);
            assertEquals("BRK-TEST-001", response.brokerCode());
            assertEquals("Test Broker Active", response.name());
        }

        @Test
        @DisplayName("Should throw when broker not found")
        void notFound() {
            assertThrows(ResourceNotFoundException.class, () -> brokerService.getById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should list brokers with pagination")
        void shouldListBrokers() {
            var page = brokerService.list(PageRequest.of(0, 10));
            assertTrue(page.totalElements() >= 2);
        }
    }
}
