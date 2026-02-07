package com.example.insurance_app.domain.model.metadata.currency.vo;

import com.example.insurance_app.domain.util.DomainAssertions;
import java.util.UUID;

public record CurrencyId(UUID value) {
    public CurrencyId {
        DomainAssertions.notNull(value, "Currency ID");
    }
}

