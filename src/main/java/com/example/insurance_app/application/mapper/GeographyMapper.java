package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.geography.CityResponse;
import com.example.insurance_app.application.dto.geography.CountryResponse;
import com.example.insurance_app.application.dto.geography.CountyResponse;
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
