package com.example.insurance_app.infrastructure.persistence.entity.policy;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "policy_pricing_snapshot_items", indexes = {
        @Index(name = "idx_snapshot_items_snapshot", columnList = "snapshot_id"),
        @Index(name = "idx_snapshot_items_source", columnList = "source_type, source_id")
})
public class PolicyPricingSnapshotItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "snapshot_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_item_snapshot"))
    private PolicyPricingSnapshotEntity snapshot;

    @Column(name = "source_type", nullable = false, length = 30)
    private String sourceType;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "percentage", nullable = false, precision = 6, scale = 4)
    private BigDecimal percentage;

    @Column(name = "applied_order", nullable = false)
    private int appliedOrder;

    public PolicyPricingSnapshotItemEntity() {
        //no arg
    }

    public UUID getId() {
        return id;
    }

    public PolicyPricingSnapshotEntity getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(PolicyPricingSnapshotEntity snapshot) {
        this.snapshot = snapshot;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public void setSourceId(UUID sourceId) {
        this.sourceId = sourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public int getAppliedOrder() {
        return appliedOrder;
    }

    public void setAppliedOrder(int appliedOrder) {
        this.appliedOrder = appliedOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyPricingSnapshotItemEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
