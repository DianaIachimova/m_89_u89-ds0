package com.example.insurance_app.application.dto.building.response;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import java.math.BigDecimal;

public record BuildingInfoResponse(
        Integer constructionYear,
        BuildingTypeDto buildingType,
        Integer numberOfFloors,
        BigDecimal surfaceArea,
        BigDecimal insuredValue
) {
}
