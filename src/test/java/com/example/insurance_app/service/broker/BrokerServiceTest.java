package com.example.insurance_app.service.broker;

import com.example.insurance_app.application.dto.broker.request.CreateBrokerRequest;
import com.example.insurance_app.application.dto.broker.request.UpdateBrokerRequest;
import com.example.insurance_app.application.dto.broker.response.BrokerResponse;
import com.example.insurance_app.application.exception.DuplicateResourceException;
import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.application.mapper.BrokerDtoMapper;
import com.example.insurance_app.application.service.broker.BrokerService;
import com.example.insurance_app.domain.model.broker.Broker;
import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import com.example.insurance_app.infrastructure.persistence.mapper.BrokerEntityMapper;
import com.example.insurance_app.infrastructure.persistence.repository.broker.BrokerRepository;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("BrokerService Unit Tests")
class BrokerServiceTest {

    @Mock
    private BrokerRepository brokerRepository;
    @Mock
    private BrokerDtoMapper brokerDtoMapper;
    @Mock
    private BrokerEntityMapper brokerEntityMapper;

    @InjectMocks
    private BrokerService brokerService;

    private UUID brokerId;
    private CreateBrokerRequest createRequest;
    private UpdateBrokerRequest updateRequest;

    @BeforeEach
    void setUp() {
        brokerId = UUID.randomUUID();
        createRequest = new CreateBrokerRequest(
                "BRK-001", "Test Broker", "broker@test.com",
                "+40712345678", new BigDecimal("0.05"), true
        );
        updateRequest = new UpdateBrokerRequest(
                "Updated Broker", "updated@test.com",
                "+40712345679", new BigDecimal("0.1")
        );
    }

    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Happy path: should create broker")
        void happyPath() {
            when(brokerRepository.existsByBrokerCodeIgnoreCase(any())).thenReturn(false);
            when(brokerRepository.existsByEmailIgnoreCase(any())).thenReturn(false);

            Broker domain = mock(Broker.class);
            BrokerEntity entity = mock(BrokerEntity.class);
            BrokerEntity saved = mock(BrokerEntity.class);
            Broker savedDomain = mock(Broker.class);
            BrokerResponse response = mock(BrokerResponse.class);

            when(brokerDtoMapper.toDomain(createRequest)).thenReturn(domain);
            when(brokerEntityMapper.toEntity(domain)).thenReturn(entity);
            when(brokerRepository.save(entity)).thenReturn(saved);
            when(saved.getId()).thenReturn(brokerId);
            when(brokerEntityMapper.toDomain(saved)).thenReturn(savedDomain);
            when(brokerDtoMapper.toResponse(savedDomain)).thenReturn(response);

            BrokerResponse result = brokerService.create(createRequest);

            assertNotNull(result);
            verify(brokerRepository).save(entity);
        }

