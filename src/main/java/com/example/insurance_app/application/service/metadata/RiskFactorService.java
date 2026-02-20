package com.example.insurance_app.application.service.metadata;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskFactorAction;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.CreateRiskFactorRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.RiskFactorActionRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.UpdateRiskFactorPercentageRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.response.RiskFactorResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.RiskFactorDtoMapper;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskFactorConfiguration;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.AdjustmentPercentage;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskTarget;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskFactorConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.RiskFactorEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CityRepository;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CountryRepository;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CountyRepository;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.RiskFactorConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static com.example.insurance_app.infrastructure.persistence.mapper.EnumEntityMapper.toBuildingTypeEntity;
import static com.example.insurance_app.infrastructure.persistence.mapper.EnumEntityMapper.toRiskLevelEntity;

@Service
public class RiskFactorService {
    private static final Logger logger = LoggerFactory.getLogger(RiskFactorService.class);

    private final RiskFactorConfigRepository riskFactorRepository;
    private final RiskFactorEntityMapper entityMapper;
    private final RiskFactorDtoMapper dtoMapper;
    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final CityRepository cityRepository;

    public RiskFactorService(RiskFactorConfigRepository riskFactorRepository,
                             RiskFactorEntityMapper entityMapper,
                             RiskFactorDtoMapper dtoMapper,
                             CountryRepository countryRepository,
                             CountyRepository countyRepository,
                             CityRepository cityRepository) {
        this.riskFactorRepository = riskFactorRepository;
        this.entityMapper = entityMapper;
        this.dtoMapper = dtoMapper;
        this.countryRepository = countryRepository;
        this.countyRepository = countyRepository;
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public PageDto<RiskFactorResponse> listRiskFactors(Pageable pageable) {
        logger.info("Listing risk factor configurations");

        var page = riskFactorRepository.findAllBy(pageable);

        var content = page.getContent().stream()
                .map(entityMapper::toDomain)
                .map(dtoMapper::toResponse)
                .toList();

        logger.info("Found {} risk factor configurations", page.getTotalElements());

        return new PageDto<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public RiskFactorResponse create(CreateRiskFactorRequest req) {
        logger.info("Creating risk factor configuration level={}", req.level());

        RiskFactorConfiguration domain = dtoMapper.toDomain(req);

        validateGeographyReference(domain.getTarget());
        if (domain.isActive()) {
            ensureNoActiveConflict(domain.getTarget());
        }

        RiskFactorConfigurationEntity entity = entityMapper.toEntity(domain);
        RiskFactorConfigurationEntity saved = riskFactorRepository.save(entity);
        RiskFactorConfiguration result = entityMapper.toDomain(saved);

        logger.info("Risk factor configuration created with id={}", saved.getId());
        return dtoMapper.toResponse(result);
    }

    @Transactional
    public RiskFactorResponse updatePercentage(UUID id, UpdateRiskFactorPercentageRequest req) {
        logger.info("Updating risk factor percentage id={}", id);

        RiskFactorConfigurationEntity entity = requireRiskFactor(id);
        RiskFactorConfiguration domain = entityMapper.toDomain(entity);
        domain.updatePercentage(new AdjustmentPercentage(req.adjustmentPercentage()));

        entityMapper.updateEntity(domain, entity);
        RiskFactorConfigurationEntity saved = riskFactorRepository.save(entity);
        RiskFactorConfiguration result = entityMapper.toDomain(saved);

        logger.info("Risk factor configuration updated id={}", id);
        return dtoMapper.toResponse(result);
    }


    @Transactional
    public RiskFactorResponse executeAction(UUID id, RiskFactorActionRequest req) {
        logger.info("Executing action={} on risk factor id={}", req.action(), id);

        RiskFactorConfigurationEntity entity = requireRiskFactor(id);
        RiskFactorConfiguration domain = entityMapper.toDomain(entity);

        if (Objects.requireNonNull(req.action()) == RiskFactorAction.ACTIVATE) {
            ensureNoActiveConflict(domain.getTarget());
            domain.activate();
        } else if (req.action() == RiskFactorAction.DEACTIVATE) {
            domain.deactivate();
        }

        entityMapper.updateEntity(domain, entity);
        RiskFactorConfigurationEntity saved = riskFactorRepository.save(entity);
        RiskFactorConfiguration result = entityMapper.toDomain(saved);

        logger.info("Risk factor action={} completed for id={}", req.action(), id);
        return dtoMapper.toResponse(result);
    }


    private RiskFactorConfigurationEntity requireRiskFactor(UUID id) {
        return riskFactorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RiskFactorConfiguration", "id", id));
    }


    private void ensureNoActiveConflict(RiskTarget target) {
        boolean conflict;
        if (target.level() == RiskLevel.BUILDING_TYPE) {
            conflict = riskFactorRepository.existsByLevelAndBuildingTypeAndActiveTrue(
                    toRiskLevelEntity(target.level()),
                    toBuildingTypeEntity(target.buildingType())
            );
        }
        else {
            conflict = riskFactorRepository.existsByLevelAndReferenceIdAndActiveTrue(
                    toRiskLevelEntity(target.level()),
                    target.referenceId()
            );
        }

        if (conflict) throw new DuplicateResourceException("RiskFactorConfiguration", "target",
                    target.level() + ":" + (target.referenceId() != null ? target.referenceId() : target.buildingType()));

    }

    private void validateGeographyReference(RiskTarget target) {
        if (target.level() == RiskLevel.BUILDING_TYPE) {
            return;
        }

        UUID refId = target.referenceId();
        boolean exists = switch (target.level()) {
            case COUNTRY -> countryRepository.existsById(refId);
            case COUNTY -> countyRepository.existsById(refId);
            case CITY -> cityRepository.existsById(refId);
            default -> false;
        };

        if (!exists) throw new ResourceNotFoundException(target.level().name(), "id", refId);
    }
}



