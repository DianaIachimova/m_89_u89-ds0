package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.geography.CityResponse;
import com.example.insurance_app.application.dto.geography.CountryResponse;
import com.example.insurance_app.application.dto.geography.CountyResponse;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountryEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountyEntity;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CityProjection;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CountryProjection;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CountyProjection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GeographyMapper {
    CountryResponse toDto(CountryProjection projection);

    CountyResponse toDto(CountyProjection projection);

    CityResponse toDto(CityProjection projection);

    CountryResponse toDto(CountryEntity entity);

    CountyResponse toDto(CountyEntity entity);

    CityResponse toDto(CityEntity entity);
}
