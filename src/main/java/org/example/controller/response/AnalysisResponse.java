package org.example.controller.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

public record AnalysisResponse(
        UUID idAnalysis,
        boolean approved,
        BigDecimal approvedLimit,
        BigDecimal withdraw,
        BigDecimal annualInterest,
        UUID clientId,
        LocalDateTime date
) {
    @Builder(toBuilder = true)

    public AnalysisResponse(UUID idAnalysis, boolean approved, BigDecimal approvedLimit, BigDecimal withdraw,
                            BigDecimal annualInterest, UUID clientId, LocalDateTime date) {
        this.idAnalysis = idAnalysis;
        this.approved = approved;
        this.approvedLimit = approvedLimit;
        this.withdraw = withdraw;
        this.annualInterest = annualInterest;
        this.clientId = clientId;
        this.date = LocalDateTime.now();
    }
}
