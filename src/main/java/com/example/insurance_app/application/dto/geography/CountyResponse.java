package com.example.insurance_app.application.dto.geography;

import java.util.UUID;

public record CountyResponse(
        UUID id,
        String name,
        String code
) {
}
