package org.example.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Immutable
@Table(name = "ANALYSIS")
public class AnalysisEntity {

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    @Id
    private UUID idAnalysis;
    private UUID clientId;
    private boolean approved;
    private BigDecimal approvedLimit;
    private BigDecimal withdraw;
    private BigDecimal annualInterest;


    public AnalysisEntity() {
    }

    @Builder(toBuilder = true)
    public AnalysisEntity(
            UUID idAnalysis, UUID clientId, boolean approved, BigDecimal approvedLimit,
            BigDecimal withdraw, BigDecimal annualInterest) {
        this.idAnalysis = UUID.randomUUID();
        this.clientId = clientId;
        this.approved = approved;
        this.approvedLimit = approvedLimit;
        this.withdraw = withdraw;
        this.annualInterest = annualInterest;
    }

    public UUID getIdAnalysis() {
        return idAnalysis;
    }

    public UUID getClientId() {
        return clientId;
    }

    public boolean isApproved() {
        return approved;
    }

    public BigDecimal getApprovedLimit() {
        return approvedLimit;
    }

    public BigDecimal getWithdraw() {
        return withdraw;
    }

    public BigDecimal getAnnualInterest() {
        return annualInterest;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
