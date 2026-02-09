package com.example.insurance_app.infrastructure.persistence.repository.policy;

import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, UUID>,
        JpaSpecificationExecutor<PolicyEntity> {

    @Query("SELECT COUNT(p) > 0 FROM PolicyEntity p WHERE p.status = :status AND p.currency.id = :currencyId")
    boolean existsByStatusAndCurrencyId(
            @Param("status") PolicyStatusEntity status,
            @Param("currencyId") UUID currencyId);
}
