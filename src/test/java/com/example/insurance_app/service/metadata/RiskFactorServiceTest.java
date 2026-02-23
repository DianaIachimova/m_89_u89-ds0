package com.example.insurance_app.service.metadata;

import com.example.insurance_app.application.dto.metadata.riskfactors.RiskFactorAction;
import com.example.insurance_app.application.dto.metadata.riskfactors.RiskLevelDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.CreateRiskFactorRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.RiskFactorActionRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.UpdateRiskFactorPercentageRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.response.RiskFactorResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.RiskFactorDtoMapper;
import com.example.insurance_app.application.service.metadata.RiskFactorService;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskFactorConfiguration;
import com.example.insurance_app.domain.model.metadata.riskfactors.RiskLevel;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.AdjustmentPercentage;
import com.example.insurance_app.domain.model.metadata.riskfactors.vo.RiskTarget;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskFactorConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.RiskFactorEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.geography.CountryRepository;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.RiskFactorConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static com.example.insurance_app.infrastructure.persistence.mapper.EnumEntityMapper.toRiskLevelEntity;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
@DisplayName("RiskFactorService Unit Tests")
class RiskFactorServiceTest {

    @Mock
    private RiskFactorConfigRepository riskFactorRepository;
    @Mock
    private RiskFactorEntityMapper entityMapper;
    @Mock
    private RiskFactorDtoMapper dtoMapper;
    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private RiskFactorService riskFactorService;

    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Happy path: create COUNTRY level risk factor")
        void createCountryLevel() {
            UUID refId = UUID.randomUUID();
            CreateRiskFactorRequest req = new CreateRiskFactorRequest(
                    RiskLevelDto.COUNTRY, refId, null, new BigDecimal("0.05"), true
            );

            RiskTarget target = RiskTarget.ofGeography(RiskLevel.COUNTRY, refId);
            RiskFactorConfiguration domain = RiskFactorConfiguration.createNew(
                    target, AdjustmentPercentage.of(new BigDecimal("0.05")), true
            );

            when(dtoMapper.toDomain(req)).thenReturn(domain);
            when(countryRepository.existsById(refId)).thenReturn(true);
            when(riskFactorRepository.existsByLevelAndReferenceIdAndActiveTrue(toRiskLevelEntity(RiskLevel.COUNTRY), refId)).thenReturn(false);

            RiskFactorConfigurationEntity entity = mock(RiskFactorConfigurationEntity.class);
            RiskFactorConfigurationEntity saved = mock(RiskFactorConfigurationEntity.class);
            RiskFactorConfiguration result = mock(RiskFactorConfiguration.class);
            RiskFactorResponse response = mock(RiskFactorResponse.class);

            when(entityMapper.toEntity(domain)).thenReturn(entity);
            when(riskFactorRepository.save(entity)).thenReturn(saved);
            when(saved.getId()).thenReturn(UUID.randomUUID());
            when(entityMapper.toDomain(saved)).thenReturn(result);
            when(dtoMapper.toResponse(result)).thenReturn(response);

            RiskFactorResponse actual = riskFactorService.create(req);
            assertNotNull(actual);
            verify(riskFactorRepository).save(entity);
        }

