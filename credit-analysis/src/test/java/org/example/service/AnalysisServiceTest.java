package org.example.service;

import org.example.credit.analysis.ClientApiClient;
import org.example.repository.AnalysisRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnalysisServiceTest {
    @Mock
    ClientApiClient clientApi;

    @Mock
    AnalysisRepository analysisRepository;

    @InjectMocks
    AnalysisService analysisService;

    @Test
    void getAll() {
    }

    @Test
    void createAnalysis() {
    }

    @Test
    void calculate() {
    }

    @Test
    void saveAnalysis() {
    }

    @Test
    void getAnalysisById() {
    }

    @Test
    void hasClient() {
    }
}