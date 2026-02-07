package com.example.insurance_app.application.service.metadata;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.metadata.currency.request.CreateCurrencyRequest;
import com.example.insurance_app.application.dto.metadata.currency.request.UpdateCurrencyStatusRequest;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.CurrencyDtoMapper;
import com.example.insurance_app.domain.model.metadata.currency.Currency;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyCode;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.CurrencyEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CurrencyService {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyRepository currencyRepository;
    private final CurrencyDtoMapper currencyDtoMapper;
    private final CurrencyEntityMapper currencyEntityMapper;

    public CurrencyService(CurrencyRepository currencyRepository, CurrencyDtoMapper currencyDtoMapper, CurrencyEntityMapper currencyEntityMapper) {
        this.currencyRepository = currencyRepository;
        this.currencyDtoMapper = currencyDtoMapper;
        this.currencyEntityMapper = currencyEntityMapper;
    }

    @Transactional(readOnly = true)
    public PageDto<CurrencyResponse> getAllCurrencies(Pageable pageable) {
        logger.info("Fetching all currencies");
        var page= currencyRepository.findAllByOrderByCodeAsc(pageable);

        List<CurrencyResponse> content = page.getContent().stream()
                .map(currencyEntityMapper::toDomain)
                .map(currencyDtoMapper::toResponse)
                .toList();

        logger.info("Found {} currencies", page.getTotalElements());
        return new PageDto<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }


    public CurrencyResponse createCurrency(CreateCurrencyRequest request) {
        logger.info("Creating currency with code: {}", request.code());
        var normalizedCode = new CurrencyCode(request.code()).code();

        if (currencyRepository.existsByCode(normalizedCode)) {
            throw new DuplicateResourceException("Currency", "code" , normalizedCode);
        }

        Currency currency = currencyDtoMapper.toDomain(request);
        CurrencyEntity entity = currencyEntityMapper.toEntity(currency);
        CurrencyEntity savedEntity = currencyRepository.save(entity);
        Currency savedCurrency = currencyEntityMapper.toDomain(savedEntity);

        logger.info("Currency created successfully with id: {}", savedEntity.getId());
        return currencyDtoMapper.toResponse(savedCurrency);
    }

    public CurrencyResponse updateActiveStatus(UUID currencyId, UpdateCurrencyStatusRequest request) {
        logger.info("Setting currency active status with id: {}, isActive: {}", currencyId, request.isActive());

        CurrencyEntity existingEntity = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", currencyId));

        Currency existingCurrency = currencyEntityMapper.toDomain(existingEntity);
        currencyDtoMapper.applyActivation(request, existingCurrency);

        //update db
        currencyEntityMapper.updateEntity(existingCurrency, existingEntity);
        CurrencyEntity updatedEntity = currencyRepository.save(existingEntity);

        //get response
        Currency updatedCurrency = currencyEntityMapper.toDomain(updatedEntity);

        logger.info("Currency active status updated successfully with id: {}", currencyId);
        return currencyDtoMapper.toResponse(updatedCurrency);
    }


}
