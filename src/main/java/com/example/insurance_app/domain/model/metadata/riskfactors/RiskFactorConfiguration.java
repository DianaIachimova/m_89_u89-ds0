package com.example.insurance_app.domain.model.metadata.riskfactors;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.AdjustmentPercentage;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskFactorConfigurationId;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskTarget;
import com.example.insurance_app.domain.util.DomainAssertions;
import java.util.Objects;

public final class RiskFactorConfiguration {
    private final RiskFactorConfigurationId id;
    private final RiskTarget target;
    private AdjustmentPercentage percentage;
    private boolean active;
    private final AuditInfo audit;


    private RiskFactorConfiguration(RiskFactorConfigurationId id, RiskTarget target,
                                   AdjustmentPercentage percentage, boolean active,
                                   AuditInfo audit) {
        this.id = id;
        this.target = DomainAssertions.notNull(target, "Risk target");
        this.percentage = DomainAssertions.notNull(percentage, "Adjustment percentage");
        this.active = active;
        this.audit = audit;
    }

    public static RiskFactorConfiguration createNew(
            RiskTarget target,
            AdjustmentPercentage percentage,
            boolean active
    ) {
        return new RiskFactorConfiguration(null, target, percentage, active, null);
    }

    public static RiskFactorConfiguration rehydrate(
            RiskFactorConfigurationId id,
            RiskTarget target,
            AdjustmentPercentage percentage,
            boolean active,
            AuditInfo audit
    ) {
        return new RiskFactorConfiguration(id, target, percentage, active, audit);
    }

    public void updatePercentage(AdjustmentPercentage newPercentage) {
        this.percentage = DomainAssertions.notNull(newPercentage, "Adjustment percentage");
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public RiskFactorConfigurationId getId() {
        return id;
    }

    public RiskTarget getTarget() {
        return target;
    }

    public AdjustmentPercentage getPercentage() {
        return percentage;
    }

    public boolean isActive() {
        return active;
    }

    public AuditInfo getAudit() {
        return audit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RiskFactorConfiguration other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}



