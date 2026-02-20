package com.example.insurance_app.webapi.controller.admin;

import com.example.insurance_app.application.dto.report.PolicyReportResponse;
import com.example.insurance_app.application.dto.report.ReportFilterParams;
import com.example.insurance_app.application.dto.report.ReportGrouping;
import com.example.insurance_app.application.service.report.PolicyReportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    private final PolicyReportService reportService;

    public AdminReportController(PolicyReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/policies-by-country")
    public List<PolicyReportResponse> byCountry(@Valid ReportFilterParams filters) {
        return reportService.generateReport(ReportGrouping.BY_COUNTRY, filters);
    }

    @GetMapping("/policies-by-county")
    public List<PolicyReportResponse> byCounty(@Valid ReportFilterParams filters) {
        return reportService.generateReport(ReportGrouping.BY_COUNTY, filters);
    }

    @GetMapping("/policies-by-city")
    public List<PolicyReportResponse> byCity(@Valid ReportFilterParams filters) {
        return reportService.generateReport(ReportGrouping.BY_CITY, filters);
    }

    @GetMapping("/policies-by-broker")
    public List<PolicyReportResponse> byBroker(@Valid ReportFilterParams filters) {
        return reportService.generateReport(ReportGrouping.BY_BROKER, filters);
    }
}
