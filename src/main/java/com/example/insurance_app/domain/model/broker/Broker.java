package com.example.insurance_app.domain.model.broker;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.broker.vo.*;
import com.example.insurance_app.domain.util.DomainAssertions;

import java.util.Objects;

public class Broker {

    private final BrokerId id;
    private final BrokerCode code;
    private BrokerName name;
    private ContactInfo contactInfo;
    private BrokerStatus status;
    private CommissionPercentage commissionPercentage;
    private AuditInfo audit;

    private Broker(BrokerId id,
                   BrokerCode code,
                   BrokerName name,
                   ContactInfo contactInfo,
                   BrokerStatus status,
                   CommissionPercentage commissionPercentage,
                   AuditInfo audit) {
        this.id = id;
        this.code = DomainAssertions.notNull(code, "Broker code");
        this.name = DomainAssertions.notNull(name, "Broker name");
        this.contactInfo = DomainAssertions.notNull(contactInfo, "Contact info");
        this.status = DomainAssertions.notNull(status, "Status");
        this.commissionPercentage = commissionPercentage;
        this.audit = audit;
    }

    public static Broker createNew(BrokerCode code, BrokerName name,
                                   ContactInfo contactInfo, BrokerStatus status,
                                   CommissionPercentage commissionPercentage) {
        return new Broker(null, code, name, contactInfo, status, commissionPercentage, null);
    }

    public static Broker rehydrate(BrokerId id, BrokerCode code, BrokerName name,
                                   ContactInfo contactInfo, BrokerStatus status,
                                   CommissionPercentage commissionPercentage, AuditInfo audit) {
        return new Broker(id, code, name, contactInfo, status, commissionPercentage, audit);
    }

    public void activate() {
        if (this.status == BrokerStatus.ACTIVE) {
            throw new DomainValidationException("Broker is already active");
        }
        this.status = BrokerStatus.ACTIVE;
    }

    public void deactivate() {
        if (this.status == BrokerStatus.INACTIVE) {
            throw new DomainValidationException("Broker is already inactive");
        }
        this.status = BrokerStatus.INACTIVE;
    }

    public void ensureActive() {
        if (this.status != BrokerStatus.ACTIVE) {
            throw new DomainValidationException("Broker is not active");
        }
    }

    public void updateDetails(BrokerName name, ContactInfo contactInfo,
                              CommissionPercentage commissionPercentage) {
        this.name = DomainAssertions.notNull(name, "Broker name");
        this.contactInfo = DomainAssertions.notNull(contactInfo, "Contact info");
        this.commissionPercentage = commissionPercentage;
    }

    public BrokerId getId() {
        return id;
    }

    public BrokerCode getCode() {
        return code;
    }

    public BrokerName getName() {
        return name;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public BrokerStatus getStatus() {
        return status;
    }

    public CommissionPercentage getCommissionPercentage() {
        return commissionPercentage;
    }

    public AuditInfo getAudit() {
        return audit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Broker other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
