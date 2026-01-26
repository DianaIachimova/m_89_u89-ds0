package com.example.insurance_app.domain.model;

import com.example.insurance_app.domain.model.vo.BuildingId;
import com.example.insurance_app.domain.model.vo.ClientId;
import com.example.insurance_app.domain.util.DomainAssertions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Building {

    private final BuildingId id;
    private final ClientId ownerId;
    private String street;
    private String streetNumber;
    private UUID cityId;
    private Integer constructionYear;
    private BuildingType buildingType;
    private Integer numberOfFloors;
    private BigDecimal surfaceArea;
    private BigDecimal insuredValue;
    private Boolean floodZone;
    private Boolean earthquakeRiskZone;

    public Building(ClientId ownerId, String street, String streetNumber, UUID cityId,
                    Integer constructionYear, BuildingType buildingType, Integer numberOfFloors,
                    BigDecimal surfaceArea, BigDecimal insuredValue, Boolean floodZone,
                    Boolean earthquakeRiskZone) {
        this(null, ownerId, street, streetNumber, cityId, constructionYear, buildingType,
                numberOfFloors, surfaceArea, insuredValue, floodZone, earthquakeRiskZone);
    }

    public Building(BuildingId id, ClientId ownerId, String street, String streetNumber, UUID cityId,
                    Integer constructionYear, BuildingType buildingType, Integer numberOfFloors,
                    BigDecimal surfaceArea, BigDecimal insuredValue, Boolean floodZone,
                    Boolean earthquakeRiskZone) {

        validateRequired(ownerId, street, streetNumber, cityId, constructionYear, buildingType,
                surfaceArea, insuredValue);
        
        this.id = id;
        this.ownerId = ownerId;
        this.street = DomainAssertions.normalize(street);
        this.streetNumber = DomainAssertions.normalize(streetNumber);
        this.cityId = cityId;
        this.constructionYear = constructionYear;
        this.buildingType = buildingType;
        this.numberOfFloors = numberOfFloors;
        this.surfaceArea = surfaceArea;
        this.insuredValue = insuredValue;
        this.floodZone = floodZone != null ? floodZone : false;
        this.earthquakeRiskZone = earthquakeRiskZone != null ? earthquakeRiskZone : false;

        validateBusinessRules();
    }

    private static void validateRequired(ClientId ownerId, String street, String streetNumber,
                                         UUID cityId, Integer constructionYear, BuildingType buildingType,
                                         BigDecimal surfaceArea, BigDecimal insuredValue) {
        DomainAssertions.notNull(ownerId, "Owner ID");
        DomainAssertions.notBlank(street, "Street");
        DomainAssertions.notBlank(streetNumber, "Street number");
        DomainAssertions.notNull(cityId, "City ID");
        DomainAssertions.notNull(constructionYear, "Construction year");
        DomainAssertions.notNull(buildingType, "Building type");
        DomainAssertions.notNull(surfaceArea, "Surface area");
        DomainAssertions.notNull(insuredValue, "Insured value");
    }

    private void validateBusinessRules() {
        int currentYear = java.time.Year.now().getValue();
        DomainAssertions.check(
                constructionYear > 1800 && constructionYear <= currentYear,
                "Construction year must be between 1800 and " + currentYear
        );

        DomainAssertions.check(
                surfaceArea.compareTo(BigDecimal.ZERO) > 0,
                "Surface area must be positive"
        );

        DomainAssertions.check(
                insuredValue.compareTo(BigDecimal.ZERO) > 0,
                "Insured value must be positive"
        );

        if (numberOfFloors != null) {
            DomainAssertions.check(
                    numberOfFloors > 0 && numberOfFloors <= 200,
                    "Number of floors must be between 1 and 200"
            );
        }
    }

    public void updateInformation(String street, String streetNumber, UUID cityId,
                                  Integer constructionYear, BuildingType buildingType,
                                  Integer numberOfFloors, BigDecimal surfaceArea,
                                  BigDecimal insuredValue, Boolean floodZone,
                                  Boolean earthquakeRiskZone) {

        DomainAssertions.notBlank(street, "Street");
        DomainAssertions.notBlank(streetNumber, "Street number");
        DomainAssertions.notNull(cityId, "City ID");
        DomainAssertions.notNull(constructionYear, "Construction year");
        DomainAssertions.notNull(buildingType, "Building type");
        DomainAssertions.notNull(surfaceArea, "Surface area");
        DomainAssertions.notNull(insuredValue, "Insured value");

        this.street = DomainAssertions.normalize(street);
        this.streetNumber = DomainAssertions.normalize(streetNumber);
        this.cityId = cityId;
        this.constructionYear = constructionYear;
        this.buildingType = buildingType;
        this.numberOfFloors = numberOfFloors;
        this.surfaceArea = surfaceArea;
        this.insuredValue = insuredValue;
        this.floodZone = floodZone != null ? floodZone : false;
        this.earthquakeRiskZone = earthquakeRiskZone != null ? earthquakeRiskZone : false;

        validateBusinessRules();
    }

    public BuildingId getId() {
        return id;
    }

    public ClientId getOwnerId() {
        return ownerId;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public UUID getCityId() {
        return cityId;
    }

    public Integer getConstructionYear() {
        return constructionYear;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public Integer getNumberOfFloors() {
        return numberOfFloors;
    }

    public BigDecimal getSurfaceArea() {
        return surfaceArea;
    }

    public BigDecimal getInsuredValue() {
        return insuredValue;
    }

    public Boolean getFloodZone() {
        return floodZone;
    }

    public Boolean getEarthquakeRiskZone() {
        return earthquakeRiskZone;
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
