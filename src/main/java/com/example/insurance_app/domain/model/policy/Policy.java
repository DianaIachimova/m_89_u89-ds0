package com.example.insurance_app.domain.model.policy;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.policy.vo.*;
import com.example.insurance_app.domain.util.DomainAssertions;

import java.time.LocalDate;
import java.util.Objects;

public class Policy {

    private final PolicyIdentity identity;
    private final PolicyReferences references;
    private PolicyStatus status;
    private final PolicyPeriod period;
    private PolicyPremium premium;
    private CancellationInfo cancellationInfo;
    private AuditInfo audit;

    private Policy(PolicyIdentity identity,
                   PolicyReferences references,
                   PolicyStatus status,
                   PolicyPeriod period,
                   PolicyPremium premium,
                   CancellationInfo cancellationInfo,
                   AuditInfo audit) {
        this.identity = DomainAssertions.notNull(identity, "Policy identity");
        this.references = DomainAssertions.notNull(references, "Policy references");
        this.status = DomainAssertions.notNull(status, "Status");
        this.period = DomainAssertions.notNull(period, "Policy period");
        this.premium = DomainAssertions.notNull(premium, "Premium");
        this.cancellationInfo = cancellationInfo;
        this.audit = audit;
    }

    public static Policy createDraft(PolicyNumber policyNumber,
                                      PolicyReferences references,
                                      PolicyPeriod period,
                                      PolicyPremium premium) {
        return new Policy(PolicyIdentity.ofNew(policyNumber), references,
                PolicyStatus.DRAFT, period, premium, null, null);
    }

    public static Policy rehydrate(PolicyIdentity identity,
                                    PolicyReferences references,
                                    PolicyStatus status,
                                    PolicyPeriod period,
                                    PolicyPremium premium,
                                    CancellationInfo cancellationInfo,
                                    AuditInfo audit) {
        return new Policy(identity, references,
                status, period, premium, cancellationInfo, audit);
    }

    public void activate(PremiumAmount recalculatedFinalPremium) {
        //if(status == PolicyStatus.ACTIVE) return;
        DomainAssertions.check(status == PolicyStatus.DRAFT,
                "Only DRAFT policies can be activated");
        DomainAssertions.check(!period.startDate().isBefore(LocalDate.now()),
                "Start date cannot be in the past");
        DomainAssertions.notNull(recalculatedFinalPremium, "Final premium");
        this.premium = new PolicyPremium(premium.base(), recalculatedFinalPremium);
        this.status = PolicyStatus.ACTIVE;
    }

    public void cancel(String reason) {
        //if(status == PolicyStatus.CANCELLED) return;
        DomainAssertions.check(status == PolicyStatus.ACTIVE,
                "Only ACTIVE policies can be cancelled");
        this.status = PolicyStatus.CANCELLED;
        this.cancellationInfo = new CancellationInfo(LocalDate.now(), reason);
    }

    public void expire() {
        DomainAssertions.check(status == PolicyStatus.ACTIVE,
                "Only ACTIVE policies can expire");
        this.status = PolicyStatus.EXPIRED;
    }

    public PolicyIdentity getIdentity() {
        return identity;
    }

    public PolicyId getId() {
        return identity.id();
    }

    public PolicyNumber getPolicyNumber() {
        return identity.number();
    }

    public PolicyReferences getReferences() {
        return references;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public PolicyPeriod getPeriod() {
        return period;
    }

    public PolicyPremium getPremium() {
        return premium;
    }

    public PremiumAmount getBasePremium() {
        return premium.base();
    }

    public PremiumAmount getFinalPremium() {
        return premium.finalAmount();
    }

    public CancellationInfo getCancellationInfo() {
        return cancellationInfo;
    }

    public AuditInfo getAudit() {
        return audit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Policy other)) return false;
        return identity.id() != null && identity.id().equals(other.identity.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identity.id());
    }
}
