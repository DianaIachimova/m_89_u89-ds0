package com.example.insurance_app.application.service.metadata;

import com.example.insurance_app.application.dto.metadata.feeconfig.request.UpdateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.response.FeeConfigResponse;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.FeeConfigDtoMapper;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfiguration;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeDetails;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.EffectivePeriod;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeName;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeePercentage;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.FeeConfigEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.FeeConfigRepository;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyPricingSnapshotItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FeeConfigurationUpdateService {
    private static final Logger logger = LoggerFactory.getLogger(FeeConfigurationUpdateService.class);

    private final FeeConfigRepository feeRepository;
    private final FeeConfigEntityMapper feeEntityMapper;
    private final FeeConfigDtoMapper feeDtoMapper;
    private final PolicyPricingSnapshotItemRepository snapshotItemRepo;

    public FeeConfigurationUpdateService(FeeConfigRepository feeRepository,
                                         FeeConfigEntityMapper feeEntityMapper,
                                         FeeConfigDtoMapper feeDtoMapper,
                                         PolicyPricingSnapshotItemRepository snapshotItemRepo) {
        this.feeRepository = feeRepository;
        this.feeEntityMapper = feeEntityMapper;
        this.feeDtoMapper = feeDtoMapper;
        this.snapshotItemRepo = snapshotItemRepo;
    }

    @Transactional
    public FeeConfigResponse update(UUID id, UpdateFeeConfigRequest req) {
        FeeConfigurationEntity entity = requireFeeConfig(id);
        validateRequiredUpdateFields(req);

        UUID entityId = entity.getId();
        logger.info("Updating fee configuration with id: {}", entityId);

        FeeConfiguration current = feeEntityMapper.toDomain(entity);

        String newName = req.name() != null ? req.name() : current.getDetails().name().value();
        BigDecimal newPercentage = req.percentage() != null ? req.percentage() : current.getDetails().percentage().value();
        LocalDate newTo = req.effectiveTo() != null ? req.effectiveTo() : current.getDetails().period().to();

        boolean usedBySnapshot = snapshotItemRepo.existsBySourceTypeAndSourceId(entity.getId());
        if (usedBySnapshot) {
            return updateByCreatingNewVersion(entity, current, newName, newPercentage, newTo);
        }
        return updateInPlace(entity, current, newName, newPercentage, newTo);
    }

    @Transactional
    public FeeConfigResponse deactivate(UUID id) {
        FeeConfigurationEntity entity = requireFeeConfig(id);
        checkIfFeeConfigIsUsed(id);

        logger.info("Deactivating fee configuration with id: {}", entity.getId());

        FeeConfiguration domain = feeEntityMapper.toDomain(entity);
        domain.deactivate();
        feeEntityMapper.updateEntity(domain, entity);
        FeeConfigurationEntity saved = feeRepository.save(entity);
        FeeConfiguration updated = feeEntityMapper.toDomain(saved);

        logger.info("Fee configuration deactivated with id: {}", updated.getId());
        return feeDtoMapper.toResponse(updated);
    }

    private FeeConfigurationEntity requireFeeConfig(UUID id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeConfiguration", "id", id));
    }

    private FeeConfigResponse updateInPlace(FeeConfigurationEntity entity, FeeConfiguration current,
                                            String newName, BigDecimal newPercentage, LocalDate newTo) {
        UUID entityId = entity.getId();
        current.updateDetails(
                new FeeName(newName),
                new FeePercentage(newPercentage),
                newTo
        );

        feeEntityMapper.updateEntity(current, entity);
        FeeConfigurationEntity saved = feeRepository.save(entity);
        FeeConfiguration updated = feeEntityMapper.toDomain(saved);
        logger.info("Fee configuration updated in place with id: {}", entityId);
        return feeDtoMapper.toResponse(updated);
    }

    private FeeConfigResponse updateByCreatingNewVersion(FeeConfigurationEntity existingEntity,
                                                         FeeConfiguration current, String newName,
                                                         BigDecimal newPercentage, LocalDate newTo) {
        UUID existingId = existingEntity.getId();
        current.deactivate();
        feeEntityMapper.updateEntity(current, existingEntity);
        feeRepository.save(existingEntity);
        logger.info("Closed fee configuration with id: {} (used by active policies)", existingId);

        FeeDetails newDetails = new FeeDetails(
                current.getDetails().code(),
                new FeeName(newName),
                current.getDetails().type(),
                new FeePercentage(newPercentage),
                new EffectivePeriod(current.getDetails().period().from(), newTo)
        );
        FeeConfiguration newVersion = FeeConfiguration.createNew(newDetails, true);
        FeeConfigurationEntity newEntity = feeEntityMapper.toEntity(newVersion);
        FeeConfigurationEntity saved = feeRepository.save(newEntity);
        FeeConfiguration updated = feeEntityMapper.toDomain(saved);
        logger.info("Created new fee configuration version with id: {}", saved.getId());
        return feeDtoMapper.toResponse(updated);
    }

    private void validateRequiredUpdateFields(UpdateFeeConfigRequest request) {
        if (request.name() == null && request.effectiveTo() == null && request.percentage() == null) {
            throw new IllegalArgumentException("At least one of name, percentage, or effectiveTo is required");
        }
    }

    private void checkIfFeeConfigIsUsed(UUID id){
        if (snapshotItemRepo.existsFeeConfigReferencedInSnapshots(id, PolicyStatusEntity.ACTIVE)) {
            throw new DomainValidationException(
                    "Cannot deactivate fee configuration still referenced by policy pricing snapshots");
        }
    }
}
