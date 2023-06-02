package org.example.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.controller.request.AnalysisRequest;
import org.example.controller.response.AnalysisResponse;
import org.example.credit.analysis.ClientApiClient;
import org.example.repository.entity.AnalysisEntity;
import org.example.service.AnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credit/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;
    private final ClientApiClient clientApi;

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public AnalysisResponse createAnalysis(@RequestBody AnalysisRequest analysisRequest) {
        return this.analysisService.createAnalysis(analysisRequest);
    }


    // O get de recursos é um lista, não é correto utilizar /list no path
    // Aqui deveria utilizar query parameters, um para id e outro para cpf
    @GetMapping("/list/{param}")
    public List<AnalysisEntity> getAllAnalysis(@PathVariable String param) {
        return this.analysisService.getAll(param);
    }

    @GetMapping("/{id}")
    public AnalysisResponse getAnalysisById(@PathVariable UUID id) {
        return this.analysisService.getAnalysisById(id);
    }
}
