package com.example.insurance_app.service.metadata;

import com.example.insurance_app.application.dto.metadata.feeconfig.FeeConfigTypeDto;
import com.example.insurance_app.application.dto.metadata.feeconfig.request.CreateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.response.FeeConfigResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.mapper.FeeConfigDtoMapper;
import com.example.insurance_app.application.service.metadata.FeeConfigurationService;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfiguration;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeConfigurationType;
import com.example.insurance_app.domain.model.metadata.feeconfig.FeeDetails;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.EffectivePeriod;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeCode;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeeName;
import com.example.insurance_app.domain.model.metadata.feeconfig.vo.FeePercentage;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.FeeConfigEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.FeeConfigRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeeConfigurationService Unit Tests")
class FeeConfigurationServiceTest {

    @Mock
    private FeeConfigRepository feeRepository;
    @Mock
    private FeeConfigEntityMapper feeEntityMapper;
    @Mock
    private FeeConfigDtoMapper feeDtoMapper;

    @InjectMocks
    private FeeConfigurationService feeConfigurationService;

    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Happy path: should create fee configuration")
        void happyPath() {
            CreateFeeConfigRequest req = new CreateFeeConfigRequest(
                    "ADMIN_FEE", "Admin Fee", FeeConfigTypeDto.ADMIN_FEE,
                    new BigDecimal("0.10"), LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31), true
            );

            FeeDetails details = FeeDetails.of(
                    FeeCode.of("ADMIN_FEE"), FeeName.of("Admin Fee"),
                    FeeConfigurationType.ADMIN_FEE,
                    FeePercentage.of(new BigDecimal("0.10")),
                    EffectivePeriod.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
            );
            FeeConfiguration domain = FeeConfiguration.createNew(details, true);

            when(feeDtoMapper.toDomain(req)).thenReturn(domain);
            when(feeRepository.existsActiveOverlapNative(any(), any(), any(), any())).thenReturn(false);

            FeeConfigurationEntity entity = mock(FeeConfigurationEntity.class);
            FeeConfigurationEntity saved = mock(FeeConfigurationEntity.class);
            FeeConfiguration savedDomain = mock(FeeConfiguration.class);
            FeeConfigResponse response = mock(FeeConfigResponse.class);

            when(feeEntityMapper.toEntity(domain)).thenReturn(entity);
            when(feeRepository.save(entity)).thenReturn(saved);
            when(saved.getId()).thenReturn(UUID.randomUUID());
            when(feeEntityMapper.toDomain(saved)).thenReturn(savedDomain);
            when(feeDtoMapper.toResponse(savedDomain)).thenReturn(response);

            FeeConfigResponse result = feeConfigurationService.create(req);
            assertNotNull(result);
            verify(feeRepository).save(entity);
        }

        @Test
        @DisplayName("Should throw on overlapping period")
        void overlappingPeriod() {
            CreateFeeConfigRequest req = new CreateFeeConfigRequest(
                    "ADMIN_FEE", "Admin Fee", FeeConfigTypeDto.ADMIN_FEE,
                    new BigDecimal("0.10"), LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 12, 31), true
            );

            FeeDetails details = FeeDetails.of(
                    FeeCode.of("ADMIN_FEE"), FeeName.of("Admin Fee"),
                    FeeConfigurationType.ADMIN_FEE,
                    FeePercentage.of(new BigDecimal("0.10")),
                    EffectivePeriod.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
            );
            FeeConfiguration domain = FeeConfiguration.createNew(details, true);

            when(feeDtoMapper.toDomain(req)).thenReturn(domain);
            when(feeRepository.existsActiveOverlapNative(any(), any(), any(), any())).thenReturn(true);

            assertThrows(DuplicateResourceException.class, () -> feeConfigurationService.create(req));
            verify(feeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listFeeConfigurations")
    class ListTests {

        @Test
        @DisplayName("Should return paginated results")
        void shouldReturnPaginatedResults() {
            Pageable pageable = PageRequest.of(0, 10);
            FeeConfigurationEntity entity = mock(FeeConfigurationEntity.class);
            FeeConfiguration domain = mock(FeeConfiguration.class);
            FeeConfigResponse response = mock(FeeConfigResponse.class);

            Page<FeeConfigurationEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
            when(feeRepository.findAllByOrderByTypeAsc(pageable)).thenReturn(page);
            when(feeEntityMapper.toDomain(entity)).thenReturn(domain);
            when(feeDtoMapper.toResponse(domain)).thenReturn(response);

            var result = feeConfigurationService.listFeeConfigurations(pageable);
            assertEquals(1, result.content().size());
        }
    }
}
