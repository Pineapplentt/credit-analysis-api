package org.example.controller.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

public record AnalysisRequest(
        // Este validator uuid é necessário?
        @NotBlank @org.hibernate.validator.constraints.UUID
        UUID clientId,

        @NotBlank
        BigDecimal monthlyIncome,

        @NotBlank
        BigDecimal requestedAmount
) {
    @Builder()
    public AnalysisRequest {
    }
}
