package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.metadata.currency.request.CreateCurrencyRequest;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyRefResponse;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;
import com.example.insurance_app.domain.model.metadata.currency.Currency;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyCode;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyName;
import com.example.insurance_app.domain.model.metadata.currency.vo.ExchangeRateToBase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface CurrencyDtoMapper {

    default Currency toDomain(CreateCurrencyRequest request) {
        if (request == null) return null;
        return Currency.createNew(
                new CurrencyCode(request.code()),
                new CurrencyName(request.name()),
                new ExchangeRateToBase(request.exchangeRateToBase()),
                Boolean.TRUE.equals(request.isActive())
        );
    }

    @Mapping(target = "id", expression = "java(domain.getId() != null ? domain.getId().value() : null)")
    @Mapping(target = "code", source = "code.code")
    @Mapping(target = "name", source = "name.name")
    @Mapping(target = "exchangeRateToBase", source = "exchangeRate.exchangeRate")
    @Mapping(target = "createdAt", expression = "java(domain.getAudit() != null ? domain.getAudit().createdAt() : null)")
    @Mapping(target = "updatedAt", expression = "java(domain.getAudit() != null ? domain.getAudit().updatedAt() : null)")
    CurrencyResponse toResponse(Currency domain);

    @Mapping(target = "id", expression = "java(domain.getId() != null ? domain.getId().value() : null)")
    @Mapping(target = "code", source = "code.code")
    @Mapping(target = "name", source = "name.name")
    @Mapping(target = "exchangeRateToBase", source = "exchangeRate.exchangeRate")
    CurrencyRefResponse toRefResponse(Currency domain);
}
