package com.example.insurance_app.domain.model;

import java.time.Instant;

public record AuditInfo(
        Instant createdAt,
        Instant updatedAt
) {
}
