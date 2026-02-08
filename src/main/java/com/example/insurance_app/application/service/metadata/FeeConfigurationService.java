package com.example.insurance_app.application.service.metadata;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.metadata.feeconfig.request.CreateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.response.FeeConfigResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.mapper.FeeConfigDtoMapper;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfiguration;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.FeeConfigEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.FeeConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;


@Service
public class FeeConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(FeeConfigurationService.class);

    private final FeeConfigRepository feeRepository;
    private final FeeConfigEntityMapper feeEntityMapper;
    private final FeeConfigDtoMapper feeDtoMapper;

    public FeeConfigurationService(FeeConfigRepository feeRepository,
                                   FeeConfigEntityMapper feeEntityMapper,
                                   FeeConfigDtoMapper feeDtoMapper) {
        this.feeRepository = feeRepository;
        this.feeEntityMapper = feeEntityMapper;
        this.feeDtoMapper = feeDtoMapper;
    }

    @Transactional(readOnly = true)
    public PageDto<FeeConfigResponse> listFeeConfigurations(Pageable pageable) {
        logger.info("Listing all fee configurations");

        var page = feeRepository.findAllByOrderByTypeAsc(pageable);

        var content = page.getContent().stream()
                .map(feeEntityMapper::toDomain)
                .map(feeDtoMapper::toResponse)
                .toList();

        logger.info("Found {} fee configurations", page.getTotalElements());

        return new PageDto<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public FeeConfigResponse create(CreateFeeConfigRequest req){
        logger.info("Creating a new fee configuration");
        var feeConfiguration  = feeDtoMapper.toDomain(req);

        ensureNoOverlap(
                feeConfiguration.getDetails().type().name(),
                feeConfiguration.getDetails().code().value(),
                feeConfiguration.getDetails().period().from(),
                feeConfiguration.getDetails().period().to()
                );

        FeeConfigurationEntity entity = feeEntityMapper.toEntity(feeConfiguration);
        FeeConfigurationEntity savedEntity = feeRepository.save(entity);
        FeeConfiguration saved = feeEntityMapper.toDomain(savedEntity);

        logger.info("Fee configuration created with id: {}", savedEntity.getId());
        return feeDtoMapper.toResponse(saved);

    }

    private void ensureNoOverlap(String type, String code, LocalDate from, LocalDate to) {
        boolean overlap = feeRepository.existsActiveOverlapNative(type,code, from, to);
        if (overlap)  throw new DuplicateResourceException("Fee Configuration", "type", type);
    }

}
