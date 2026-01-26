package com.example.insurance_app.application.dto.client.response;

public record AddressResponse(
        String street,
        String city,
        String county,
        String postalCode,
        String country
) {
}
