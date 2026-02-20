package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.report.PolicyReportResponse;
import com.example.insurance_app.infrastructure.persistence.repository.policy.projection.PolicyReportProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportDtoMapper {

     PolicyReportResponse toResponse(PolicyReportProjection projection);
}
