package com.example.insurance_app.infrastructure.persistence.entity.policy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
public class PolicyDetailsEmbeddable {
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "base_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePremium;

    @Column(name = "final_premium", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalPremium;

    @Column(name = "cancelled_at")
    private LocalDate cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    protected PolicyDetailsEmbeddable() {

    }

    public PolicyDetailsEmbeddable(LocalDate startDate, LocalDate endDate,
                                   BigDecimal basePremium, BigDecimal finalPremium,
                                   LocalDate cancelledAt, String cancellationReason)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.basePremium = basePremium;
        this.finalPremium = finalPremium;
        this.cancelledAt = cancelledAt;
        this.cancellationReason = cancellationReason;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(BigDecimal basePremium) {
        this.basePremium = basePremium;
    }

    public BigDecimal getFinalPremium() {
        return finalPremium;
    }

    public void setFinalPremium(BigDecimal finalPremium) {
        this.finalPremium = finalPremium;
    }

    public LocalDate getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDate cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
