package com.example.insurance_app.infrastructure.persistence.repository.policy;

import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyPricingSnapshotItemEntity;
import com.example.insurance_app.infrastructure.persistence.entity.policy.PolicyStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PolicyPricingSnapshotItemRepository extends JpaRepository<PolicyPricingSnapshotItemEntity, UUID> {

    @Query("SELECT COUNT(i) > 0 FROM PolicyPricingSnapshotItemEntity i " +
            "WHERE i.sourceId = :feeConfigId " +
            "AND i.sourceType IN ('FEE_CONFIGURATION', 'FEE_RISK_ADJUSTMENT') ")
    boolean existsBySourceTypeAndSourceId(@Param("feeConfigId") UUID sourceId);

    @Query("SELECT COUNT(i) > 0 FROM PolicyPricingSnapshotItemEntity i " +
            "WHERE i.sourceId = :feeConfigId " +
            "AND i.sourceType IN ('FEE_CONFIGURATION', 'FEE_RISK_ADJUSTMENT') " +
            "AND i.snapshot.policy.status = :policyStatus")
    boolean existsFeeConfigReferencedInSnapshots(@Param("feeConfigId") UUID feeConfigId,
                                                 @Param("policyStatus") PolicyStatusEntity policyStatus);

}
