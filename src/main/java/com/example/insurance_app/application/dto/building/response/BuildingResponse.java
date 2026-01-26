package com.example.insurance_app.application.dto.building.response;

import com.example.insurance_app.application.dto.building.BuildingTypeDto;
import com.example.insurance_app.application.dto.geogrophy.CityResponse;
import com.example.insurance_app.application.dto.geogrophy.CountryResponse;
import com.example.insurance_app.application.dto.geogrophy.CountyResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BuildingResponse(
        UUID id,
        UUID ownerId,
        String ownerName,
        String street,
        String streetNumber,
        CityResponse city,
        CountyResponse county,
        CountryResponse country,
        Integer constructionYear,
        BuildingTypeDto buildingType,
        Integer numberOfFloors,
        BigDecimal surfaceArea,
        BigDecimal insuredValue,
        Boolean floodZone,
        Boolean earthquakeRiskZone,
        Instant createdAt,
        Instant updatedAt
) {
}
