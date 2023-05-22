package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.controller.request.AnalysisRequest;
import org.example.controller.response.AnalysisResponse;
import org.example.credit.analysis.ClientApiClient;
import org.example.repository.Entity.AnalysisEntity;
import org.example.service.AnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/list/{param}")
    public List<AnalysisEntity> getAllAnalysis(@PathVariable String param) {
        return this.analysisService.getAll(param);
    }

    @GetMapping("/{id}")
    public AnalysisResponse getAnalysisById(@PathVariable UUID id) {
        return this.analysisService.getAnalysisById(id);
    }
}
