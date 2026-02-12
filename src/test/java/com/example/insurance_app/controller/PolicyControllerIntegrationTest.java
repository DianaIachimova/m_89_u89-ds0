package com.example.insurance_app.controller;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import com.example.insurance_app.application.dto.building.request.AddressRequest;
import com.example.insurance_app.application.dto.building.request.BuildingInfoRequest;
import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingSummaryResponse;
import com.example.insurance_app.application.dto.client.ClientTypeDto;
import com.example.insurance_app.application.dto.client.request.ContactInfoRequest;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.dto.client.response.ClientResponse;
import com.example.insurance_app.application.dto.policy.request.CancelPolicyRequest;
import com.example.insurance_app.application.dto.policy.request.CreatePolicyRequest;
import com.example.insurance_app.application.dto.policy.response.PolicyDetailResponse;
import com.example.insurance_app.application.dto.policy.response.PolicyResponse;
import com.example.insurance_app.application.exception.PolicyNotFoundException;
import com.example.insurance_app.application.service.BuildingService;
import com.example.insurance_app.application.service.ClientService;
import com.example.insurance_app.application.service.policy.PolicyService;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyPricingSnapshotRepository;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("Policy API Integration Tests")
class PolicyControllerIntegrationTest {

    @Autowired
    private PolicyService policyService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private BuildingService buildingService;
    @Autowired
    private PolicyRepository policyRepository;
    @Autowired
    private PolicyPricingSnapshotRepository snapshotRepository;

    private static final UUID CITY_ID = UUID.fromString("f1d2c3b4-5a6b-4c8d-9e0f-112233445566");
    private static final UUID BROKER_ID = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000001");
    private static final UUID INACTIVE_BROKER_ID = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000002");
    private static final UUID CURRENCY_ID = UUID.fromString("c0c0c0c0-0000-4000-a000-000000000001");

    private UUID clientId;
    private UUID buildingId;

    @BeforeEach
    void setUp() {
        ClientResponse client = clientService.createClient(new CreateClientRequest(
                ClientTypeDto.INDIVIDUAL, "Policy Test Client", "9876543210123",
                new ContactInfoRequest("policytest@example.com", "+40712345670"), null
        ));
        clientId = client.id();

        BuildingSummaryResponse building = buildingService.createBuilding(clientId, new CreateBuildingRequest(
                new AddressRequest("Policy Street", "100", CITY_ID),
                new BuildingInfoRequest(2020, BuildingTypeDto.RESIDENTIAL, 5,
                        new BigDecimal("200.00"), new BigDecimal("300000.00")),
                new RiskIndicatorsDto(true, false)
        ));
        buildingId = building.id();
    }

