package org.example.model;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import lombok.Builder;

public record AnalysisModel(
        @NotBlank(message = "clientId is mandatory") @org.hibernate.validator.constraints.UUID(message = "clientId invalid.")
        String clientId,
        boolean approved,
        BigDecimal approvedLimit,
        BigDecimal withdraw,
        BigDecimal annualInterest
) {

    @Builder(toBuilder = true)
    public AnalysisModel(String clientId, boolean approved, BigDecimal approvedLimit, BigDecimal withdraw, BigDecimal annualInterest) {
        this.clientId = clientId;
        this.approved = approved;
        this.approvedLimit = approvedLimit;
        this.withdraw = withdraw;
        this.annualInterest = annualInterest;
    }
}
