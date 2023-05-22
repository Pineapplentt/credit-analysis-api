package org.example.controller.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AnalysisResponse (
        UUID idAnalysis,
        boolean approved,
        BigDecimal approvedLimit,
        BigDecimal withdraw,
        BigDecimal annualInterest,
        UUID clientId,
        LocalDateTime date
){
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
