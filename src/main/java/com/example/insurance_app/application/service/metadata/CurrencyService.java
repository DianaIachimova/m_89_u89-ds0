package com.example.insurance_app.application.service.metadata;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.metadata.currency.CurrencyAction;
import com.example.insurance_app.application.dto.metadata.currency.request.CreateCurrencyRequest;
import com.example.insurance_app.application.dto.metadata.currency.request.CurrencyActionRequest;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.CurrencyDtoMapper;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.metadata.currency.Currency;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyCode;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.CurrencyEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.CurrencyRepository;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyRepository;
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
    private final PolicyRepository policyRepository;

    public CurrencyService(CurrencyRepository currencyRepository,
                           CurrencyDtoMapper currencyDtoMapper,
                           CurrencyEntityMapper currencyEntityMapper,
                           PolicyRepository policyRepository) {
        this.currencyRepository = currencyRepository;
        this.currencyDtoMapper = currencyDtoMapper;
        this.currencyEntityMapper = currencyEntityMapper;
        this.policyRepository = policyRepository;
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

    @Transactional
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

    @Transactional
    public CurrencyResponse executeAction(UUID currencyId, CurrencyActionRequest request) {
        logger.info("Executing action={} on currency id={}", request.action(), currencyId);

        CurrencyEntity entity = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", currencyId));

        Currency domain = currencyEntityMapper.toDomain(entity);

        switch(request.action()) {
            case ACTIVATE ->  domain.activate();
            case DEACTIVATE ->  {
                boolean usedByActive = policyRepository.existsByStatusAndCurrencyId(
                        PolicyStatusEntity.ACTIVE, currencyId);
                if (usedByActive) {
                    throw new DomainValidationException(
                            "Cannot deactivate currency used by active policies");
                }
                domain.deactivate();
            }
        }

        currencyEntityMapper.updateEntity(domain, entity);
        CurrencyEntity saved = currencyRepository.saveAndFlush(entity);
        Currency updated = currencyEntityMapper.toDomain(saved);

        logger.info("Currency action={} completed for id={}", request.action(), currencyId);
        return currencyDtoMapper.toResponse(updated);
    }


}
