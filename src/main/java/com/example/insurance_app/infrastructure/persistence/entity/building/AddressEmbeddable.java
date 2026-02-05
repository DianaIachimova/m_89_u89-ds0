package com.example.insurance_app.infrastructure.persistence.entity.building;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {
    @Column(name = "street", nullable = false, length = 200)
    private String street;

    @Column(name = "street_number", nullable = false, length = 20)
    private String streetNumber;

    protected AddressEmbeddable() {}

    public AddressEmbeddable(String street, String streetNumber) {
        this.street = street;
        this.streetNumber = streetNumber;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getStreet() {
        return street;
    }
}
