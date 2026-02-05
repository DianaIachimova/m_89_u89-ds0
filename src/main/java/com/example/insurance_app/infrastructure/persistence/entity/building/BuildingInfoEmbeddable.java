package com.example.insurance_app.infrastructure.persistence.entity.building;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

@Embeddable
public class BuildingInfoEmbeddable {
    @Column(name = "construction_year", nullable = false)
    private Integer constructionYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "building_type", nullable = false)
    private BuildingTypeEntity buildingType;

    @Column(name = "number_of_floors")
    private Integer numberOfFloors;

    @Column(name = "surface_area", nullable = false, precision = 10, scale = 2)
    private BigDecimal surfaceArea;

    @Column(name = "insured_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal insuredValue;

    protected  BuildingInfoEmbeddable() {
    }

    public BuildingInfoEmbeddable(Integer constructionYear, BuildingTypeEntity buildingType, Integer numberOfFloors, BigDecimal surfaceArea, BigDecimal insuredValue) {
        this.constructionYear = constructionYear;
        this.buildingType = buildingType;
        this.numberOfFloors = numberOfFloors;
        this.surfaceArea = surfaceArea;
        this.insuredValue = insuredValue;
    }

    public Integer getConstructionYear() {
        return constructionYear;
    }

    public BuildingTypeEntity getBuildingType() {
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
}
