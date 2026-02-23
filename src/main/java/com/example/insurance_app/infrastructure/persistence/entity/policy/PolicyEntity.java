package com.example.insurance_app.infrastructure.persistence.entity.policy;

import jakarta.persistence.*;
import com.example.insurance_app.infrastructure.persistence.entity.broker.BrokerEntity;
import com.example.insurance_app.infrastructure.persistence.entity.building.BuildingEntity;
import com.example.insurance_app.infrastructure.persistence.entity.client.ClientEntity;
import com.example.insurance_app.infrastructure.persistence.entity.metadata.CurrencyEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "policies", uniqueConstraints = {
        @UniqueConstraint(name = "uk_policies_number", columnNames = "policy_number")
}, indexes = {
        @Index(name = "idx_policies_client", columnList = "client_id"),
        @Index(name = "idx_policies_building", columnList = "building_id"),
        @Index(name = "idx_policies_broker", columnList = "broker_id"),
        @Index(name = "idx_policies_status", columnList = "status"),
        @Index(name = "idx_policies_dates", columnList = "start_date, end_date")
})
@EntityListeners(AuditingEntityListener.class)
public class PolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "policy_number", nullable = false, length = 30, updatable = false)
    private String policyNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_policies_client"))
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_policies_building"))
    private BuildingEntity building;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "broker_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_policies_broker"))
    private BrokerEntity broker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_policies_currency"))
    private CurrencyEntity currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private PolicyStatusEntity status;

    @Embedded
    private PolicyDetailsEmbeddable policyDetails;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected PolicyEntity() {
    }

    public PolicyEntity(String policyNumber,
                        ClientEntity client,
                        BuildingEntity building,
                        BrokerEntity broker,
                        CurrencyEntity currency,
                        PolicyStatusEntity status,
                        PolicyDetailsEmbeddable policyDetails) {
        this.policyNumber = policyNumber;
        this.client = client;
        this.building = building;
        this.broker = broker;
        this.currency = currency;
        this.status = status;
        this.policyDetails = policyDetails;
    }


    public UUID getId() {
        return id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }

    public BuildingEntity getBuilding() {
        return building;
    }

    public void setBuilding(BuildingEntity building) {
        this.building = building;
    }

    public BrokerEntity getBroker() {
        return broker;
    }

    public void setBroker(BrokerEntity broker) {
        this.broker = broker;
    }

    public CurrencyEntity getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyEntity currency) {
        this.currency = currency;
    }

    public PolicyStatusEntity getStatus() {
        return status;
    }

    public void setStatus(PolicyStatusEntity status) {
        this.status = status;
    }

    public PolicyDetailsEmbeddable getPolicyDetails() {
        return policyDetails;
    }

    public void setPolicyDetails(PolicyDetailsEmbeddable policyDetails) {
        this.policyDetails = policyDetails;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
