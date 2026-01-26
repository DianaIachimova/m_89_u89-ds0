package com.example.insurance_app.application.dto.client.response;

import java.time.Instant;

public record IdentificationNumberChangeDto(
        Instant changedAt,
        String changedBy,
        String changeReason
) {
}
