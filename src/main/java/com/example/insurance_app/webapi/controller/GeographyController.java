package com.example.insurance_app.webapi.controller;

import com.example.insurance_app.application.dto.geogrophy.CityResponse;
import com.example.insurance_app.application.dto.geogrophy.CountryResponse;
import com.example.insurance_app.application.dto.geogrophy.CountyResponse;
import com.example.insurance_app.application.service.GeographyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/brokers/")
public class GeographyController {
    private final GeographyService geographyService;

    public GeographyController(GeographyService geographyService) {
        this.geographyService = geographyService;
    }

    @GetMapping("/countries")
    public List<CountryResponse> getCountries() {
        return geographyService.getCountries();
    }

    @GetMapping("countries/{countryId}/counties")
    public List<CountyResponse> getCountiesByCountry(@PathVariable UUID countryId) {
        return geographyService.getCountiesByCountryId(countryId);
    }

    @GetMapping("/counties/{countyId}/cities")
    public List<CityResponse> getCitiesByCounty(@PathVariable UUID countyId) {
        return geographyService.getCitiesByCountyId(countyId);
    }


}
