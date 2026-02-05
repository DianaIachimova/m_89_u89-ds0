package com.example.insurance_app.infrastructure.persistence.repository.client;

import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    boolean existsByIdentificationNumber(String identificationNumber);

    @Query("""
                SELECT c FROM ClientEntity c WHERE
            (
                :name IS NULL OR :name = '' OR
                LOWER(c.name) LIKE CONCAT(LOWER(:name), '%') OR
                LOWER(c.name) LIKE CONCAT('% ', LOWER(:name), '%')
            ) AND (
                :identificationNumber IS NULL OR :identificationNumber = '' OR
                c.identificationNumber = :identificationNumber)
            """)
    Page<ClientEntity> searchClients(
            @Param("name") String name,
            @Param("identificationNumber") String identificationNumber,
            Pageable pageable
    );
}