    private CreatePolicyRequest validCreateRequest() {
        return new CreatePolicyRequest(
                clientId, buildingId, BROKER_ID, CURRENCY_ID,
                new BigDecimal("1000.00"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusYears(1)
        );
    }

    @Nested
    @DisplayName("Full lifecycle: create -> activate -> cancel")
    class FullLifecycleTests {

        @Test
        @DisplayName("Should create draft policy with calculated premium")
        void shouldCreateDraftPolicy() {
            PolicyResponse draft = policyService.createDraft(validCreateRequest());

            assertNotNull(draft);
            assertNotNull(draft.id());
            assertNotNull(draft.policyNumber());
            assertEquals("DRAFT", draft.status());
            assertEquals(clientId, draft.clientId());
            assertEquals(buildingId, draft.buildingId());
            assertEquals(BROKER_ID, draft.brokerId());
            assertEquals("RON", draft.currencyCode());
            assertEquals(new BigDecimal("1000.00"), draft.basePremium());
            assertTrue(draft.finalPremium().compareTo(draft.basePremium()) >= 0);
            assertTrue(policyRepository.existsById(draft.id()));
        }

        @Test
        @DisplayName("Should activate draft policy and persist pricing snapshot")
        void shouldActivatePolicy() {
            PolicyResponse draft = policyService.createDraft(validCreateRequest());
            PolicyResponse active = policyService.activate(draft.id());

            assertEquals("ACTIVE", active.status());
            assertNotNull(active.finalPremium());
            assertTrue(active.finalPremium().compareTo(BigDecimal.ZERO) > 0);

            var snapshot = snapshotRepository.findByPolicyId(draft.id());
            assertTrue(snapshot.isPresent());
            assertEquals(draft.basePremium(), snapshot.get().getBasePremium());
        }

        @Test
        @DisplayName("Should cancel active policy")
        void shouldCancelActivePolicy() {
            PolicyResponse draft = policyService.createDraft(validCreateRequest());
            policyService.activate(draft.id());

            PolicyResponse cancelled = policyService.cancel(draft.id(), new CancelPolicyRequest("Customer requested cancellation"));

            assertEquals("CANCELLED", cancelled.status());
            assertEquals("Customer requested cancellation", cancelled.cancellationReason());
            assertEquals(LocalDate.now(), cancelled.cancelledAt());
        }
    }

    @Nested
    @DisplayName("Invalid policy creation")
    class InvalidCreationTests {

        @Test
        @DisplayName("Should fail with non-existent client")
        void nonExistentClient() {
            CreatePolicyRequest req = new CreatePolicyRequest(
                    UUID.randomUUID(), buildingId, BROKER_ID, CURRENCY_ID,
                    new BigDecimal("1000.00"), LocalDate.now().plusDays(1), LocalDate.now().plusYears(1));
            assertThrows(Exception.class, () -> policyService.createDraft(req));
        }

        @Test
        @DisplayName("Should fail with non-existent building")
        void nonExistentBuilding() {
            CreatePolicyRequest req = new CreatePolicyRequest(
                    clientId, UUID.randomUUID(), BROKER_ID, CURRENCY_ID,
                    new BigDecimal("1000.00"), LocalDate.now().plusDays(1), LocalDate.now().plusYears(1));
            assertThrows(Exception.class, () -> policyService.createDraft(req));
        }

        @Test
        @DisplayName("Should fail with inactive broker")
        void inactiveBroker() {
            CreatePolicyRequest req = new CreatePolicyRequest(
                    clientId, buildingId, INACTIVE_BROKER_ID, CURRENCY_ID,
                    new BigDecimal("1000.00"), LocalDate.now().plusDays(1), LocalDate.now().plusYears(1));
            assertThrows(DomainValidationException.class, () -> policyService.createDraft(req));
        }

        @Test
        @DisplayName("Should fail when building does not belong to client")
        void buildingNotBelongingToClient() {
            CreateClientRequest clientReq = new CreateClientRequest(
                    ClientTypeDto.COMPANY, "Other Company", "1234567890",
                    new ContactInfoRequest("other@example.com", "+40799999999"), null);
            ClientResponse otherClient = clientService.createClient(clientReq);

            CreatePolicyRequest req = new CreatePolicyRequest(
                    otherClient.id(), buildingId, BROKER_ID, CURRENCY_ID,
                    new BigDecimal("1000.00"), LocalDate.now().plusDays(1), LocalDate.now().plusYears(1));
            assertThrows(DomainValidationException.class, () -> policyService.createDraft(req));
        }

        @Test
        @DisplayName("Should fail with inactive currency")
        void inactiveCurrency() {
            UUID inactiveCurrencyId = UUID.fromString("c0c0c0c0-0000-4000-a000-000000000003");
            CreatePolicyRequest req = new CreatePolicyRequest(
                    clientId, buildingId, BROKER_ID, inactiveCurrencyId,
                    new BigDecimal("1000.00"), LocalDate.now().plusDays(1), LocalDate.now().plusYears(1));
            assertThrows(DomainValidationException.class, () -> policyService.createDraft(req));
        }
    }

    @Nested
    @DisplayName("Get and list policies")
    class QueryTests {

        @Test
        @DisplayName("Should get policy by ID with currency details")
        void getById() {
            PolicyResponse draft = policyService.createDraft(validCreateRequest());
            PolicyDetailResponse fetched = policyService.getById(draft.id());

            assertEquals(draft.id(), fetched.id());
            assertEquals(draft.policyNumber(), fetched.policyNumber());
            assertEquals(draft.basePremium(), fetched.basePremium());
            assertNotNull(fetched.currency());
            assertEquals("RON", fetched.currency().code());
            assertNotNull(fetched.currency().name());
            assertNotNull(fetched.currency().id());
        }

        @Test
        @DisplayName("Should throw when policy not found")
        void notFound() {
            assertThrows(PolicyNotFoundException.class, () -> policyService.getById(UUID.randomUUID()));
        }

        @Test
        @DisplayName("Should list policies with filters")
        void listWithFilters() {
            policyService.createDraft(validCreateRequest());
            policyService.createDraft(validCreateRequest());

            var all = policyService.list(null, null, null, null, null, PageRequest.of(0, 20));
            assertEquals(2, all.totalElements());

            var byClient = policyService.list(clientId, null, null, null, null, PageRequest.of(0, 20));
            assertEquals(2, byClient.totalElements());

            var byBroker = policyService.list(null, BROKER_ID, null, null, null, PageRequest.of(0, 20));
            assertEquals(2, byBroker.totalElements());

            var byStatus = policyService.list(null, null, "DRAFT", null, null, PageRequest.of(0, 20));
            assertEquals(2, byStatus.totalElements());
        }
    }
}
