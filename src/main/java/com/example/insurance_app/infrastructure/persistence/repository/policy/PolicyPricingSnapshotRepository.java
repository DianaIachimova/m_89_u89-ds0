package com.example.insurance_app.infrastructure.persistence.repository.policy;

import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyPricingSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PolicyPricingSnapshotRepository extends JpaRepository<PolicyPricingSnapshotEntity, UUID> {

    Optional<PolicyPricingSnapshotEntity> findByPolicyId(UUID policyId);
}
