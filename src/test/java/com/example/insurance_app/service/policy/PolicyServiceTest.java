package com.example.insurance_app.service.policy;

import com.example.insurance_app.application.dto.policy.request.CancelPolicyRequest;
import com.example.insurance_app.application.dto.policy.request.CreatePolicyRequest;
import com.example.insurance_app.application.dto.policy.response.PolicyDetailResponse;
import com.example.insurance_app.application.dto.policy.response.PolicyResponse;
import com.example.insurance_app.application.exception.PolicyNotFoundException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.PolicyDtoMapper;
import com.example.insurance_app.application.service.policy.PolicyNumberGenerator;
import com.example.insurance_app.application.service.policy.PolicyReferenceRepositories;
import com.example.insurance_app.application.service.policy.PolicyService;
import com.example.insurance_app.application.service.policy.PolicySupportDeps;
import com.example.insurance_app.application.service.policy.pricing.PremiumCalculationResult;
import com.example.insurance_app.application.service.policy.pricing.PremiumCalculator;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.building.Building;
import com.example.insurance_app.domain.model.client.Client;
import com.example.insurance_app.domain.model.policy.Policy;
import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingInfoEmbeddable;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountryEntity;
import com.example.insurance_app.infrastructure.persistence.entity.geography.CountyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.BuildingEntityMapper;
import com.example.insurance_app.infrastructure.persistence.mapper.ClientEntityMapper;
import com.example.insurance_app.infrastructure.persistence.mapper.PolicyEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyPricingSnapshotRepository;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("PolicyService Unit Tests")
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepo;
    @Mock
    private PolicyPricingSnapshotRepository snapshotRepo;
    @Mock
    private PolicyReferenceRepositories refRepos;
    @Mock
    private PremiumCalculator premiumCalculator;
    @Mock
    private PolicyEntityMapper entityMapper;
    @Mock
    private PolicyDtoMapper dtoMapper;
    @Mock
    private PolicyNumberGenerator numberGenerator;
    @Mock
    private ClientEntityMapper clientEntityMapper;
    @Mock
    private BuildingEntityMapper buildingEntityMapper;
    @Mock
    private PolicySupportDeps support;

    @InjectMocks
    private PolicyService policyService;

    private UUID clientId;
    private UUID buildingId;
    private UUID brokerId;
    private UUID currencyId;
    private UUID policyId;
    private CreatePolicyRequest createRequest;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        buildingId = UUID.randomUUID();
        brokerId = UUID.randomUUID();
        currencyId = UUID.randomUUID();
        policyId = UUID.randomUUID();

        lenient().when(support.clientEntityMapper()).thenReturn(clientEntityMapper);
        lenient().when(support.buildingEntityMapper()).thenReturn(buildingEntityMapper);
        lenient().when(support.numberGenerator()).thenReturn(numberGenerator);

        createRequest = new CreatePolicyRequest(
                clientId, buildingId, brokerId, currencyId,
                new BigDecimal("1000.00"),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusYears(1)
        );
    }

    private ClientEntity mockClient() {
        ClientEntity client = mock(ClientEntity.class);
        when(refRepos.requireClient(clientId)).thenReturn(client);
        return client;
    }

    private BuildingEntity mockBuilding() {
        BuildingEntity building = mock(BuildingEntity.class);
        ClientEntity owner = mock(ClientEntity.class);
        lenient().when(owner.getId()).thenReturn(clientId);
        lenient().when(building.getOwner()).thenReturn(owner);

        CityEntity city = mock(CityEntity.class);
        CountyEntity county = mock(CountyEntity.class);
        CountryEntity country = mock(CountryEntity.class);
        lenient().when(city.getCounty()).thenReturn(county);
        lenient().when(county.getCountry()).thenReturn(country);
        lenient().when(building.getCity()).thenReturn(city);

        BuildingInfoEmbeddable info = mock(BuildingInfoEmbeddable.class);
        lenient().when(info.getBuildingType()).thenReturn(BuildingTypeEntity.RESIDENTIAL);
        lenient().when(building.getBuildingInfo()).thenReturn(info);
        lenient().when(building.getRisk()).thenReturn(null);

        when(refRepos.requireBuilding(buildingId)).thenReturn(building);
        return building;
    }

    private BrokerEntity mockActiveBroker() {
        BrokerEntity broker = mock(BrokerEntity.class);
        when(broker.getStatus()).thenReturn("ACTIVE");
        when(refRepos.requireBroker(brokerId)).thenReturn(broker);
        return broker;
    }

    private CurrencyEntity mockActiveCurrency() {
        CurrencyEntity currency = mock(CurrencyEntity.class);
        when(currency.isActive()).thenReturn(true);
        when(currency.getCode()).thenReturn("RON");
        when(refRepos.requireCurrency(currencyId)).thenReturn(currency);
        return currency;
    }

    private void mockCalculation() {
        when(premiumCalculator.calculate(any(), any(), any()))
                .thenReturn(new PremiumCalculationResult(new BigDecimal("1100.00"), List.of()));
    }

    @Nested
    @DisplayName("createDraft")
    class CreateDraftTests {

        @Test
        @DisplayName("Happy path: should create draft policy")
        void happyPath() {
            mockClient();
            mockBuilding();
            mockActiveBroker();
            mockActiveCurrency();
            mockCalculation();

            when(numberGenerator.generate()).thenReturn("POL-2026-ABCD1234");

            PolicyEntity entity = mock(PolicyEntity.class);
            Policy domain = mock(Policy.class);
            PolicyResponse response = mock(PolicyResponse.class);

            when(entityMapper.toEntity(any(Policy.class), any(), any(), any(), any())).thenReturn(entity);
            when(policyRepo.save(entity)).thenReturn(entity);
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponse(domain, "RON")).thenReturn(response);

            PolicyResponse result = policyService.createDraft(createRequest);

            assertNotNull(result);
            verify(policyRepo).save(any());
        }

        @Test
        @DisplayName("Should throw when broker is not active")
        void inactiveBroker() {
            mockClient();
            mockBuilding();
            BrokerEntity broker = mock(BrokerEntity.class);
            when(broker.getStatus()).thenReturn("INACTIVE");
            when(refRepos.requireBroker(brokerId)).thenReturn(broker);

            assertThrows(DomainValidationException.class, () -> policyService.createDraft(createRequest));
            verify(policyRepo, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when building does not belong to client")
        void buildingNotOwnedByClient() {
            mockClient();
            BuildingEntity building = mock(BuildingEntity.class);
            ClientEntity otherOwner = mock(ClientEntity.class);
            when(otherOwner.getId()).thenReturn(UUID.randomUUID());
            when(building.getOwner()).thenReturn(otherOwner);
            when(refRepos.requireBuilding(buildingId)).thenReturn(building);
            mockActiveBroker();

            assertThrows(DomainValidationException.class, () -> policyService.createDraft(createRequest));
            verify(policyRepo, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when currency is not active")
        void inactiveCurrency() {
            mockClient();
            mockBuilding();
            mockActiveBroker();
            CurrencyEntity currency = mock(CurrencyEntity.class);
            when(currency.isActive()).thenReturn(false);
            when(refRepos.requireCurrency(currencyId)).thenReturn(currency);

            assertThrows(DomainValidationException.class, () -> policyService.createDraft(createRequest));
            verify(policyRepo, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when client does not exist")
        void clientNotFound() {
            when(refRepos.requireClient(clientId)).thenThrow(new ResourceNotFoundException("Client", "id", clientId));

            assertThrows(ResourceNotFoundException.class, () -> policyService.createDraft(createRequest));
        }

        @Test
        @DisplayName("Should throw when building does not exist")
        void buildingNotFound() {
            mockClient();
            when(refRepos.requireBuilding(buildingId)).thenThrow(new ResourceNotFoundException("Building", "id", buildingId));

            assertThrows(ResourceNotFoundException.class, () -> policyService.createDraft(createRequest));
        }

        @Test
        @DisplayName("Should throw when broker does not exist")
        void brokerNotFound() {
            mockClient();
            mockBuilding();
            when(refRepos.requireBroker(brokerId)).thenThrow(new ResourceNotFoundException("Broker", "id", brokerId));

            assertThrows(ResourceNotFoundException.class, () -> policyService.createDraft(createRequest));
        }
    }

    @Nested
    @DisplayName("activate")
    class ActivateTests {

        @Test
        @DisplayName("Happy path: should activate policy and persist snapshot")
        void happyPath() {
            PolicyEntity entity = mock(PolicyEntity.class);
            BuildingEntity building = mock(BuildingEntity.class);
            CityEntity city = mock(CityEntity.class);
            CountyEntity county = mock(CountyEntity.class);
            CountryEntity country = mock(CountryEntity.class);
            CurrencyEntity currency = mock(CurrencyEntity.class);
            BuildingInfoEmbeddable buildingInfo = mock(BuildingInfoEmbeddable.class);

            BrokerEntity broker = mock(BrokerEntity.class);
            when(broker.getCommissionPercentage()).thenReturn(null);

            when(policyRepo.findById(policyId)).thenReturn(Optional.of(entity));
            when(entity.getBuilding()).thenReturn(building);
            when(entity.getBroker()).thenReturn(broker);
            when(entity.getCurrency()).thenReturn(currency);
            when(currency.getCode()).thenReturn("RON");
            when(building.getCity()).thenReturn(city);
            when(city.getCounty()).thenReturn(county);
            when(county.getCountry()).thenReturn(country);
            when(building.getBuildingInfo()).thenReturn(buildingInfo);
            when(buildingInfo.getBuildingType()).thenReturn(BuildingTypeEntity.RESIDENTIAL);
            when(building.getRisk()).thenReturn(null);

            Policy domain = mock(Policy.class, RETURNS_DEEP_STUBS);
            when(domain.getBasePremium().value()).thenReturn(new BigDecimal("1000.00"));
            when(domain.getPeriod().startDate()).thenReturn(LocalDate.now().plusDays(1));
            when(entityMapper.toDomain(entity)).thenReturn(domain);

            when(premiumCalculator.calculate(any(), any(), any()))
                    .thenReturn(new PremiumCalculationResult(new BigDecimal("1100.00"), List.of()));

            PolicyResponse response = mock(PolicyResponse.class);
            when(dtoMapper.toResponse(any(), eq("RON"))).thenReturn(response);

            PolicyResponse result = policyService.activate(policyId);

            assertNotNull(result);
            verify(domain).activate(any());
            verify(policyRepo).save(entity);
            verify(snapshotRepo).save(any());
        }

        @Test
        @DisplayName("Should throw PolicyNotFoundException when policy does not exist")
        void policyNotFound() {
            when(policyRepo.findById(policyId)).thenReturn(Optional.empty());

            assertThrows(PolicyNotFoundException.class, () -> policyService.activate(policyId));
        }
    }

    @Nested
    @DisplayName("cancel")
    class CancelTests {

        @Test
        @DisplayName("Happy path: should cancel active policy")
        void happyPath() {
            PolicyEntity entity = mock(PolicyEntity.class);
            CurrencyEntity currency = mock(CurrencyEntity.class);
            Policy domain = mock(Policy.class);
            PolicyResponse response = mock(PolicyResponse.class);

            when(policyRepo.findById(policyId)).thenReturn(Optional.of(entity));
            when(entity.getCurrency()).thenReturn(currency);
            when(currency.getCode()).thenReturn("RON");
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponse(domain, "RON")).thenReturn(response);

            CancelPolicyRequest cancelRequest = new CancelPolicyRequest("Customer requested");
            PolicyResponse result = policyService.cancel(policyId, cancelRequest);

            assertNotNull(result);
            verify(domain).cancel("Customer requested");
            verify(policyRepo).save(entity);
        }

        @Test
        @DisplayName("Should throw PolicyNotFoundException when policy does not exist")
        void policyNotFound() {
            when(policyRepo.findById(policyId)).thenReturn(Optional.empty());

            CancelPolicyRequest cancelReq = new CancelPolicyRequest("reason");
            assertThrows(PolicyNotFoundException.class,
                    () -> policyService.cancel(policyId, cancelReq));
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTests {

        @Test
        @DisplayName("Happy path: should return policy detail response with currency details")
        void happyPath() {
            PolicyEntity entity = mock(PolicyEntity.class);
            CurrencyEntity currency = mock(CurrencyEntity.class);
            ClientEntity clientEntity = mock(ClientEntity.class);
            BuildingEntity buildingEntity = mock(BuildingEntity.class);
            CityEntity city = mock(CityEntity.class);
            CountyEntity county = mock(CountyEntity.class);
            CountryEntity country = mock(CountryEntity.class);
            Policy domain = mock(Policy.class);
            Client clientDomain = mock(Client.class);
            Building buildingDomain = mock(Building.class);
            PolicyDetailResponse detailResponse = mock(PolicyDetailResponse.class);

            when(policyRepo.findById(policyId)).thenReturn(Optional.of(entity));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(entity.getClient()).thenReturn(clientEntity);
            when(entity.getBuilding()).thenReturn(buildingEntity);
            when(entity.getCurrency()).thenReturn(currency);
            when(buildingEntity.getCity()).thenReturn(city);
            when(city.getCounty()).thenReturn(county);
            when(county.getCountry()).thenReturn(country);
            when(clientEntityMapper.toDomain(clientEntity)).thenReturn(clientDomain);
            when(buildingEntityMapper.toDomain(buildingEntity)).thenReturn(buildingDomain);
            when(dtoMapper.toDetailResponse(domain, currency, clientDomain,
                    buildingDomain, city, county, country)).thenReturn(detailResponse);

            PolicyDetailResponse result = policyService.getById(policyId);

            assertNotNull(result);
            verify(dtoMapper).toDetailResponse(domain, currency, clientDomain,
                    buildingDomain, city, county, country);
        }

        @Test
        @DisplayName("Should throw PolicyNotFoundException when policy does not exist")
        void policyNotFound() {
            when(policyRepo.findById(policyId)).thenReturn(Optional.empty());

            assertThrows(PolicyNotFoundException.class, () -> policyService.getById(policyId));
        }
    }

    @Nested
    @DisplayName("expireOverduePolicies")
    class ExpireOverduePolicies {

        @Test
        @DisplayName("Should return 0 when no overdue policies")
        void shouldReturnZeroWhenNoOverduePolicies() {
            when(policyRepo.markActiveOverdueAsExpired(any(), any(), any(LocalDate.class), any(Instant.class)))
                    .thenReturn(0);

            int result = policyService.expire();

            assertEquals(0, result);
            verify(policyRepo).markActiveOverdueAsExpired(
                    eq(PolicyStatusEntity.ACTIVE), eq(PolicyStatusEntity.EXPIRED), any(LocalDate.class), any(Instant.class));
        }

        @Test
        @DisplayName("Should return count and pass ACTIVE/EXPIRED when N policies expired")
        void shouldReturnCountWhenPoliciesExpired() {
            when(policyRepo.markActiveOverdueAsExpired(any(), any(), any(LocalDate.class), any(Instant.class)))
                    .thenReturn(3);

            int result = policyService.expire();

            assertEquals(3, result);
            ArgumentCaptor<LocalDate> todayCaptor = ArgumentCaptor.forClass(LocalDate.class);
            verify(policyRepo).markActiveOverdueAsExpired(
                    eq(PolicyStatusEntity.ACTIVE), eq(PolicyStatusEntity.EXPIRED), todayCaptor.capture(), any(Instant.class));
            assertEquals(LocalDate.now(), todayCaptor.getValue());
        }
    }
}
