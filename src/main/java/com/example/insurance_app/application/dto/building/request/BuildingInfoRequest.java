package com.example.insurance_app.application.dto.building.request;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BuildingInfoRequest(
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
        BigDecimal insuredValue
) {

}
