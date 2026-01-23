package com.example.insurance_app.webapi.mapper;

import com.example.insurance_app.application.dto.geogrophy.CityResponse;
import com.example.insurance_app.application.dto.geogrophy.CountryResponse;
import com.example.insurance_app.application.dto.geogrophy.CountyResponse;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CityProjection;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CountryProjection;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CountyProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GeographyMapper {
    CountryResponse toDto(CountryProjection projection);

    CountyResponse toDto(CountyProjection projection);

    CityResponse toDto(CityProjection projection);
}
