package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Seu projeto esta dentro de uma pasta, com o gitgnore fora desta, isso esta fazendo subir pastas e arquivos que não deveriam para o
// repositório remoto.
// A app não esta fazendo build devido a uma violação no checkstyle
@SpringBootApplication
@EnableFeignClients
public class AnalysisApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalysisApplication.class, args);
    }
}
