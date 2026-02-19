package com.example.insurance_app.infrastructure.persistence.repository.policy;

import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, UUID>,
        JpaSpecificationExecutor<PolicyEntity> {

    @Query("""
                SELECT COUNT(p) > 0 FROM PolicyEntity p
                WHERE p.status = :status AND p.currency.id = :currencyId
            """)
    boolean existsByStatusAndCurrencyId(
            @Param("status") PolicyStatusEntity status,
            @Param("currencyId") UUID currencyId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE PolicyEntity p
            SET p.status = :expired, p.updatedAt = :now
            WHERE p.status = :active AND p.policyDetails.endDate < :today
           """)
    int markActiveOverdueAsExpired(
            @Param("active") PolicyStatusEntity active,
            @Param("expired") PolicyStatusEntity expired,
            @Param("today") LocalDate today,
            @Param("now") Instant now);

}
