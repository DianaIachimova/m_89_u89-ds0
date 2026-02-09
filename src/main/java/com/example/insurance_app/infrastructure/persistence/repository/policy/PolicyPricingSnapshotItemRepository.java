package com.example.insurance_app.infrastructure.persistence.repository.policy;

import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyPricingSnapshotItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PolicyPricingSnapshotItemRepository extends JpaRepository<PolicyPricingSnapshotItemEntity, UUID> {

    boolean existsBySourceTypeAndSourceId(String sourceType, UUID sourceId);
}
