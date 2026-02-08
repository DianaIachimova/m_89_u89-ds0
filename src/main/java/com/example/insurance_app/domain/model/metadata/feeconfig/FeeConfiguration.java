package com.example.insurance_app.domain.model.metadata.feeconfig;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeConfigurationId;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeName;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeePercentage;
import com.example.insurance_app.domain.util.DomainAssertions;

import java.time.LocalDate;
import java.util.Objects;

public final class FeeConfiguration {
    private final FeeConfigurationId id;
    private FeeDetails details;
    private boolean active;
    private AuditInfo audit;

    private FeeConfiguration(FeeConfigurationId id, FeeDetails details, boolean active, AuditInfo audit) {
        this.id = id;
        this.details = DomainAssertions.notNull(details, "Fee configuration details");
        this.active = active;
        this.audit = audit;
    }

    public static FeeConfiguration createNew(
            FeeDetails details,
            boolean active)
    {
        return new FeeConfiguration(null, details, active, null);
    }

    public static FeeConfiguration rehydrate(
            FeeConfigurationId id,
            FeeDetails details,
            boolean active,
            AuditInfo audit
    ){
        return new FeeConfiguration(id, details, active, audit);
    }

    public void updateDetails(FeeName name, FeePercentage percentage, LocalDate newEnd) {
        DomainAssertions.notNull(percentage, "percentage");
        DomainAssertions.notNull(name, "name");

        var newPeriod=details.period().changeEnd(newEnd);

        this.details = FeeDetails.of(
                details.code(), name, details.type(), percentage,newPeriod
        );

    }

    public void deactivate() {
        if (details.period().to() == null)
            this.details.period().changeEnd(LocalDate.now());

        this.active = false;
    }

    public boolean isValidOn(LocalDate date) {
        DomainAssertions.notNull(date, "date");
        return active && details.period().includes(date);
    }

    public FeeConfigurationId getId() {
        return id;
    }

    public FeeDetails getDetails() {
        return details;
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
        if (!(o instanceof FeeConfiguration other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
