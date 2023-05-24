package org.example.credit.analysis.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

public record ClientSearch(
        UUID id,
        String name,
        String cpf,
        LocalDate birthdate
) {
    @Builder(toBuilder = true)
    public ClientSearch(UUID id, String name, String cpf, LocalDate birthdate) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.birthdate = birthdate;
    }
}
