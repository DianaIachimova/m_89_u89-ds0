package com.example.insurance_app.application.dto.client.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Pattern(regexp = "^(?:\\d{13}|\\d{2,10})$",
                message = "Identification number must be valid")
        String identificationNumber,

        @NotNull(message = "Contact info is required")
        @Valid
        ContactInfoRequest contactInfo,

        @Valid
        AddressRequest address
) {
}
