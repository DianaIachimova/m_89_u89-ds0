package com.example.insurance_app.application.dto.building.request;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateBuildingRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 200, message = "Street must not exceed 200 characters")
        String street,

        @NotBlank(message = "Street number is required")
        @Size(max = 20, message = "Street number must not exceed 20 characters")
        String streetNumber,

        @NotNull(message = "City ID is required")
        UUID cityId,

        @NotNull(message = "Construction year is required")
        @Min(value = 1800, message = "Construction year must be at least 1800")
        @Max(value = 2100, message = "Construction year must be at most 2100")
        Integer constructionYear,

        @NotNull(message = "Building type is required")
        BuildingTypeDto buildingType,

        @Min(value = 1, message = "Number of floors must be at least 1")
        @Max(value = 200, message = "Number of floors must be at most 200")
        Integer numberOfFloors,

        @NotNull(message = "Surface area is required")
        @DecimalMin(value = "0.01", message = "Surface area must be positive")
        BigDecimal surfaceArea,

        @NotNull(message = "Insured value is required")
        @DecimalMin(value = "0.01", message = "Insured value must be positive")
        BigDecimal insuredValue,

        Boolean floodZone,

        Boolean earthquakeRiskZone
) {
}
