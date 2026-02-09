package com.example.insurance_app.infrastructure.persistence.repository.metadata;

import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskFactorConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskLevelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RiskFactorConfigRepository extends JpaRepository<RiskFactorConfigurationEntity, UUID> {

    Page<RiskFactorConfigurationEntity>findAllBy(Pageable pageable);

    boolean existsByLevelAndReferenceIdAndActiveTrue(RiskLevelEntity level, UUID referenceId);

    boolean existsByLevelAndBuildingTypeAndActiveTrue(RiskLevelEntity level, BuildingTypeEntity buildingType);
}
