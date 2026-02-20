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
import com.example.insurance_app.application.dto.policy.request.CreatePolicyRequest;
import com.example.insurance_app.application.dto.policy.response.PolicyResponse;
import com.example.insurance_app.application.dto.report.PolicyReportResponse;
import com.example.insurance_app.application.dto.report.ReportGrouping;
import com.example.insurance_app.application.service.BuildingService;
import com.example.insurance_app.application.service.ClientService;
import com.example.insurance_app.application.service.policy.PolicyService;
import com.example.insurance_app.application.service.report.PolicyReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Admin Report Integration Tests")
class AdminReportIntegrationTest {

    @Autowired
    private PolicyReportService reportService;

    @Autowired
    private PolicyService policyService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BuildingService buildingService;

    private static final UUID BROKER_ID = UUID.fromString("b0b0b0b0-0000-4000-a000-000000000001");
    private static final UUID CURRENCY_RON_ID = UUID.fromString("c0c0c0c0-0000-4000-a000-000000000001");
    private static final UUID CURRENCY_EUR_ID = UUID.fromString("c0c0c0c0-0000-4000-a000-000000000002");
    private static final UUID CITY_SECTOR1_ID = UUID.fromString("f1d2c3b4-5a6b-4c8d-9e0f-112233445566");

    @Nested
    @DisplayName("Report by Country")
    class ReportByCountryTests {

        @Test
        @DisplayName("Should aggregate policies by country")
        void shouldAggregateByCountry() {
            ClientResponse client = createTestClient();
            BuildingSummaryResponse building = createTestBuilding(client.id(), CITY_SECTOR1_ID);

            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusYears(1);

            CreatePolicyRequest policyRequest1 = new CreatePolicyRequest(
                client.id(),
                building.id(),
                BROKER_ID,
                CURRENCY_RON_ID,
                new BigDecimal("1000.00"),
                startDate,
                endDate
            );

            CreatePolicyRequest policyRequest2 = new CreatePolicyRequest(
                client.id(),
                building.id(),
                BROKER_ID,
                CURRENCY_RON_ID,
                new BigDecimal("2000.00"),
                startDate,
                endDate
            );

            PolicyResponse policy1 = policyService.createDraft(policyRequest1);
            PolicyResponse policy2 = policyService.createDraft(policyRequest2);

            policyService.activate(policy1.id());
            policyService.activate(policy2.id());

            List<PolicyReportResponse> results = reportService.generateReport(
                ReportGrouping.BY_COUNTRY,
                new com.example.insurance_app.application.dto.report.ReportFilterParams(
                    startDate,
                    endDate,
                    null,
                    null,
                    null
                )
            );

            assertNotNull(results);
            assertFalse(results.isEmpty());

            PolicyReportResponse countryReport = results.stream()
                .filter(r -> "Romania".equals(r.groupingKey()))
                .findFirst()
                .orElse(null);

            assertNotNull(countryReport);
            assertTrue(countryReport.policyCount() >= 2);
        }
    }

    @Nested
    @DisplayName("Report by City")
    class ReportByCityTests {

        @Test
        @DisplayName("Should aggregate policies by city")
        void shouldAggregateByCity() {
            ClientResponse client = createTestClient();
            BuildingSummaryResponse building1 = createTestBuilding(client.id(), CITY_SECTOR1_ID);
            BuildingSummaryResponse building2 = createTestBuilding(client.id(), CITY_SECTOR1_ID);

            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusYears(1);

            PolicyResponse policy1 = policyService.createDraft(new CreatePolicyRequest(
                client.id(), building1.id(), BROKER_ID, CURRENCY_RON_ID,
                new BigDecimal("1000.00"), startDate, endDate
            ));

            PolicyResponse policy2 = policyService.createDraft(new CreatePolicyRequest(
                client.id(), building2.id(), BROKER_ID, CURRENCY_RON_ID,
                new BigDecimal("2000.00"), startDate, endDate
            ));

            policyService.activate(policy1.id());
            policyService.activate(policy2.id());

            List<PolicyReportResponse> results = reportService.generateReport(
                ReportGrouping.BY_CITY,
                new com.example.insurance_app.application.dto.report.ReportFilterParams(
                    startDate,
                    endDate,
                    null,
                    null,
                    null
                )
            );

            assertNotNull(results);
            assertFalse(results.isEmpty());

            PolicyReportResponse cityReport = results.stream()
                .filter(r -> "Sector 1".equals(r.groupingKey()))
                .findFirst()
                .orElse(null);

            assertNotNull(cityReport);
            assertTrue(cityReport.policyCount() >= 2);
        }
    }

    @Nested
    @DisplayName("Report with Filters")
    class ReportWithFiltersTests {

        @Test
        @DisplayName("Should filter by status")
        void shouldFilterByStatus() {
            ClientResponse client = createTestClient();
            BuildingSummaryResponse building = createTestBuilding(client.id(), CITY_SECTOR1_ID);

            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusYears(1);

            PolicyResponse draftPolicy = policyService.createDraft(new CreatePolicyRequest(
                client.id(), building.id(), BROKER_ID, CURRENCY_RON_ID,
                new BigDecimal("1000.00"), startDate, endDate
            ));

            PolicyResponse activePolicy = policyService.createDraft(new CreatePolicyRequest(
                client.id(), building.id(), BROKER_ID, CURRENCY_RON_ID,
                new BigDecimal("2000.00"), startDate, endDate
            ));

            policyService.activate(activePolicy.id());

            List<PolicyReportResponse> activeResults = reportService.generateReport(
                ReportGrouping.BY_COUNTRY,
                new com.example.insurance_app.application.dto.report.ReportFilterParams(
                    startDate,
                    endDate,
                    "ACTIVE",
                    null,
                    null
                )
            );

            assertNotNull(activeResults);
            assertFalse(activeResults.isEmpty());

            long totalActivePolicies = activeResults.stream()
                .mapToLong(PolicyReportResponse::policyCount)
                .sum();

            assertTrue(totalActivePolicies >= 1);
        }

