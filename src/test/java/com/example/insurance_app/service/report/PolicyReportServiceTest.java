package com.example.insurance_app.service.report;

import com.example.insurance_app.application.dto.report.PolicyReportResponse;
import com.example.insurance_app.application.dto.report.ReportFilterParams;
import com.example.insurance_app.application.dto.report.ReportGrouping;
import com.example.insurance_app.application.mapper.ReportDtoMapper;
import com.example.insurance_app.application.service.report.PolicyReportService;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.building.BuildingType;
import com.example.insurance_app.domain.model.policy.PolicyStatus;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyReportRepository;
import com.example.insurance_app.infrastructure.persistence.repository.policy.projection.PolicyReportProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyReportService Unit Tests")
class PolicyReportServiceTest {

    @Mock
    private PolicyReportRepository reportRepository;

    @Mock
    private ReportDtoMapper reportDtoMapper;

    @InjectMocks
    private PolicyReportService reportService;

    private LocalDate validFrom;
    private LocalDate validTo;
    private ReportFilterParams validFilters;
    private PolicyReportProjection projection;
    private PolicyReportResponse response;

    @BeforeEach
    void setUp() {
        validFrom = LocalDate.of(2026, 1, 1);
        validTo = LocalDate.of(2026, 12, 31);

        validFilters = new ReportFilterParams(validFrom, validTo, null, null, null);

        projection = new PolicyReportProjection(
            "United States",
            "USD",
            100L,
            new BigDecimal("50000.00"),
            new BigDecimal("50000.00")
        );

        response = new PolicyReportResponse(
            "United States",
            "USD",
            100L,
            new BigDecimal("50000.00"),
            new BigDecimal("50000.00")
        );
    }

    @Nested
    @DisplayName("generateReport")
    class GenerateReportTests {

        @Test
        @DisplayName("Should generate report with valid filters")
        void shouldGenerateReportWithValidFilters() {
            when(reportRepository.generateReport(any())).thenReturn(List.of(projection));
            when(reportDtoMapper.toResponse(projection)).thenReturn(response);

            List<PolicyReportResponse> result = reportService.generateReport(ReportGrouping.BY_COUNTRY, validFilters);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(response, result.getFirst());

            verify(reportRepository).generateReport(argThat(query ->
                query.grouping() == ReportGrouping.BY_COUNTRY &&
                query.from().equals(validFrom) &&
                query.to().equals(validTo) &&
                query.status() == null &&
                query.currency() == null &&
                query.buildingType() == null
            ));
            verify(reportDtoMapper).toResponse(projection);
        }

        @Test
        @DisplayName("Should generate report with all filters applied")
        void shouldGenerateReportWithAllFilters() {
            ReportFilterParams allFilters = new ReportFilterParams(
                validFrom,
                validTo,
                "ACTIVE",
                "USD",
                "RESIDENTIAL"
            );

            when(reportRepository.generateReport(any())).thenReturn(List.of(projection));
            when(reportDtoMapper.toResponse(projection)).thenReturn(response);

            List<PolicyReportResponse> result = reportService.generateReport(ReportGrouping.BY_CITY, allFilters);

            assertNotNull(result);
            assertEquals(1, result.size());

            verify(reportRepository).generateReport(argThat(query ->
                query.grouping() == ReportGrouping.BY_CITY &&
                query.from().equals(validFrom) &&
                query.to().equals(validTo) &&
                query.status() == PolicyStatus.ACTIVE &&
                query.currency().equals("USD") &&
                query.buildingType() == BuildingType.RESIDENTIAL
            ));
        }

        @Test
        @DisplayName("Should throw exception when from is after to")
        void shouldThrowWhenFromIsAfterTo() {
            ReportFilterParams invalidFilters = new ReportFilterParams(
                LocalDate.of(2026, 12, 31),
                LocalDate.of(2026, 1, 1),
                null,
                null,
                null
            );

            assertThrows(DomainValidationException.class, () ->
                reportService.generateReport(ReportGrouping.BY_COUNTRY, invalidFilters)
            );

            verify(reportRepository, never()).generateReport(any());
        }

        @Test
        @DisplayName("Should throw exception when status is invalid")
        void shouldThrowWhenStatusIsInvalid() {
            ReportFilterParams invalidFilters = new ReportFilterParams(
                validFrom,
                validTo,
                "INVALID_STATUS",
                null,
                null
            );

            assertThrows(IllegalArgumentException.class, () ->
                reportService.generateReport(ReportGrouping.BY_COUNTRY, invalidFilters)
            );

            verify(reportRepository, never()).generateReport(any());
        }

        @Test
        @DisplayName("Should throw exception when buildingType is invalid")
        void shouldThrowWhenBuildingTypeIsInvalid() {
            ReportFilterParams invalidFilters = new ReportFilterParams(
                validFrom,
                validTo,
                null,
                null,
                "INVALID_TYPE"
            );

            assertThrows(IllegalArgumentException.class, () ->
                reportService.generateReport(ReportGrouping.BY_COUNTRY, invalidFilters)
            );

            verify(reportRepository, never()).generateReport(any());
        }

        @Test
        @DisplayName("Should return empty list when no data matches filters")
        void shouldReturnEmptyListWhenNoData() {
            when(reportRepository.generateReport(any())).thenReturn(List.of());

            List<PolicyReportResponse> result = reportService.generateReport(ReportGrouping.BY_BROKER, validFilters);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle multiple projections")
        void shouldHandleMultipleProjections() {
            PolicyReportProjection projection2 = new PolicyReportProjection(
                "Canada",
                "CAD",
                50L,
                new BigDecimal("25000.00"),
                new BigDecimal("20000.00")
            );

            PolicyReportResponse response2 = new PolicyReportResponse(
                "Canada",
                "CAD",
                50L,
                new BigDecimal("25000.00"),
                new BigDecimal("20000.00")
            );

            when(reportRepository.generateReport(any())).thenReturn(List.of(projection, projection2));
            when(reportDtoMapper.toResponse(projection)).thenReturn(response);
            when(reportDtoMapper.toResponse(projection2)).thenReturn(response2);

            List<PolicyReportResponse> result = reportService.generateReport(ReportGrouping.BY_COUNTRY, validFilters);

            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(response, result.get(0));
            assertEquals(response2, result.get(1));
        }
    }
}
