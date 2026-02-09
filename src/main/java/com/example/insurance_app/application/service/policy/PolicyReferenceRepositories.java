package com.example.insurance_app.application.service.policy;

import com.example.insurance_app.application.exception.ResourceNotFoundException;
import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import com.example.insurance_app.infrastructure.persistence.repository.broker.BrokerRepository;
import com.example.insurance_app.infrastructure.persistence.repository.building.BuildingRepository;
import com.example.insurance_app.infrastructure.persistence.repository.client.ClientRepository;
import com.example.insurance_app.infrastructure.persistence.repository.metadata.CurrencyRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public record PolicyReferenceRepositories(
        ClientRepository clientRepo,
        BuildingRepository buildingRepo,
        BrokerRepository brokerRepo,
        CurrencyRepository currencyRepo
) {
    public ClientEntity requireClient(UUID id) {
        return clientRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
    }

    public BuildingEntity requireBuilding(UUID id) {
        return buildingRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building", "id", id));
    }

    public BrokerEntity requireBroker(UUID id) {
        return brokerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Broker", "id", id));
    }

    public CurrencyEntity requireCurrency(UUID id) {
        return currencyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", id));
    }
}
