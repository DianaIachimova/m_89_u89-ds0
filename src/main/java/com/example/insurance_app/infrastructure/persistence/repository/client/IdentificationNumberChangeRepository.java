package com.example.insurance_app.infrastructure.persistence.repository.client;

import com.example.insurance_app.infrastructure.persistence.entity.client.IdentificationNumberChangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IdentificationNumberChangeRepository extends JpaRepository<IdentificationNumberChangeEntity, UUID> {

    List<IdentificationNumberChangeEntity> findByClientIdOrderByChangedAtDesc(UUID clientId);
}
