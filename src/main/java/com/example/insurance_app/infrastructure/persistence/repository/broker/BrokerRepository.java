package com.example.insurance_app.infrastructure.persistence.repository.broker;

import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BrokerRepository extends JpaRepository<BrokerEntity, UUID> {

    boolean existsByBrokerCodeIgnoreCase(String brokerCode);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
}
