package com.example.insurance_app.infrastructure.persistence.repository.geography;

import com.example.insurance_app.infrastructure.persistence.entity.geography.CountryEntity;
import com.example.insurance_app.infrastructure.persistence.projection.geography.CountryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity,UUID> {
    List<CountryProjection> findAllByOrderByNameAsc();

}
