package com.example.insurance_app.domain.model.policy.vo;

import com.example.insurance_app.domain.model.broker.vo.BrokerId;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyId;
import com.example.insurance_app.domain.util.DomainAssertions;

public record PolicyReferences(
        ClientId clientId,
        BuildingId buildingId,
        BrokerId brokerId,
        CurrencyId currencyId
) {
    public PolicyReferences {
        DomainAssertions.notNull(clientId, "Client");
        DomainAssertions.notNull(buildingId, "Building");
        DomainAssertions.notNull(brokerId, "Broker");
        DomainAssertions.notNull(currencyId, "Currency");
    }
}
