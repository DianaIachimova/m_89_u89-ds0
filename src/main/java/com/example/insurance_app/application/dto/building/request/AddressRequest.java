package com.example.insurance_app.application.dto.building.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record AddressRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 200, message = "Street must not exceed 200 characters")
        String street,

        @NotBlank(message = "Street number is required")
        @Size(max = 20, message = "Street number must not exceed 20 characters")
        String streetNumber,

        @NotNull(message = "City ID is required")
        UUID cityId
) {
}
