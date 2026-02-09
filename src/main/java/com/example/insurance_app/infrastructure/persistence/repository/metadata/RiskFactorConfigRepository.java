package com.example.insurance_app.infrastructure.persistence.repository.metadata;

import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskFactorConfigurationEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.riskfactors.RiskLevelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RiskFactorConfigRepository extends JpaRepository<RiskFactorConfigurationEntity, UUID> {

    Page<RiskFactorConfigurationEntity> findAllBy(Pageable pageable);

    boolean existsByLevelAndReferenceIdAndActiveTrue(RiskLevelEntity level, UUID referenceId);

    boolean existsByLevelAndBuildingTypeAndActiveTrue(RiskLevelEntity level, BuildingTypeEntity buildingType);

    @Query("SELECT r FROM RiskFactorConfigurationEntity r WHERE r.active = true AND (" +
            "(r.level = 'COUNTRY' AND r.referenceId = :countryId) OR " +
            "(r.level = 'COUNTY' AND r.referenceId = :countyId) OR " +
            "(r.level = 'CITY' AND r.referenceId = :cityId) OR " +
            "(r.level = 'BUILDING_TYPE' AND r.buildingType = :buildingType))")
    List<RiskFactorConfigurationEntity> findAllActiveByTargets(
            @Param("countryId") UUID countryId,
            @Param("countyId") UUID countyId,
            @Param("cityId") UUID cityId,
            @Param("buildingType") BuildingTypeEntity buildingType);
}
