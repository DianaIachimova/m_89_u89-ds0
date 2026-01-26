package com.example.insurance_app.domain.model.vo;

import com.example.insurance_app.domain.util.DomainAssertions;

import java.util.UUID;

public record ClientId(UUID value) {

    public ClientId {
       DomainAssertions.notNull(value, "Client ID");
    }
}

