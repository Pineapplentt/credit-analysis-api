package org.example.controller.request;

import java.math.BigDecimal;
import lombok.Builder;

public record AnalysisRequest(
        String clientId,
        BigDecimal monthlyIncome,
        BigDecimal requestedAmount
) {
    @Builder()
    public AnalysisRequest {
    }
}
