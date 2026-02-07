package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.metadata.currency.Currency;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyCode;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyId;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyName;
import com.example.insurance_app.domain.model.metadata.currency.vo.ExchangeRateToBase;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import org.springframework.stereotype.Component;

@Component
public class CurrencyEntityMapper {

    public Currency toDomain(CurrencyEntity entity) {
        if (entity == null)
            return null;

        return Currency.rehydrate(
                new CurrencyId(entity.getId()),
                new CurrencyCode(entity.getCode()),
                new CurrencyName(entity.getName()),
                new ExchangeRateToBase(entity.getExchangeRateToBase()),
                entity.isActive(),
                new AuditInfo(entity.getCreatedAt(), entity.getUpdatedAt())
        );

    }

    public CurrencyEntity toEntity(Currency domain) {
        if (domain == null)
            return null;

        return new CurrencyEntity(
                domain.getId() != null ? domain.getId().value() : null,
                domain.getCode().code(),
                domain.getName().name(),
                domain.getExchangeRate().exchangeRate(),
                domain.isActive()
        );
    }

    public void updateEntity(Currency domain, CurrencyEntity entity) {
        if (domain == null || entity == null)
            return;

        entity.setActive(domain.isActive());
    }
}
