package org.example.credit.analysis;

import java.util.UUID;
import org.example.credit.analysis.dto.ClientSearch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "clientApi", url = "http://localhost:8080/v1.0/clients")
public interface ClientApiClient {
    @GetMapping(path = "/{id}")
    ClientSearch getClientById(@PathVariable UUID id);

    // Este endpoint na app de clientes estão fora do padrão, não faz sentido este path. No endpoint acima se não encontra ele retorna 404
    @GetMapping(path = "/exists/{id}")
    boolean getClientExistsById(@PathVariable UUID id);

    @GetMapping(path = "/cpf")
    ClientSearch getClientByCpf(@RequestParam String cpf);
}
