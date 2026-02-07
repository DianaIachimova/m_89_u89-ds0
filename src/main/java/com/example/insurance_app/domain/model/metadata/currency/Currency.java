package com.example.insurance_app.domain.model.metadata.currency;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyCode;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyId;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyName;
import com.example.insurance_app.domain.model.metadata.currency.vo.ExchangeRateToBase;
import com.example.insurance_app.domain.util.DomainAssertions;
import java.util.Objects;

public class Currency {
    private final CurrencyId id;
    private final CurrencyCode code;
    private CurrencyName name;
    private ExchangeRateToBase exchangeRate;
    private boolean active;
    private AuditInfo audit;

    private Currency(CurrencyId id, CurrencyCode code, CurrencyName name, ExchangeRateToBase exchangeRate, boolean active, AuditInfo audit) {
        this.id =id;
        this.code = DomainAssertions.notNull(code, "code");
        this.name = DomainAssertions.notNull(name, "name");
        this.exchangeRate = DomainAssertions.notNull(exchangeRate, "exchangeRate");
        this.active = active;
        this.audit = audit;
    }
    public static Currency createNew(
            CurrencyCode code,
            CurrencyName name,
            ExchangeRateToBase exchangeRate,
            boolean active
    ) {
        return new Currency(null, code, name, exchangeRate, active, null);
    }

    public static Currency rehydrate(
            CurrencyId id,
            CurrencyCode code,
            CurrencyName name,
            ExchangeRateToBase exchangeRate,
            boolean active,
            AuditInfo audit
    ) {
        return new Currency(id, code, name, exchangeRate, active, audit);
    }

    public void activate() {
        if(active) return;
        this.active = true;
    }

    public void deactivate() {
        if(!active) return;
        this.active = false;
    }

    public CurrencyId getId() {
        return id;
    }

    public CurrencyCode getCode() {
        return code;
    }

    public CurrencyName getName() {
        return name;
    }

    public ExchangeRateToBase getExchangeRate() {
        return exchangeRate;
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
        if (!(o instanceof Currency other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
