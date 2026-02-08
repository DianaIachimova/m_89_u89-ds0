package com.example.insurance_app.infrastructure.persistence.repository.metadata;

import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigTypeEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig.FeeConfigurationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface FeeConfigRepository extends JpaRepository<FeeConfigurationEntity, UUID> {
    Page<FeeConfigurationEntity> findAllByOrderByTypeAsc(Pageable pageable);

    @Query(value = """
select (count(*) > 0)
from fee_configurations f
where f.type = cast(:type as varchar)
  and f.is_active = true
  and f.code = cast(:code as varchar)
  and (
       (f.effective_to is null or cast(:from as date) < f.effective_to)
       and
       (cast(:to as date) is null or f.effective_from < cast(:to as date))
  )
""", nativeQuery = true)
    boolean existsActiveOverlapNative(
            @Param("type") String type,
            @Param("code") String code,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
