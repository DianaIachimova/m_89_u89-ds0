package com.example.insurance_app.infrastructure.persistence.entity.policy;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "policy_pricing_snapshots", uniqueConstraints = {
        @UniqueConstraint(name = "uk_snapshot_policy", columnNames = "policy_id")
})
public class PolicyPricingSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_snapshot_policy"))
    private PolicyEntity policy;

    @Column(name = "base_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePremium;

    @Column(name = "final_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalPremium;

    @Column(name = "total_fee_pct", nullable = false, precision = 6, scale = 4)
    private BigDecimal totalFeePct;

    @Column(name = "total_risk_pct", nullable = false, precision = 6, scale = 4)
    private BigDecimal totalRiskPct;

    @Column(name = "snapshot_date", nullable = false)
    private Instant snapshotDate;

    @OneToMany(mappedBy = "snapshot", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("appliedOrder ASC")
    private List<PolicyPricingSnapshotItemEntity> items = new ArrayList<>();

    public PolicyPricingSnapshotEntity() {
        //no arg
    }

    public UUID getId() {
        return id;
    }

    public PolicyEntity getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyEntity policy) {
        this.policy = policy;
    }

    public BigDecimal getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(BigDecimal basePremium) {
        this.basePremium = basePremium;
    }

    public BigDecimal getFinalPremium() {
        return finalPremium;
    }

    public void setFinalPremium(BigDecimal finalPremium) {
        this.finalPremium = finalPremium;
    }

    public BigDecimal getTotalFeePct() {
        return totalFeePct;
    }

    public void setTotalFeePct(BigDecimal totalFeePct) {
        this.totalFeePct = totalFeePct;
    }

    public BigDecimal getTotalRiskPct() {
        return totalRiskPct;
    }

    public void setTotalRiskPct(BigDecimal totalRiskPct) {
        this.totalRiskPct = totalRiskPct;
    }

    public Instant getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Instant snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public List<PolicyPricingSnapshotItemEntity> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyPricingSnapshotEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
