package com.example.insurance_app.infrastructure.persistence.repository.geography;

import com.example.insurance_app.infrastructure.persistence.entity.geography.CityEntity;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CityProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, UUID> {
    List<CityProjection> findByCountyIdOrderByNameAsc(UUID countyId);
}
