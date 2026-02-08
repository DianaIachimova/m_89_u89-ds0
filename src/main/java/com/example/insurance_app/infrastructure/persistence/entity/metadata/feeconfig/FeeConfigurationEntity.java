package com.example.insurance_app.infrastructure.persistence.entity.metadata.feeconfig;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "fee_configurations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_fee_configurations", columnNames = {"type","code", "is_active", "effective_from","effective_to"})
})
@EntityListeners(AuditingEntityListener.class)
public class FeeConfigurationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "code", nullable = false, updatable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private FeeConfigTypeEntity type;

    @Column(name = "percentage", nullable = false, precision = 6, scale = 4)
    private BigDecimal percentage;

    @Embedded
    ValidityPeriodEmbeddable period;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected FeeConfigurationEntity() {}

    public FeeConfigurationEntity(UUID id, String code, String name, FeeConfigTypeEntity type, BigDecimal percentage, ValidityPeriodEmbeddable period, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.percentage = percentage;
        this.period = period;
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

    public FeeConfigTypeEntity getType() {
        return type;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public boolean isActive() {
        return active;
    }

    public ValidityPeriodEmbeddable getPeriod() {
        return period;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPeriod(ValidityPeriodEmbeddable period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeeConfigurationEntity other)) return false;
        return id!=null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
