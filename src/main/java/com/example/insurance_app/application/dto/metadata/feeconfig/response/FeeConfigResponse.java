package com.example.insurance_app.application.dto.metadata.feeconfig.response;

import com.example.insurance_app.application.dto.metadata.feeconfig.FeeConfigTypeDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FeeConfigResponse(
        UUID id,
        String code,
        String name,
        FeeConfigTypeDto type,
        BigDecimal percentage,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        boolean isActive,
        Instant createdAt,
        Instant updatedAt

) {
}
