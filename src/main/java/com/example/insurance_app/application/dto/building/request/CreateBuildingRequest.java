package com.example.insurance_app.application.dto.building.request;

import com.example.insurance_app.application.dto.building.RiskIndicatorsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


public record CreateBuildingRequest(

        @NotNull(message = "Address is required")
        @Valid
        AddressRequest address,

        @NotNull(message = "Details are required")
        @Valid
        BuildingInfoRequest buildingDetails,

        @Valid
        RiskIndicatorsDto riskIndicators
) {
}
