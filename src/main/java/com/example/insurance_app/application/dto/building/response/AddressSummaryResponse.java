package com.example.insurance_app.application.dto.building.response;

import com.example.insurance_app.application.dto.geogrophy.CityResponse;

public record AddressSummaryResponse(
        String street,
        String streetNumber,
        CityResponse city
) {
}
