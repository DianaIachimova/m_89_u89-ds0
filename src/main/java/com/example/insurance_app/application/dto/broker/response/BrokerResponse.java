package com.example.insurance_app.application.dto.broker.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BrokerResponse(
        UUID id,
        String brokerCode,
        String name,
        String email,
        String phone,
        String status,
        BigDecimal commissionPercentage,
        Instant createdAt,
        Instant updatedAt
) {
}
