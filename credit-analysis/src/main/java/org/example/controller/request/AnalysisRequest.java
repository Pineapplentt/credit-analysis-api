package org.example.controller.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

public record AnalysisRequest (
        UUID clientId,
        BigDecimal monthlyIncome,
        BigDecimal requestedAmount
){
    @Builder()
    public AnalysisRequest{}
}
