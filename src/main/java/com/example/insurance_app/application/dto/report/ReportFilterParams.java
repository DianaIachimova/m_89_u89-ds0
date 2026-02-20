package com.example.insurance_app.application.dto.report;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReportFilterParams(
    @NotNull(message = "From date is required")
    LocalDate from,

    @NotNull(message = "To date is required")
    LocalDate to,

    String status,
    String currency,
    String buildingType
) {}
