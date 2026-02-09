package com.example.insurance_app.service.metadata;

import com.example.insurance_app.application.dto.metadata.currency.CurrencyAction;
import com.example.insurance_app.application.dto.metadata.currency.request.CreateCurrencyRequest;
import com.example.insurance_app.application.dto.metadata.currency.request.CurrencyActionRequest;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.CurrencyDtoMapper;
import com.example.insurance_app.application.service.metadata.CurrencyService;
import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.metadata.currency.Currency;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.CurrencyEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.CurrencyRepository;
import com.example.insurance_app.infrastructure.persistence.repository.policy.PolicyRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurrencyService Unit Tests")
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private CurrencyDtoMapper currencyDtoMapper;
    @Mock
    private CurrencyEntityMapper currencyEntityMapper;
    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private CurrencyService currencyService;

    @Nested
    @DisplayName("createCurrency")
    class CreateTests {

        @Test
        @DisplayName("Happy path: should create currency")
        void happyPath() {
            CreateCurrencyRequest req = new CreateCurrencyRequest("RON", "Romanian Leu", new BigDecimal("1.000000"), true);

            when(currencyRepository.existsByCode("RON")).thenReturn(false);
            Currency domain = mock(Currency.class);
            CurrencyEntity entity = mock(CurrencyEntity.class);
            CurrencyEntity saved = mock(CurrencyEntity.class);
            Currency savedDomain = mock(Currency.class);
            CurrencyResponse response = mock(CurrencyResponse.class);

            when(currencyDtoMapper.toDomain(req)).thenReturn(domain);
            when(currencyEntityMapper.toEntity(domain)).thenReturn(entity);
            when(currencyRepository.save(entity)).thenReturn(saved);
            when(saved.getId()).thenReturn(UUID.randomUUID());
            when(currencyEntityMapper.toDomain(saved)).thenReturn(savedDomain);
            when(currencyDtoMapper.toResponse(savedDomain)).thenReturn(response);

            CurrencyResponse result = currencyService.createCurrency(req);
            assertNotNull(result);
            verify(currencyRepository).save(entity);
        }

        @Test
        @DisplayName("Should throw on duplicate code")
        void duplicateCode() {
            CreateCurrencyRequest req = new CreateCurrencyRequest("RON", "Romanian Leu", new BigDecimal("1.000000"), true);
            when(currencyRepository.existsByCode("RON")).thenReturn(true);

            assertThrows(DuplicateResourceException.class, () -> currencyService.createCurrency(req));
            verify(currencyRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("executeAction")
    class ActionTests {

        @Test
        @DisplayName("Should activate currency")
        void shouldActivate() {
            UUID id = UUID.randomUUID();
            CurrencyEntity entity = mock(CurrencyEntity.class);
            Currency domain = mock(Currency.class);
            Currency updated = mock(Currency.class);
            CurrencyResponse response = mock(CurrencyResponse.class);

            when(currencyRepository.findById(id)).thenReturn(Optional.of(entity));
            when(currencyEntityMapper.toDomain(entity)).thenReturn(domain, updated);
            when(currencyRepository.save(entity)).thenReturn(entity);
            when(currencyDtoMapper.toResponse(updated)).thenReturn(response);

            CurrencyResponse result = currencyService.executeAction(id, new CurrencyActionRequest(CurrencyAction.ACTIVATE));
            assertNotNull(result);
            verify(domain).activate();
        }

        @Test
        @DisplayName("Should deactivate when no active policies")
        void shouldDeactivateNoActivePolicies() {
            UUID id = UUID.randomUUID();
            CurrencyEntity entity = mock(CurrencyEntity.class);
            Currency domain = mock(Currency.class);
            Currency updated = mock(Currency.class);
            CurrencyResponse response = mock(CurrencyResponse.class);

            when(currencyRepository.findById(id)).thenReturn(Optional.of(entity));
            when(currencyEntityMapper.toDomain(entity)).thenReturn(domain, updated);
            when(policyRepository.existsByStatusAndCurrencyId(PolicyStatusEntity.ACTIVE, id)).thenReturn(false);
            when(currencyRepository.save(entity)).thenReturn(entity);
            when(currencyDtoMapper.toResponse(updated)).thenReturn(response);

            CurrencyResponse result = currencyService.executeAction(id, new CurrencyActionRequest(CurrencyAction.DEACTIVATE));
            assertNotNull(result);
            verify(domain).deactivate();
        }

        @Test
        @DisplayName("Should throw when deactivating currency used by active policies")
        void shouldThrowWhenActivePoliciesExist() {
            UUID id = UUID.randomUUID();
            CurrencyEntity entity = mock(CurrencyEntity.class);
            Currency domain = mock(Currency.class);

            when(currencyRepository.findById(id)).thenReturn(Optional.of(entity));
            when(currencyEntityMapper.toDomain(entity)).thenReturn(domain);
            when(policyRepository.existsByStatusAndCurrencyId(PolicyStatusEntity.ACTIVE, id)).thenReturn(true);

            CurrencyActionRequest req = new CurrencyActionRequest(CurrencyAction.DEACTIVATE);
            assertThrows(DomainValidationException.class,
                    () -> currencyService.executeAction(id, req));
        }

        @Test
        @DisplayName("Should throw when currency not found")
        void notFound() {
            UUID id = UUID.randomUUID();
            when(currencyRepository.findById(id)).thenReturn(Optional.empty());

            CurrencyActionRequest req = new CurrencyActionRequest(CurrencyAction.ACTIVATE);
            assertThrows(ResourceNotFoundException.class,
                    () -> currencyService.executeAction(id, req));
        }
    }

    @Nested
    @DisplayName("getAllCurrencies")
    class ListTests {

        @Test
        @DisplayName("Should return paginated results")
        void shouldReturnPaginatedResults() {
            Pageable pageable = PageRequest.of(0, 10);
            CurrencyEntity entity = mock(CurrencyEntity.class);
            Currency domain = mock(Currency.class);
            CurrencyResponse response = mock(CurrencyResponse.class);

            Page<CurrencyEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
            when(currencyRepository.findAllByOrderByCodeAsc(pageable)).thenReturn(page);
            when(currencyEntityMapper.toDomain(entity)).thenReturn(domain);
            when(currencyDtoMapper.toResponse(domain)).thenReturn(response);

            var result = currencyService.getAllCurrencies(pageable);
            assertEquals(1, result.content().size());
        }
    }
}
