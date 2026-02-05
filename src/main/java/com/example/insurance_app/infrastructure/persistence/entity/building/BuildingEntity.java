package com.example.insurance_app.infrastructure.persistence.entity.building;

import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @JoinColumn(
            name = "owner_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_buildings_owner"))
    private ClientEntity owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "city_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_buildings_city"))
    private CityEntity city;

    @Embedded
    AddressEmbeddable address;

    @Embedded
    BuildingInfoEmbeddable  buildingInfo;

    @Embedded
    RiskIndicatorsEmbeddable riskIndicators;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BuildingEntity() {
    }

    public BuildingEntity(UUID id, ClientEntity owner, CityEntity city, AddressEmbeddable address, BuildingInfoEmbeddable buildingInfo, RiskIndicatorsEmbeddable riskIndicators) {
        this.id = id;
        this.owner = owner;
        this.city = city;
        this.address = address;
        this.buildingInfo = buildingInfo;
        this.riskIndicators = riskIndicators;
    }

    public UUID getId() {
        return id;
    }

    public ClientEntity getOwner() {
        return owner;
    }

    public CityEntity getCity() {
        return city;
    }

    public AddressEmbeddable getAddress() {
        return address;
    }

    public BuildingInfoEmbeddable getBuildingInfo() {
        return buildingInfo;
    }

    public RiskIndicatorsEmbeddable getRisk() {
        return riskIndicators;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setOwner(ClientEntity owner) {
        this.owner = owner;
    }

    public void setCity(CityEntity city) {
        this.city = city;
    }

    public void setAddress(AddressEmbeddable address) {
        this.address = address;
    }

    public void setBuildingInfo(BuildingInfoEmbeddable buildingInfo) {
        this.buildingInfo = buildingInfo;
    }

    public void setRisk(RiskIndicatorsEmbeddable risk) {
        this.riskIndicators = risk;
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
