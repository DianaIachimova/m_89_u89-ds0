package com.example.insurance_app.infrastructure.persistence.entity.client;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "identification_number_changes",
        indexes = {
                @Index(name = "idx_inc_client_id", columnList = "client_id"),
        }
)
public class IdentificationNumberChangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_inc_client"))
    private ClientEntity client;

    @Column(name = "old_value", length = 13)
    private String oldValue;

    @Column(name = "new_value", nullable = false, length = 13)
    private String newValue;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(name = "changed_by", nullable = false, length = 100)
    private String changedBy;

    @Column(name = "reason", nullable = false, length = 100)
    private String reason;

    protected IdentificationNumberChangeEntity() {
    }

    public IdentificationNumberChangeEntity(
            ClientEntity client,
            String oldValue,
            String newValue,
            Instant changedAt,
            String changedBy,
            String reason
    ) {
        this.client = client;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
        this.reason = reason;
    }

    public UUID getId() {
        return id;
    }

    public ClientEntity getClient() {
        return client;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public String getReason() {
        return reason;
    }

}