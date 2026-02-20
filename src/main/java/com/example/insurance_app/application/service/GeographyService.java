package com.example.insurance_app.application.service;

import com.example.insurance_app.application.dto.geography.CityResponse;
import com.example.insurance_app.application.dto.geography.CountryResponse;
import com.example.insurance_app.application.dto.geography.CountyResponse;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.infrastructure.config.cache.CacheNames;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CityRepository;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CountryRepository;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CountyRepository;
import com.example.insurance_app.application.mapper.GeographyMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@Transactional(readOnly=true)
public class GeographyService {
    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final CityRepository cityRepository;

    private static final Logger logger = LoggerFactory.getLogger(GeographyService.class);
    private final GeographyMapper geographyMapper;

    public GeographyService(CountryRepository countryRepository, CountyRepository countyRepository, CityRepository cityRepository, GeographyMapper geographyMapper) {
        this.countryRepository = countryRepository;
        this.countyRepository = countyRepository;
        this.cityRepository = cityRepository;
        this.geographyMapper = geographyMapper;
    }

    @Cacheable(CacheNames.COUNTRIES)
    public List<CountryResponse> getCountries() {
        logger.info("Fetching all countries");
        var views = countryRepository.findAllByOrderByNameAsc();
        logger.info("Fetched {} countries", views.size());
        return views.stream().map(geographyMapper::toDto).toList();
    }

    @Cacheable(value = CacheNames.COUNTIES, key = "#countryId")
    public List<CountyResponse> getCountiesByCountryId(UUID countryId) {
        logger.info("Fetching all counties for country {}", countryId);

        if(!countryRepository.existsById(countryId)) {
            logger.warn("Country with id {} not found", countryId);
            throw new ResourceNotFoundException("Country", "id", countryId);
        }

        var views = countyRepository.findByCountryIdOrderByNameAsc(countryId);
        logger.info("Fetched {} counties for country id {}", views.size(), countryId);
        return views.stream().map(geographyMapper::toDto).toList();

    }

    @Cacheable(value = CacheNames.CITIES, key = "#countyId")
    public List<CityResponse> getCitiesByCountyId(UUID countyId) {
        logger.info("Fetching all cities for county {}", countyId);

        if(!countyRepository.existsById(countyId)) {
            logger.warn("County with id {} not found", countyId);
            throw new ResourceNotFoundException("County", "id", countyId);
        }

        var views = cityRepository.findByCountyIdOrderByNameAsc(countyId);
        logger.info("Fetched {} cities for county id {}", views.size(), countyId);
        return views.stream().map(geographyMapper::toDto).toList();
    }


}
