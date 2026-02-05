package com.example.insurance_app.domain.model.building;

import com.example.insurance_app.domain.model.building.vo.BuildingAddress;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.building.vo.BuildingInfo;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.util.DomainAssertions;
import java.util.Objects;
import java.util.UUID;

public class Building {

    private final BuildingId id;
    private final ClientId ownerId;
    private BuildingAddress address;
    private UUID cityId;
    private BuildingInfo buildingInfo;
    private RiskIndicators riskIndicators;


    public Building(ClientId ownerId, BuildingAddress address, UUID cityId,
                    BuildingInfo buildingInfo, RiskIndicators riskIndicators)
    {
        this(null,ownerId, address, cityId, buildingInfo, riskIndicators);
    }

    public Building(BuildingId id, ClientId ownerId, BuildingAddress address, UUID cityId,
            BuildingInfo buildingInfo, RiskIndicators riskIndicators)
    {
        this.id = id;
        this.ownerId = ownerId;
        this.address = DomainAssertions.notNull(address, "address");
        this.cityId = DomainAssertions.notNull(cityId,  "cityId");
        this.buildingInfo = DomainAssertions.notNull(buildingInfo, "buildingInfo");
        this.riskIndicators = riskIndicators;
    }


    public void updateInformation(BuildingAddress address, UUID cityId, BuildingInfo buildingInfo,
                                  RiskIndicators riskIndicators) {

        DomainAssertions.notNull(address, "address");
        DomainAssertions.notNull(cityId, "City ID");
        DomainAssertions.notNull(buildingInfo, "buildingInfo");


        this.address = address;
        this.cityId = cityId;
        this.buildingInfo = buildingInfo;

        if(riskIndicators != null)
            this.riskIndicators = riskIndicators;
    }

    public BuildingId getId() {
        return id;
    }

    public ClientId getOwnerId() {
        return ownerId;
    }

    public BuildingAddress getAddress() {
        return address;
    }

    public UUID getCityId() {
        return cityId;
    }

    public BuildingInfo getBuildingInfo() {
        return buildingInfo;
    }

    public RiskIndicators getRiskIndicators() {
        return riskIndicators;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Building other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
