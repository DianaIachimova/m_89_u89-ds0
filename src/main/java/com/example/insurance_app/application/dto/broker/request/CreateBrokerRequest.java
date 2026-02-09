package com.example.insurance_app.application.dto.broker.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateBrokerRequest(
        @NotBlank(message = "Broker code is required")
        @Size(min = 3, max = 30, message = "Broker code must be between 3 and 30 characters")
        String brokerCode,

        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Phone must be a valid phone number")
        String phone,

        BigDecimal commissionPercentage,

        @NotNull(message = "Active flag is required")
        Boolean active
) {
}