        @Test
        @DisplayName("Should throw on duplicate broker code")
        void duplicateBrokerCode() {
            when(brokerRepository.existsByBrokerCodeIgnoreCase(any())).thenReturn(true);

            assertThrows(DuplicateResourceException.class, () -> brokerService.create(createRequest));
            verify(brokerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw on duplicate email")
        void duplicateEmail() {
            when(brokerRepository.existsByBrokerCodeIgnoreCase(any())).thenReturn(false);
            when(brokerRepository.existsByEmailIgnoreCase(any())).thenReturn(true);

            assertThrows(DuplicateResourceException.class, () -> brokerService.create(createRequest));
            verify(brokerRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Happy path: should update broker")
        void happyPath() {
            BrokerEntity entity = mock(BrokerEntity.class);
            Broker domain = mock(Broker.class);
            BrokerEntity saved = mock(BrokerEntity.class);
            Broker updated = mock(Broker.class);
            BrokerResponse response = mock(BrokerResponse.class);

            when(brokerRepository.findById(brokerId)).thenReturn(Optional.of(entity));
            when(brokerRepository.existsByEmailIgnoreCaseAndIdNot(any(), eq(brokerId))).thenReturn(false);
            when(brokerEntityMapper.toDomain(entity)).thenReturn(domain);
            when(brokerRepository.save(entity)).thenReturn(saved);
            when(brokerEntityMapper.toDomain(saved)).thenReturn(updated);
            when(brokerDtoMapper.toResponse(updated)).thenReturn(response);

            BrokerResponse result = brokerService.update(brokerId, updateRequest);

            assertNotNull(result);
            verify(domain).updateDetails(any(), any(), any());
        }

        @Test
        @DisplayName("Should throw on duplicate email for different broker")
        void duplicateEmail() {
            BrokerEntity entity = mock(BrokerEntity.class);
            when(brokerRepository.findById(brokerId)).thenReturn(Optional.of(entity));
            when(brokerRepository.existsByEmailIgnoreCaseAndIdNot(any(), eq(brokerId))).thenReturn(true);

            assertThrows(DuplicateResourceException.class,
                    () -> brokerService.update(brokerId, updateRequest));
            verify(brokerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when broker not found")
        void brokerNotFound() {
            when(brokerRepository.findById(brokerId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> brokerService.update(brokerId, updateRequest));
        }
    }

    @Nested
    @DisplayName("activate / deactivate")
    class StatusTests {

        @Test
        @DisplayName("Should activate broker")
        void shouldActivate() {
            BrokerEntity entity = mock(BrokerEntity.class);
            Broker domain = mock(Broker.class);
            Broker updated = mock(Broker.class);
            BrokerResponse response = mock(BrokerResponse.class);

            when(brokerRepository.findById(brokerId)).thenReturn(Optional.of(entity));
            when(brokerEntityMapper.toDomain(entity)).thenReturn(domain, updated);
            when(brokerRepository.save(entity)).thenReturn(entity);
            when(brokerDtoMapper.toResponse(updated)).thenReturn(response);

            BrokerResponse result = brokerService.activate(brokerId);

            assertNotNull(result);
            verify(domain).activate();
        }

        @Test
        @DisplayName("Should deactivate broker")
        void shouldDeactivate() {
            BrokerEntity entity = mock(BrokerEntity.class);
            Broker domain = mock(Broker.class);
            Broker updated = mock(Broker.class);
            BrokerResponse response = mock(BrokerResponse.class);

            when(brokerRepository.findById(brokerId)).thenReturn(Optional.of(entity));
            when(brokerEntityMapper.toDomain(entity)).thenReturn(domain, updated);
            when(brokerRepository.save(entity)).thenReturn(entity);
            when(brokerDtoMapper.toResponse(updated)).thenReturn(response);

            BrokerResponse result = brokerService.deactivate(brokerId);

            assertNotNull(result);
            verify(domain).deactivate();
        }

        @Test
        @DisplayName("Should throw on activate when not found")
        void activateNotFound() {
            when(brokerRepository.findById(brokerId)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> brokerService.activate(brokerId));
        }

        @Test
        @DisplayName("Should throw on deactivate when not found")
        void deactivateNotFound() {
            when(brokerRepository.findById(brokerId)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> brokerService.deactivate(brokerId));
        }
    }

    @Nested
    @DisplayName("getById")
    class GetByIdTests {

        @Test
        @DisplayName("Happy path: should return broker")
        void happyPath() {
            BrokerEntity entity = mock(BrokerEntity.class);
            Broker domain = mock(Broker.class);
            BrokerResponse response = mock(BrokerResponse.class);

            when(brokerRepository.findById(brokerId)).thenReturn(Optional.of(entity));
            when(brokerEntityMapper.toDomain(entity)).thenReturn(domain);
            when(brokerDtoMapper.toResponse(domain)).thenReturn(response);

            BrokerResponse result = brokerService.getById(brokerId);
            assertNotNull(result);
        }

        @Test
        @DisplayName("Should throw when not found")
        void notFound() {
            when(brokerRepository.findById(brokerId)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> brokerService.getById(brokerId));
        }
    }

    @Nested
    @DisplayName("list")
    class ListTests {

        @Test
        @DisplayName("Should return paginated results")
        void shouldReturnPaginatedResults() {
            Pageable pageable = PageRequest.of(0, 10);
            BrokerEntity entity = mock(BrokerEntity.class);
            Broker domain = mock(Broker.class);
            BrokerResponse response = mock(BrokerResponse.class);

            Page<BrokerEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
            when(brokerRepository.findAll(pageable)).thenReturn(page);
            when(brokerEntityMapper.toDomain(entity)).thenReturn(domain);
            when(brokerDtoMapper.toResponse(domain)).thenReturn(response);

            var result = brokerService.list(pageable);

            assertEquals(1, result.content().size());
            assertEquals(1, result.totalElements());
        }
    }
}
