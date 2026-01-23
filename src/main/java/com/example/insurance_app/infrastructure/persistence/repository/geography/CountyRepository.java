package com.example.insurance_app.infrastructure.persistence.repository.geography;

import com.example.insurance_app.infrastructure.persistence.entity.geography.CountyEntity;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CountyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface CountyRepository extends JpaRepository<CountyEntity, UUID> {
    List<CountyProjection> findByCountryIdOrderByNameAsc(UUID countryId);
}
