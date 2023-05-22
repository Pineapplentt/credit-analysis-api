package org.example.credit.analysis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

public record ClientSearch(
        UUID uuid,
        String name,
        String cpf,
        LocalDate birthdate
) {
    @Builder(toBuilder = true)
    public ClientSearch (UUID uuid, String name, String cpf, LocalDate birthdate) {
        this.uuid = uuid;
        this.name = name;
        this.cpf = cpf;
        this.birthdate = birthdate;
    }
}
