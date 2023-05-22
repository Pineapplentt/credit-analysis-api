package org.example.credit.analysis.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

public record ClientSearch(
        UUID clientId,
        String name,
        String cpf,
        LocalDate birthdate
) {
    @Builder(toBuilder = true)
    public ClientSearch (UUID clientId, String name, String cpf, LocalDate birthdate) {
        this.clientId = clientId;
        this.name = name;
        this.cpf = cpf;
        this.birthdate = birthdate;
    }
}
