package com.example.insurance_app.infrastructure.persistence.entity.building;

import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "buildings",
        indexes = {
                @Index(name = "idx_buildings_owner", columnList = "owner_id"),
                @Index(name = "idx_buildings_city", columnList = "city_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class BuildingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_buildings_owner"))
    private ClientEntity owner;

    @Column(name = "street", nullable = false, length = 200)
    private String street;

    @Column(name = "street_number", nullable = false, length = 20)
    private String streetNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_buildings_city"))
    private CityEntity city;

    @Column(name = "construction_year", nullable = false)
    private Integer constructionYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "building_type", nullable = false, length = 20)
    private BuildingTypeEntity buildingType;

    @Column(name = "number_of_floors")
    private Integer numberOfFloors;

    @Column(name = "surface_area", nullable = false, precision = 10, scale = 2)
    private BigDecimal surfaceArea;

    @Column(name = "insured_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal insuredValue;

    @Column(name = "flood_zone", nullable = false)
    private Boolean floodZone;

    @Column(name = "earthquake_risk_zone", nullable = false)
    private Boolean earthquakeRiskZone;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BuildingEntity() {
    }

    public BuildingEntity(UUID id, ClientEntity owner, String street, String streetNumber,
                          CityEntity city, Integer constructionYear, BuildingTypeEntity buildingType,
                          Integer numberOfFloors, BigDecimal surfaceArea, BigDecimal insuredValue,
                          Boolean floodZone, Boolean earthquakeRiskZone) {
        this.id = id;
        this.owner = owner;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.constructionYear = constructionYear;
        this.buildingType = buildingType;
        this.numberOfFloors = numberOfFloors;
        this.surfaceArea = surfaceArea;
        this.insuredValue = insuredValue;
        this.floodZone = floodZone;
        this.earthquakeRiskZone = earthquakeRiskZone;
    }

    public UUID getId() {
        return id;
    }

    public ClientEntity getOwner() {
        return owner;
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public CityEntity getCity() {
        return city;
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

    public Boolean getFloodZone() {
        return floodZone;
    }

    public Boolean getEarthquakeRiskZone() {
        return earthquakeRiskZone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public void setConstructionYear(Integer constructionYear) {
        this.constructionYear = constructionYear;
    }

    public void setBuildingType(BuildingTypeEntity buildingType) {
        this.buildingType = buildingType;
    }

    public void setNumberOfFloors(Integer numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    public void setSurfaceArea(BigDecimal surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public void setInsuredValue(BigDecimal insuredValue) {
        this.insuredValue = insuredValue;
    }

    public void setFloodZone(Boolean floodZone) {
        this.floodZone = floodZone;
    }

    public void setEarthquakeRiskZone(Boolean earthquakeRiskZone) {
        this.earthquakeRiskZone = earthquakeRiskZone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuildingEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
