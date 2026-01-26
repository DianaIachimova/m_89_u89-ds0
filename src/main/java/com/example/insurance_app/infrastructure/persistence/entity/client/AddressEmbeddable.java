package com.example.insurance_app.infrastructure.persistence.entity.client;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {
    @Column(name = "street", nullable = true, length = 200)
    private String street;

    @Column(name = "city", nullable = true, length = 100)
    private String city;

    @Column(name = "county", nullable = true, length = 100)
    private String county;

    @Column(name = "postal_code", nullable = true, length = 6)
    private String postalCode;

    @Column(name = "country", nullable = true, length = 100)
    private String country;

    public AddressEmbeddable() {
    }

    public AddressEmbeddable(String street, String city, String county, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.county = county;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getCounty() {
        return county;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }
}
