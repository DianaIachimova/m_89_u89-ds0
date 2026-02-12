package com.example.insurance_app.infrastructure.persistence.entity.broker;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "brokers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_brokers_code", columnNames = "broker_code"),
        @UniqueConstraint(name = "uk_brokers_email", columnNames = "email")
}, indexes = {
        @Index(name = "idx_brokers_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class BrokerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "broker_code", nullable = false, length = 30, updatable = false)
    private String brokerCode;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "commission_percentage", precision = 5, scale = 4)
    private BigDecimal commissionPercentage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BrokerEntity() {
    }

    public BrokerEntity(UUID id, String brokerCode, String name,
                        String email, String phone, String status,
                        BigDecimal commissionPercentage) {
        this.id = id;
        this.brokerCode = brokerCode;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.commissionPercentage = commissionPercentage;
    }

    public UUID getId() {
        return id;
    }

    public String getBrokerCode() {
        return brokerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(BigDecimal commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
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
        if (!(o instanceof BrokerEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
