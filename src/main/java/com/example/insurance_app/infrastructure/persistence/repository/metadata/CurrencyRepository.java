package com.example.insurance_app.infrastructure.persistence.repository.metadata;

import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, UUID> {
    boolean existsByCode(String code);

    Optional<CurrencyEntity> findByCode(String code);
    Page<CurrencyEntity> findAllByOrderByCodeAsc(Pageable pageable);
}
