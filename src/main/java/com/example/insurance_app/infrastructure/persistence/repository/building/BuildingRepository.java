package com.example.insurance_app.infrastructure.persistence.repository.building;

import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuildingRepository extends JpaRepository<BuildingEntity, UUID> {

    @Query("SELECT b FROM BuildingEntity b WHERE b.owner.id = :clientId")
    List<BuildingEntity> findByOwnerId(@Param("clientId") UUID clientId);

    @Query("SELECT b FROM BuildingEntity b " +
            "LEFT JOIN FETCH b.owner " +
            "LEFT JOIN FETCH b.city c " +
            "LEFT JOIN FETCH c.county co " +
            "LEFT JOIN FETCH co.country " +
            "WHERE b.id = :id")
    BuildingEntity findByIdWithGeography(@Param("id") UUID id);
}
