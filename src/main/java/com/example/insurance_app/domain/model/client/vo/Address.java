package com.example.insurance_app.domain.model.client.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

public record Address(
        String street,
        String city,
        String county,
        String postalCode,
        String country) {

    public Address {
        DomainAssertions.notBlank(street, "Street");
        DomainAssertions.notBlank(city, "City");
        DomainAssertions.notBlank(country, "Country");

        street = DomainAssertions.normalize(street);
        city = DomainAssertions.normalize(city);
        country = DomainAssertions.normalize(country);

        if (postalCode == null || postalCode.isBlank()) {
            postalCode = null;
        } else {
            var pc = DomainAssertions.normalize(postalCode).replaceAll("\\s+", "");
            DomainAssertions.check(pc.matches("^[0-9]{6}$"), "Postal code must be 6 digits");
            postalCode = pc;
        }

        county = (county == null || county.isBlank()) ? null : DomainAssertions.normalize(county);
    }


}