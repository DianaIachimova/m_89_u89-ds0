package com.example.insurance_app.infrastructure.persistence.entity.metadata;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "currencies", uniqueConstraints = {
        @UniqueConstraint(name = "uk_currencies_code", columnNames = "code")
},  indexes = {
        @Index(name = "idx_currency_is_active", columnList = "is_active")
})
@EntityListeners(AuditingEntityListener.class)
public class CurrencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "code", nullable = false, length = 3, updatable = false)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "exchange_rate_to_base", nullable = false, precision = 16, scale = 6)
    private BigDecimal exchangeRateToBase;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CurrencyEntity() {}

    public CurrencyEntity(UUID id, String code, String name, BigDecimal exchangeRateToBase, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.exchangeRateToBase = exchangeRateToBase;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getExchangeRateToBase() {
        return exchangeRateToBase;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
        if (!(o instanceof CurrencyEntity other)) return false;
        return id!=null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
