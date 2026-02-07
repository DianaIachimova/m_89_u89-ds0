package com.example.insurance_app.application.mapper;

import com.example.insurance_app.application.dto.metadata.currency.request.CreateCurrencyRequest;
import com.example.insurance_app.application.dto.metadata.currency.request.UpdateCurrencyStatusRequest;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;
import com.example.insurance_app.domain.model.metadata.currency.Currency;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyCode;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyName;
import com.example.insurance_app.domain.model.metadata.currency.vo.ExchangeRateToBase;
import org.springframework.stereotype.Component;

@Component
public class CurrencyDtoMapper {
    public Currency toDomain(CreateCurrencyRequest request) {
        if (request == null)
            return null;

        return Currency.createNew(
                new CurrencyCode(request.code()),
                new CurrencyName(request.name()),
                new ExchangeRateToBase(request.exchangeRateToBase()),
                request.isActive()
        );

    }

    public CurrencyResponse toResponse(Currency domain) {
        if (domain == null)
            return null;
        return new CurrencyResponse(
                domain.getId().value(),
                domain.getCode().code(),
                domain.getName().name(),
                domain.getExchangeRate().exchangeRate(),
                domain.isActive(),
                domain.getAudit().createdAt(),
                domain.getAudit().updatedAt()
        );
    }

    public void applyActivation(UpdateCurrencyStatusRequest request, Currency domain) {
        if (request == null || domain == null)
            return;

        if (Boolean.TRUE.equals(request.isActive()))
            domain.activate();
        else domain.deactivate();
    }

}
