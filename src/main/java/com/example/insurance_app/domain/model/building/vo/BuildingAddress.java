package com.example.insurance_app.domain.model.building.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record BuildingAddress(String street, String streetNumber) {
    public BuildingAddress {
        DomainAssertions.notBlank(street, "Street");
        DomainAssertions.notBlank(streetNumber, "Street number");

        street = DomainAssertions.normalize(street);
        streetNumber = DomainAssertions.normalize(streetNumber);
    }
}
