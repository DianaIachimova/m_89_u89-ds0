package com.example.insurance_app.application.service.policy;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.policy.request.CancelPolicyRequest;
import com.example.insurance_app.application.dto.policy.request.CreatePolicyRequest;
import com.example.insurance_app.application.dto.policy.response.PolicyResponse;
import com.example.insurance_app.application.dto.policy.response.PolicySummaryResponse;
import com.example.insurance_app.application.exception.PolicyNotFoundException;
import com.example.insurance_app.application.mapper.PolicyDtoMapper;
import com.example.insurance_app.application.service.policy.pricing.AppliedAdjustment;
import com.example.insurance_app.application.service.policy.pricing.BuildingPricingContext;
import com.example.insurance_app.application.service.policy.pricing.PremiumCalculationResult;
import com.example.insurance_app.application.service.policy.pricing.PremiumCalculator;
import com.example.insurance_app.domain.model.broker.vo.BrokerId;
import com.example.insurance_app.domain.model.building.vo.BuildingId;
import com.example.insurance_app.domain.model.building.vo.RiskIndicators;
import com.example.insurance_app.domain.model.client.vo.ClientId;
import com.example.insurance_app.domain.model.metadata.currency.vo.CurrencyId;
import com.example.insurance_app.domain.model.policy.Policy;
import com.example.insurance_app.domain.model.policy.vo.*;
import com.example.insurance_app.domain.util.DomainAssertions;
import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.RiskIndicatorsEmbeddable;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyPricingSnapshotEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyPricingSnapshotItemEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.PolicyEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyPricingSnapshotRepository;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyRepository;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicySpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    private final PolicyRepository policyRepo;
    private final PolicyPricingSnapshotRepository snapshotRepo;
    private final PolicyReferenceRepositories refRepos;
    private final PremiumCalculator premiumCalculator;
    private final PolicyEntityMapper entityMapper;
    private final PolicyDtoMapper dtoMapper;
    private final PolicyNumberGenerator numberGenerator;

    public PolicyService(PolicyRepository policyRepo,
                         PolicyPricingSnapshotRepository snapshotRepo,
                         PolicyReferenceRepositories refRepos,
                         PremiumCalculator premiumCalculator,
                         PolicyEntityMapper entityMapper,
                         PolicyDtoMapper dtoMapper,
                         PolicyNumberGenerator numberGenerator) {
        this.policyRepo = policyRepo;
        this.snapshotRepo = snapshotRepo;
        this.refRepos = refRepos;
        this.premiumCalculator = premiumCalculator;
        this.entityMapper = entityMapper;
        this.dtoMapper = dtoMapper;
        this.numberGenerator = numberGenerator;
    }

    @Transactional
    public PolicyResponse createDraft(CreatePolicyRequest req) {
        logger.info("Creating draft policy for client={} building={}", req.clientId(), req.buildingId());

        ClientEntity client = refRepos.requireClient(req.clientId());
        BuildingEntity building = refRepos.requireBuilding(req.buildingId());
        BrokerEntity broker = refRepos.requireBroker(req.brokerId());
        CurrencyEntity currency = refRepos.requireCurrency(req.currencyId());

        DomainAssertions.check(
                "ACTIVE".equals(broker.getStatus()),
                "Broker is not active");
        DomainAssertions.check(
                building.getOwner().getId().equals(req.clientId()),
                "Building does not belong to the specified client");
        DomainAssertions.check(
                currency.isActive(),
                "Currency is not active");

        BuildingPricingContext ctx = buildPricingContext(building);
        PremiumCalculationResult calc = premiumCalculator.calculate(
                req.basePremium(), ctx, req.startDate());

        PolicyReferences refs = new PolicyReferences(
                new ClientId(req.clientId()),
                new BuildingId(req.buildingId()),
                new BrokerId(req.brokerId()),
                new CurrencyId(req.currencyId()));

        PolicyPremium premium = new PolicyPremium(
                new PremiumAmount(req.basePremium()),
                new PremiumAmount(calc.finalPremium()));

        Policy policy = Policy.createDraft(
                new PolicyNumber(numberGenerator.generate()),
                refs,
                new PolicyPeriod(req.startDate(), req.endDate()),
                premium);

        PolicyEntity entity = entityMapper.toEntity(policy, client, building, broker, currency);
        PolicyEntity saved = policyRepo.save(entity);
        Policy savedDomain = entityMapper.toDomain(saved);

        logger.info("Draft policy created id={} number={}", saved.getId(), saved.getPolicyNumber());
        return dtoMapper.toResponse(savedDomain, currency.getCode());
    }

    @Transactional
    public PolicyResponse activate(UUID policyId) {
        logger.info("Activating policy id={}", policyId);

        PolicyEntity entity = requirePolicy(policyId);
        Policy domain = entityMapper.toDomain(entity);

        BuildingPricingContext ctx = buildPricingContext(entity.getBuilding());
        PremiumCalculationResult calc = premiumCalculator.calculate(
                domain.getBasePremium().value(), ctx, domain.getPeriod().startDate());

        domain.activate(new PremiumAmount(calc.finalPremium()));

        entityMapper.updateEntity(domain, entity);
        policyRepo.save(entity);

        persistSnapshot(entity, domain.getBasePremium().value(),
                calc.finalPremium(), calc.appliedAdjustments());

        logger.info("Policy activated id={} finalPremium={}", policyId, calc.finalPremium());
        return dtoMapper.toResponse(entityMapper.toDomain(entity), entity.getCurrency().getCode());
    }

    @Transactional
    public PolicyResponse cancel(UUID policyId, CancelPolicyRequest req) {
        logger.info("Cancelling policy id={}", policyId);

        PolicyEntity entity = requirePolicy(policyId);
        Policy domain = entityMapper.toDomain(entity);

        domain.cancel(req.reason());

        entityMapper.updateEntity(domain, entity);
        policyRepo.save(entity);

        logger.info("Policy cancelled id={}", policyId);
        return dtoMapper.toResponse(entityMapper.toDomain(entity), entity.getCurrency().getCode());
    }

    @Transactional(readOnly = true)
    public PageDto<PolicySummaryResponse> list(UUID clientId,
                                                UUID brokerId,
                                                String status,
                                                LocalDate startFrom,
                                                LocalDate startTo,
                                                Pageable pageable) {
        logger.info("Listing policies clientId={} brokerId={} status={}", clientId, brokerId, status);

        var spec = PolicySpecifications.withFilters(clientId, brokerId, status, startFrom, startTo);
        var page = policyRepo.findAll(spec, pageable);

        List<PolicySummaryResponse> content = page.getContent().stream()
                .map(dtoMapper::toSummaryResponse)
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
    public PolicyResponse getById(UUID policyId) {
        logger.info("Fetching policy id={}", policyId);
        PolicyEntity entity = requirePolicy(policyId);
        Policy domain = entityMapper.toDomain(entity);
        return dtoMapper.toResponse(domain, entity.getCurrency().getCode());
    }

    private void persistSnapshot(PolicyEntity policy,
                                  BigDecimal basePremium,
                                  BigDecimal finalPremium,
                                  List<AppliedAdjustment> adjustments) {
        var snapshot = new PolicyPricingSnapshotEntity();
        snapshot.setPolicy(policy);
        snapshot.setBasePremium(basePremium);
        snapshot.setFinalPremium(finalPremium);
        snapshot.setSnapshotDate(Instant.now());

        BigDecimal feePct = BigDecimal.ZERO;
        BigDecimal riskPct = BigDecimal.ZERO;

        int order = 0;
        for (var adj : adjustments) {
            var item = new PolicyPricingSnapshotItemEntity();
            item.setSnapshot(snapshot);
            item.setSourceType(adj.sourceType());
            item.setSourceId(adj.sourceId());
            item.setName(adj.name());
            item.setPercentage(adj.percentage());
            item.setAppliedOrder(order++);
            snapshot.getItems().add(item);

            if (adj.sourceType().startsWith("FEE")) {
                feePct = feePct.add(adj.percentage());
            } else {
                riskPct = riskPct.add(adj.percentage());
            }
        }

        snapshot.setTotalFeePct(feePct);
        snapshot.setTotalRiskPct(riskPct);
        snapshotRepo.save(snapshot);
    }

    private BuildingPricingContext buildPricingContext(BuildingEntity building) {
        CityEntity city = building.getCity();
        var county = city.getCounty();
        var country = county.getCountry();
        RiskIndicatorsEmbeddable risk = building.getRisk();

        return new BuildingPricingContext(
                country.getId(),
                county.getId(),
                city.getId(),
                building.getBuildingInfo().getBuildingType(),
                risk != null
                        ? new RiskIndicators(risk.isFloodZone(), risk.isEarthquakeRiskZone())
                        : null
        );
    }

    private PolicyEntity requirePolicy(UUID id) {
        return policyRepo.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(id));
    }
}
