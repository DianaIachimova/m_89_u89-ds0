package com.example.insurance_app.application.service.report;

import com.example.insurance_app.application.dto.report.PolicyReportQuery;
import com.example.insurance_app.application.dto.report.PolicyReportResponse;
import com.example.insurance_app.application.dto.report.ReportFilterParams;
import com.example.insurance_app.application.dto.report.ReportGrouping;
import com.example.insurance_app.application.mapper.ReportDtoMapper;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PolicyReportService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyReportService.class);

    private final PolicyReportRepository reportRepository;
    private final ReportDtoMapper reportDtoMapper;

    public PolicyReportService(PolicyReportRepository reportRepository, ReportDtoMapper reportDtoMapper) {
        this.reportRepository = reportRepository;
        this.reportDtoMapper = reportDtoMapper;
    }

    @Transactional(readOnly = true)
    public List<PolicyReportResponse> generateReport(ReportGrouping grouping, ReportFilterParams filters) {
        PolicyReportQuery query = PolicyReportQuery.from(grouping, filters);

        logger.info("Generating report with grouping={}, from={}, to={}", 
                query.grouping(), query.from(), query.to());

        return reportRepository.generateReport(query).stream()
                .map(reportDtoMapper::toResponse)
                .toList();
    }
}