        @Test
        @DisplayName("Should filter by currency")
        void shouldFilterByCurrency() {
            ClientResponse client = createTestClient();
            BuildingSummaryResponse building = createTestBuilding(client.id(), CITY_SECTOR1_ID);

            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusYears(1);

            PolicyResponse ronPolicy = policyService.createDraft(new CreatePolicyRequest(
                client.id(), building.id(), BROKER_ID, CURRENCY_RON_ID,
                new BigDecimal("1000.00"), startDate, endDate
            ));

            PolicyResponse eurPolicy = policyService.createDraft(new CreatePolicyRequest(
                client.id(), building.id(), BROKER_ID, CURRENCY_EUR_ID,
                new BigDecimal("2000.00"), startDate, endDate
            ));

            policyService.activate(ronPolicy.id());
            policyService.activate(eurPolicy.id());

            List<PolicyReportResponse> ronResults = reportService.generateReport(
                ReportGrouping.BY_COUNTRY,
                new com.example.insurance_app.application.dto.report.ReportFilterParams(
                    startDate,
                    endDate,
                    null,
                    "RON",
                    null
                )
            );

            assertNotNull(ronResults);
            assertTrue(ronResults.stream().allMatch(r -> "RON".equals(r.currencyCode())));
        }

        @Test
        @DisplayName("Should filter by building type")
        void shouldFilterByBuildingType() {
            ClientResponse client = createTestClient();
            BuildingSummaryResponse residentialBuilding = createTestBuildingWithType(
                client.id(), CITY_SECTOR1_ID, BuildingTypeDto.RESIDENTIAL
            );
            BuildingSummaryResponse officeBuilding = createTestBuildingWithType(
                client.id(), CITY_SECTOR1_ID, BuildingTypeDto.OFFICE
            );

            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusYears(1);

            PolicyResponse residentialPolicy = policyService.createDraft(new CreatePolicyRequest(
                client.id(), residentialBuilding.id(), BROKER_ID, CURRENCY_RON_ID,
                new BigDecimal("1000.00"), startDate, endDate
            ));

            PolicyResponse officePolicy = policyService.createDraft(new CreatePolicyRequest(
                client.id(), officeBuilding.id(), BROKER_ID, CURRENCY_RON_ID,
                new BigDecimal("2000.00"), startDate, endDate
            ));

            policyService.activate(residentialPolicy.id());
            policyService.activate(officePolicy.id());

            List<PolicyReportResponse> residentialResults = reportService.generateReport(
                ReportGrouping.BY_CITY,
                new com.example.insurance_app.application.dto.report.ReportFilterParams(
                    startDate,
                    endDate,
                    null,
                    null,
                    "RESIDENTIAL"
                )
            );

            assertNotNull(residentialResults);
            assertFalse(residentialResults.isEmpty());
        }
    }

    @Nested
    @DisplayName("Report by Broker")
    class ReportByBrokerTests {

        @Test
        @DisplayName("Should aggregate policies by broker")
        void shouldAggregateByBroker() {
            ClientResponse client = createTestClient();
            BuildingSummaryResponse building = createTestBuilding(client.id(), CITY_SECTOR1_ID);

            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusYears(1);

            PolicyResponse policy = policyService.createDraft(new CreatePolicyRequest(
                client.id(), building.id(), BROKER_ID, CURRENCY_RON_ID,
                new BigDecimal("1000.00"), startDate, endDate
            ));

            policyService.activate(policy.id());

            List<PolicyReportResponse> results = reportService.generateReport(
                ReportGrouping.BY_BROKER,
                new com.example.insurance_app.application.dto.report.ReportFilterParams(
                    startDate,
                    endDate,
                    null,
                    null,
                    null
                )
            );

            assertNotNull(results);
            assertFalse(results.isEmpty());

            PolicyReportResponse brokerReport = results.stream()
                .filter(r -> r.policyCount() > 0)
                .findFirst()
                .orElse(null);

            assertNotNull(brokerReport);
            assertNotNull(brokerReport.groupingKey());
            assertTrue(brokerReport.policyCount() >= 1);
        }
    }

    private ClientResponse createTestClient() {
        CreateClientRequest request = new CreateClientRequest(
            ClientTypeDto.INDIVIDUAL,
            "Test Client " + UUID.randomUUID(),
            "1234567890123",
            new ContactInfoRequest("test@example.com", "+40712345678"),
            new com.example.insurance_app.application.dto.client.request.AddressRequest(
                "Test Street",
                "Test City",
                "Test County",
                "123456",
                "Test Country"
            )
        );
        return clientService.createClient(request);
    }

    private BuildingSummaryResponse createTestBuilding(UUID ownerId, UUID cityId) {
        return createTestBuildingWithType(ownerId, cityId, BuildingTypeDto.RESIDENTIAL);
    }

    private BuildingSummaryResponse createTestBuildingWithType(UUID ownerId, UUID cityId, BuildingTypeDto buildingType) {
        CreateBuildingRequest request = new CreateBuildingRequest(
            new AddressRequest("Building Street", "100", cityId),
            new BuildingInfoRequest(
                2020,
                buildingType,
                3,
                new BigDecimal("150.00"),
                new BigDecimal("200000.00")
            ),
            new RiskIndicatorsDto(false, false)
        );
        return buildingService.createBuilding(ownerId, request);
    }
}
