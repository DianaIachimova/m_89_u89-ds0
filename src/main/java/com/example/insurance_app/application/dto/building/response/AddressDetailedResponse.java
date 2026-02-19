package com.example.insurance_app.application.dto.building.response;

import com.example.insurance_app.application.dto.geography.CityResponse;
import com.example.insurance_app.application.dto.geography.CountryResponse;
import com.example.insurance_app.application.dto.geography.CountyResponse;

public record AddressDetailedResponse(
        String street,
        String streetNumber,
        CityResponse city,
        CountyResponse county,
        CountryResponse country
) {
}
