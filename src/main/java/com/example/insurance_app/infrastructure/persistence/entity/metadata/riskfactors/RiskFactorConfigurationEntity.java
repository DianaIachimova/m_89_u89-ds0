package com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors;

import jakarta.persistence.*;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "risk_factor_configurations")
@EntityListeners(AuditingEntityListener.class)
public class RiskFactorConfigurationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private RiskLevelEntity level;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "building_type", length = 30)
    private BuildingTypeEntity buildingType;

    @Column(name = "adjustment_percentage", nullable = false, precision = 5, scale = 4)
    private BigDecimal adjustmentPercentage;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected RiskFactorConfigurationEntity() {
    }

    public RiskFactorConfigurationEntity(UUID id, RiskLevelEntity level, UUID referenceId,
                                         BuildingTypeEntity buildingType, BigDecimal adjustmentPercentage,
                                         boolean active) {
        this.id = id;
        this.level = level;
        this.referenceId = referenceId;
        this.buildingType = buildingType;
        this.adjustmentPercentage = adjustmentPercentage;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public RiskLevelEntity getLevel() {
        return level;
    }

    public UUID getReferenceId() {
        return referenceId;
    }

    public BuildingTypeEntity getBuildingType() {
        return buildingType;
    }

    public BigDecimal getAdjustmentPercentage() {
        return adjustmentPercentage;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setAdjustmentPercentage(BigDecimal adjustmentPercentage) {
        this.adjustmentPercentage = adjustmentPercentage;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RiskFactorConfigurationEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