        @Test
        @DisplayName("Should throw when geography reference does not exist")
        void geoRefNotFound() {
            UUID refId = UUID.randomUUID();
            CreateRiskFactorRequest req = new CreateRiskFactorRequest(
                    RiskLevelDto.COUNTRY, refId, null, new BigDecimal("0.05"), false
            );

            RiskTarget target = RiskTarget.ofGeography(RiskLevel.COUNTRY, refId);
            RiskFactorConfiguration domain = RiskFactorConfiguration.createNew(
                    target, AdjustmentPercentage.of(new BigDecimal("0.05")), false
            );

            when(dtoMapper.toDomain(req)).thenReturn(domain);
            when(countryRepository.existsById(refId)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class, () -> riskFactorService.create(req));
            verify(riskFactorRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when active conflict exists")
        void activeConflict() {
            UUID refId = UUID.randomUUID();
            CreateRiskFactorRequest req = new CreateRiskFactorRequest(
                    RiskLevelDto.COUNTRY, refId, null, new BigDecimal("0.05"), true
            );

            RiskTarget target = RiskTarget.ofGeography(RiskLevel.COUNTRY, refId);
            RiskFactorConfiguration domain = RiskFactorConfiguration.createNew(
                    target, AdjustmentPercentage.of(new BigDecimal("0.05")), true
            );

            when(dtoMapper.toDomain(req)).thenReturn(domain);
            when(countryRepository.existsById(refId)).thenReturn(true);
            when(riskFactorRepository.existsByLevelAndReferenceIdAndActiveTrue(toRiskLevelEntity(RiskLevel.COUNTRY), refId)).thenReturn(true);

            assertThrows(DuplicateResourceException.class, () -> riskFactorService.create(req));
            verify(riskFactorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updatePercentage")
    class UpdatePercentageTests {

        @Test
        @DisplayName("Happy path: should update percentage")
        void happyPath() {
            UUID id = UUID.randomUUID();
            RiskFactorConfigurationEntity entity = mock(RiskFactorConfigurationEntity.class);
            RiskFactorConfiguration domain = mock(RiskFactorConfiguration.class);
            RiskFactorConfigurationEntity saved = mock(RiskFactorConfigurationEntity.class);
            RiskFactorConfiguration result = mock(RiskFactorConfiguration.class);
            RiskFactorResponse response = mock(RiskFactorResponse.class);

            when(riskFactorRepository.findById(id)).thenReturn(Optional.of(entity));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(riskFactorRepository.save(entity)).thenReturn(saved);
            when(entityMapper.toDomain(saved)).thenReturn(result);
            when(dtoMapper.toResponse(result)).thenReturn(response);

            UpdateRiskFactorPercentageRequest req = new UpdateRiskFactorPercentageRequest(new BigDecimal("0.10"));
            RiskFactorResponse actual = riskFactorService.updatePercentage(id, req);

            assertNotNull(actual);
            verify(domain).updatePercentage(any(AdjustmentPercentage.class));
        }

        @Test
        @DisplayName("Should throw when not found")
        void notFound() {
            UUID id = UUID.randomUUID();
            when(riskFactorRepository.findById(id)).thenReturn(Optional.empty());

            UpdateRiskFactorPercentageRequest req = new UpdateRiskFactorPercentageRequest(new BigDecimal("0.10"));
            assertThrows(ResourceNotFoundException.class,
                    () -> riskFactorService.updatePercentage(id, req));
        }
    }

    @Nested
    @DisplayName("executeAction")
    class ExecuteActionTests {

        @Test
        @DisplayName("Should activate risk factor")
        void shouldActivate() {
            UUID id = UUID.randomUUID();
            UUID refId = UUID.randomUUID();
            RiskFactorConfigurationEntity entity = mock(RiskFactorConfigurationEntity.class);
            RiskTarget target = RiskTarget.ofGeography(RiskLevel.COUNTRY, refId);
            RiskFactorConfiguration domain = RiskFactorConfiguration.createNew(
                    target, AdjustmentPercentage.of(new BigDecimal("0.05")), false
            );

            when(riskFactorRepository.findById(id)).thenReturn(Optional.of(entity));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(riskFactorRepository.existsByLevelAndReferenceIdAndActiveTrue(toRiskLevelEntity(RiskLevel.COUNTRY), refId)).thenReturn(false);

            RiskFactorConfigurationEntity saved = mock(RiskFactorConfigurationEntity.class);
            RiskFactorConfiguration result = mock(RiskFactorConfiguration.class);
            RiskFactorResponse response = mock(RiskFactorResponse.class);

            when(riskFactorRepository.save(entity)).thenReturn(saved);
            when(entityMapper.toDomain(saved)).thenReturn(result);
            when(dtoMapper.toResponse(result)).thenReturn(response);

            RiskFactorResponse actual = riskFactorService.executeAction(id, new RiskFactorActionRequest(RiskFactorAction.ACTIVATE));
            assertNotNull(actual);
            assertTrue(domain.isActive());
        }

        @Test
        @DisplayName("Should deactivate risk factor")
        void shouldDeactivate() {
            UUID id = UUID.randomUUID();
            RiskFactorConfigurationEntity entity = mock(RiskFactorConfigurationEntity.class);
            RiskTarget target = RiskTarget.ofGeography(RiskLevel.COUNTRY, UUID.randomUUID());
            RiskFactorConfiguration domain = RiskFactorConfiguration.createNew(
                    target, AdjustmentPercentage.of(new BigDecimal("0.05")), true
            );

            when(riskFactorRepository.findById(id)).thenReturn(Optional.of(entity));
            when(entityMapper.toDomain(entity)).thenReturn(domain);

            RiskFactorConfigurationEntity saved = mock(RiskFactorConfigurationEntity.class);
            RiskFactorConfiguration result = mock(RiskFactorConfiguration.class);
            RiskFactorResponse response = mock(RiskFactorResponse.class);

            when(riskFactorRepository.save(entity)).thenReturn(saved);
            when(entityMapper.toDomain(saved)).thenReturn(result);
            when(dtoMapper.toResponse(result)).thenReturn(response);

            RiskFactorResponse actual = riskFactorService.executeAction(id, new RiskFactorActionRequest(RiskFactorAction.DEACTIVATE));
            assertNotNull(actual);
            assertFalse(domain.isActive());
        }

        @Test
        @DisplayName("Should throw on activate with active conflict")
        void activateConflict() {
            UUID id = UUID.randomUUID();
            UUID refId = UUID.randomUUID();
            RiskFactorConfigurationEntity entity = mock(RiskFactorConfigurationEntity.class);
            RiskTarget target = RiskTarget.ofGeography(RiskLevel.COUNTRY, refId);
            RiskFactorConfiguration domain = RiskFactorConfiguration.createNew(
                    target, AdjustmentPercentage.of(new BigDecimal("0.05")), false
            );

            when(riskFactorRepository.findById(id)).thenReturn(Optional.of(entity));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(riskFactorRepository.existsByLevelAndReferenceIdAndActiveTrue(toRiskLevelEntity(RiskLevel.COUNTRY), refId)).thenReturn(true);

            RiskFactorActionRequest req = new RiskFactorActionRequest(RiskFactorAction.ACTIVATE);
            assertThrows(DuplicateResourceException.class,
                    () -> riskFactorService.executeAction(id, req));
        }
    }

    @Nested
    @DisplayName("list")
    class ListTests {

        @Test
        @DisplayName("Should return paginated results")
        void shouldReturnPaginatedResults() {
            Pageable pageable = PageRequest.of(0, 10);
            RiskFactorConfigurationEntity entity = mock(RiskFactorConfigurationEntity.class);
            RiskFactorConfiguration domain = mock(RiskFactorConfiguration.class);
            RiskFactorResponse response = mock(RiskFactorResponse.class);

            Page<RiskFactorConfigurationEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
            when(riskFactorRepository.findAllBy(pageable)).thenReturn(page);
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponse(domain)).thenReturn(response);

            var result = riskFactorService.listRiskFactors(pageable);
            assertEquals(1, result.content().size());
        }
    }
}
