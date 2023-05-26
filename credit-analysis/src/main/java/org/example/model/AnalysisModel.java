package org.example.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

public record AnalysisModel(
        UUID clientId,
        boolean approved,
        BigDecimal approvedLimit,
        BigDecimal withdraw,
        BigDecimal annualInterest
) {

    @Builder(toBuilder = true)
    public AnalysisModel(UUID clientId, boolean approved, BigDecimal approvedLimit, BigDecimal withdraw,
                         BigDecimal annualInterest) {
        this.clientId = clientId;
        this.approved = approved;
        this.approvedLimit = approvedLimit;
        this.withdraw = withdraw;
        this.annualInterest = annualInterest;
    }
}
