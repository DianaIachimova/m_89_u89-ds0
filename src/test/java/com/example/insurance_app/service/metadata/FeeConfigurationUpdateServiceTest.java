package com.example.insurance_app.service.metadata;

import com.example.insurance_app.application.dto.metadata.feeconfig.request.UpdateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.response.FeeConfigResponse;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.FeeConfigDtoMapper;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.application.service.metadata.FeeConfigurationUpdateService;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfiguration;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeDetails;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.*;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.FeeConfigEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.FeeConfigRepository;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyPricingSnapshotItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeeConfigurationUpdateService Unit Tests")
class FeeConfigurationUpdateServiceTest {

    @Mock
    private FeeConfigRepository feeRepository;
    @Mock
    private FeeConfigEntityMapper feeEntityMapper;
    @Mock
    private FeeConfigDtoMapper feeDtoMapper;
    @Mock
    private PolicyPricingSnapshotItemRepository snapshotItemRepo;

    @InjectMocks
    private FeeConfigurationUpdateService updateService;

    private FeeConfiguration createDomainConfig() {
        FeeDetails details = FeeDetails.of(
                FeeCode.of("ADMIN_FEE"), FeeName.of("Admin Fee"),
                FeeConfigurationType.ADMIN_FEE,
                FeePercentage.of(new BigDecimal("0.10")),
                EffectivePeriod.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
        );
        return FeeConfiguration.createNew(details, true);
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Should update in-place when not used by snapshot")
        void updateInPlace() {
            UUID id = UUID.randomUUID();
            FeeConfigurationEntity entity = mock(FeeConfigurationEntity.class);
            when(entity.getId()).thenReturn(id);
            when(feeRepository.findById(id)).thenReturn(Optional.of(entity));

            FeeConfiguration domain = createDomainConfig();
            when(feeEntityMapper.toDomain(entity)).thenReturn(domain);
            when(snapshotItemRepo.existsBySourceTypeAndSourceId(id)).thenReturn(false);

            FeeConfigurationEntity saved = mock(FeeConfigurationEntity.class);
            FeeConfiguration updatedDomain = mock(FeeConfiguration.class);
            FeeConfigResponse response = mock(FeeConfigResponse.class);

            when(feeRepository.save(entity)).thenReturn(saved);
            when(feeEntityMapper.toDomain(saved)).thenReturn(updatedDomain);
            when(feeDtoMapper.toResponse(updatedDomain)).thenReturn(response);

            UpdateFeeConfigRequest req = new UpdateFeeConfigRequest("Updated Fee", new BigDecimal("0.15"), null);
            FeeConfigResponse result = updateService.update(id, req);

            assertNotNull(result);
            verify(feeRepository).save(entity);
        }

        @Test
        @DisplayName("Should create new version when used by snapshot")
        void updateByNewVersion() {
            UUID id = UUID.randomUUID();
            FeeConfigurationEntity entity = mock(FeeConfigurationEntity.class);
            when(entity.getId()).thenReturn(id);
            when(feeRepository.findById(id)).thenReturn(Optional.of(entity));

            FeeConfiguration domain = createDomainConfig();
            when(feeEntityMapper.toDomain(entity)).thenReturn(domain);
            when(snapshotItemRepo.existsBySourceTypeAndSourceId(id)).thenReturn(true);

            FeeConfigurationEntity newEntity = mock(FeeConfigurationEntity.class);
            FeeConfigurationEntity savedNew = mock(FeeConfigurationEntity.class);
            FeeConfiguration newDomain = mock(FeeConfiguration.class);
            FeeConfigResponse response = mock(FeeConfigResponse.class);

            when(feeRepository.save(entity)).thenReturn(entity);
            when(feeEntityMapper.toEntity(any(FeeConfiguration.class))).thenReturn(newEntity);
            when(feeRepository.save(newEntity)).thenReturn(savedNew);
            when(savedNew.getId()).thenReturn(UUID.randomUUID());
            when(feeEntityMapper.toDomain(savedNew)).thenReturn(newDomain);
            when(feeDtoMapper.toResponse(newDomain)).thenReturn(response);

            UpdateFeeConfigRequest req = new UpdateFeeConfigRequest("Updated Fee", new BigDecimal("0.15"), null);
            FeeConfigResponse result = updateService.update(id, req);

            assertNotNull(result);
            verify(feeRepository, times(2)).save(any());
        }

        @Test
        @DisplayName("Should throw when all fields are null")
        void allFieldsNull() {
            UUID id = UUID.randomUUID();
            FeeConfigurationEntity entity = mock(FeeConfigurationEntity.class);
            when(feeRepository.findById(id)).thenReturn(Optional.of(entity));

            UpdateFeeConfigRequest req = new UpdateFeeConfigRequest(null, null, null);

            assertThrows(IllegalArgumentException.class, () -> updateService.update(id, req));
        }

        @Test
        @DisplayName("Should throw when not found")
        void notFound() {
            UUID id = UUID.randomUUID();
            when(feeRepository.findById(id)).thenReturn(Optional.empty());

            UpdateFeeConfigRequest req = new UpdateFeeConfigRequest("Updated", null, null);
            assertThrows(ResourceNotFoundException.class, () -> updateService.update(id, req));
        }
    }

    @Nested
    @DisplayName("deactivate")
    class DeactivateTests {

        @Test
        @DisplayName("Should deactivate fee configuration when not referenced by active policy snapshots")
        void happyPath() {
            UUID id = UUID.randomUUID();
            FeeConfigurationEntity entity = mock(FeeConfigurationEntity.class);
            when(entity.getId()).thenReturn(id);
            when(feeRepository.findById(id)).thenReturn(Optional.of(entity));
            when(snapshotItemRepo.existsFeeConfigReferencedInSnapshots(id, PolicyStatusEntity.ACTIVE)).thenReturn(false);

            FeeConfiguration domain = createDomainConfig();
            when(feeEntityMapper.toDomain(entity)).thenReturn(domain);

            FeeConfigurationEntity saved = mock(FeeConfigurationEntity.class);
            FeeConfiguration updatedDomain = mock(FeeConfiguration.class);
            FeeConfigResponse response = mock(FeeConfigResponse.class);

            when(feeRepository.save(entity)).thenReturn(saved);
            when(feeEntityMapper.toDomain(saved)).thenReturn(updatedDomain);
            when(updatedDomain.getId()).thenReturn(new FeeConfigurationId(id));
            when(feeDtoMapper.toResponse(updatedDomain)).thenReturn(response);

            FeeConfigResponse result = updateService.deactivate(id);
            assertNotNull(result);
            verify(feeRepository).save(entity);
        }

        @Test
        @DisplayName("Should throw DomainValidationException when fee is referenced by active policy snapshots")
        void deactivateWhenInUse() {
            UUID id = UUID.randomUUID();
            FeeConfigurationEntity entity = mock(FeeConfigurationEntity.class);
            when(feeRepository.findById(id)).thenReturn(Optional.of(entity));
            when(snapshotItemRepo.existsFeeConfigReferencedInSnapshots(id, PolicyStatusEntity.ACTIVE)).thenReturn(true);

            DomainValidationException ex = assertThrows(DomainValidationException.class, () -> updateService.deactivate(id));
            assertNotNull(ex.getMessage());
            assertTrue(ex.getMessage().contains("referenced by policy pricing snapshots"));
            verify(feeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when not found")
        void notFound() {
            UUID id = UUID.randomUUID();
            when(feeRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> updateService.deactivate(id));
        }
    }
}
