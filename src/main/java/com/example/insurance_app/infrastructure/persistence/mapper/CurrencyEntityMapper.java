package com.example.insurance_app.infrastructure.persistence.mapper;

import com.example.insurance_app.domain.model.AuditInfo;
import com.example.insurance_app.domain.model.metadata.currency.Currency;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyCode;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyId;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyName;
import com.example.insurance_app.domain.model.metadata.currency.vo.ExchangeRateToBase;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CurrencyEntityMapper {

    default Currency toDomain(CurrencyEntity entity) {
        if (entity == null) return null;
        return Currency.rehydrate(
                new CurrencyId(entity.getId()),
                new CurrencyCode(entity.getCode()),
                new CurrencyName(entity.getName()),
                new ExchangeRateToBase(entity.getExchangeRateToBase()),
                entity.isActive(),
                new AuditInfo(entity.getCreatedAt(), entity.getUpdatedAt())
        );
    }

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "code", source = "code.code")
    @Mapping(target = "name", source = "name.name")
    @Mapping(target = "exchangeRateToBase", source = "exchangeRate.exchangeRate")
    CurrencyEntity toEntity(Currency domain);

    default void updateEntity(Currency domain, CurrencyEntity entity) {
        if (domain == null || entity == null) return;
        entity.setActive(domain.isActive());
    }
}
