package com.example.insurance_app.application.service.broker;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.broker.request.CreateBrokerRequest;
import com.example.insurance_app.application.dto.broker.request.UpdateBrokerRequest;
import com.example.insurance_app.application.dto.broker.response.BrokerResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.BrokerDtoMapper;
import com.example.insurance_app.domain.model.broker.Broker;
import com.example.insurance_app.domain.model.broker.vo.BrokerCode;
import com.example.insurance_app.domain.model.broker.vo.BrokerName;
import com.example.insurance_app.domain.model.broker.vo.CommissionPercentage;
import com.example.insurance_app.domain.model.broker.vo.ContactInfo;
import com.example.insurance_app.domain.model.client.vo.EmailAddress;
import com.example.insurance_app.domain.model.client.vo.PhoneNumber;
import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.BrokerEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.broker.BrokerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BrokerService {

    private static final Logger logger = LoggerFactory.getLogger(BrokerService.class);
    private static final String BROKER = "Broker";

    private final BrokerRepository brokerRepository;
    private final BrokerDtoMapper brokerDtoMapper;
    private final BrokerEntityMapper brokerEntityMapper;

    public BrokerService(BrokerRepository brokerRepository,
                         BrokerDtoMapper brokerDtoMapper,
                         BrokerEntityMapper brokerEntityMapper) {
        this.brokerRepository = brokerRepository;
        this.brokerDtoMapper = brokerDtoMapper;
        this.brokerEntityMapper = brokerEntityMapper;
    }

    @Transactional(readOnly = true)
    public PageDto<BrokerResponse> list(Pageable pageable) {
        logger.info("Fetching brokers page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        var page = brokerRepository.findAll(pageable);

        List<BrokerResponse> content = page.getContent().stream()
                .map(brokerEntityMapper::toDomain)
                .map(brokerDtoMapper::toResponse)
                .toList();

        return new PageDto<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public BrokerResponse getById(UUID id) {
        logger.info("Fetching broker id={}", id);
        BrokerEntity entity = findEntityById(id);
        Broker domain = brokerEntityMapper.toDomain(entity);
        return brokerDtoMapper.toResponse(domain);
    }

    @Transactional
    public BrokerResponse create(CreateBrokerRequest request) {
        logger.info("Creating broker with code={}", request.brokerCode());

        String normalizedCode = new BrokerCode(request.brokerCode()).value();
        String normalizedEmail = new EmailAddress(request.email()).value();

        if (brokerRepository.existsByBrokerCodeIgnoreCase(normalizedCode)) {
            throw new DuplicateResourceException(BROKER, "brokerCode", normalizedCode);
        }
        if (brokerRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException(BROKER, "email", normalizedEmail);
        }

        Broker domain = brokerDtoMapper.toDomain(request);
        BrokerEntity entity = brokerEntityMapper.toEntity(domain);
        BrokerEntity saved = brokerRepository.save(entity);
        Broker savedDomain = brokerEntityMapper.toDomain(saved);

        logger.info("Broker created id={}", saved.getId());
        return brokerDtoMapper.toResponse(savedDomain);
    }

    @Transactional
    public BrokerResponse update(UUID id, UpdateBrokerRequest request) {
        logger.info("Updating broker id={}", id);
        BrokerEntity entity = findEntityById(id);

        String normalizedEmail = new EmailAddress(request.email()).value();
        if (brokerRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id)) {
            throw new DuplicateResourceException(BROKER, "email", normalizedEmail);
        }

        Broker domain = brokerEntityMapper.toDomain(entity);
        ContactInfo contactInfo = new ContactInfo(
                new EmailAddress(request.email()),
                request.phone() != null ? new PhoneNumber(request.phone()) : null
        );

        domain.updateDetails(
                new BrokerName(request.name()),
                contactInfo,
                request.commissionPercentage() != null
                        ? new CommissionPercentage(request.commissionPercentage())
                        : null
        );

        brokerEntityMapper.updateEntity(domain, entity);
        BrokerEntity saved = brokerRepository.save(entity);
        Broker updated = brokerEntityMapper.toDomain(saved);

        logger.info("Broker updated id={}", id);
        return brokerDtoMapper.toResponse(updated);
    }

    @Transactional
    public BrokerResponse activate(UUID id) {
        logger.info("Activating broker id={}", id);
        return changeStatus(id, true);
    }

    @Transactional
    public BrokerResponse deactivate(UUID id) {
        logger.info("Deactivating broker id={}", id);
        return changeStatus(id, false);
    }

    private BrokerResponse changeStatus(UUID id, boolean activate) {
        BrokerEntity entity = findEntityById(id);
        Broker domain = brokerEntityMapper.toDomain(entity);

        if (activate) {
            domain.activate();
        } else {
            domain.deactivate();
        }

        brokerEntityMapper.updateEntity(domain, entity);
        BrokerEntity saved = brokerRepository.save(entity);
        Broker updated = brokerEntityMapper.toDomain(saved);

        logger.info("Broker status changed id={} status={}", id, updated.getStatus());
        return brokerDtoMapper.toResponse(updated);
    }

    private BrokerEntity findEntityById(UUID id) {
        return brokerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(BROKER, "id", id));
    }
}
