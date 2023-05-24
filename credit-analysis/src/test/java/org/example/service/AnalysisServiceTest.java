package org.example.service;

import org.example.controller.request.AnalysisRequest;
import org.example.controller.response.AnalysisResponse;
import org.example.credit.analysis.ClientApiClient;
import org.example.credit.analysis.dto.ClientSearch;
import org.example.exception.ClientNotFoundException;
import org.example.mapper.AnalysisEntityMapper;
import org.example.mapper.AnalysisEntityMapperImpl;
import org.example.mapper.AnalysisMapper;
import org.example.mapper.AnalysisMapperImpl;
import org.example.mapper.AnalysisResponseMapper;
import org.example.mapper.AnalysisResponseMapperImpl;
import org.example.repository.AnalysisRepository;
import org.example.repository.entity.AnalysisEntity;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AnalysisServiceTest {
    @Mock
    private ClientApiClient clientApi;

    @Mock
    private AnalysisRepository analysisRepository;

    @Spy
    private AnalysisEntityMapper analysisEntityMapper = new AnalysisEntityMapperImpl();

    @Spy
    private AnalysisResponseMapper analysisResponseMapper = new AnalysisResponseMapperImpl();

    @Spy
    private AnalysisMapper analysisMapper = new AnalysisMapperImpl();

    @InjectMocks
    private AnalysisService analysisService;

    @Captor
    private ArgumentCaptor<UUID> clientSearchUUIDArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> clientSearchCpfArgumentCaptor;

    @Captor
    private ArgumentCaptor<AnalysisRequest> analysisRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<AnalysisEntity> analysisEntityArgumentCaptor;

    @Test
    void should_return_all_analysis_from_cpf() {
        final ClientSearch clientSearch = clientSearchFactory();
        when(clientApi.getClientByCpf(clientSearchCpfArgumentCaptor.capture())).thenReturn(clientSearchFactory());
        when(analysisService.getAll(clientSearch.cpf()))
                .thenReturn(analysisEntityListFactory());
    }

    @Test
    void should_return_all_analysis_from_uuid() {
//        final ClientSearch clientSearch = clientSearchFactory();
//        when(analysisService.getAll(clientSearchUUIDArgumentCaptor.capture()))
//                .thenReturn(analysisEntityListFactory());
    }

    @Test
    void should_create_analysis() {
        final ClientSearch clientSearch = clientSearchFactory();
        when(clientApi.getClientById(clientSearchUUIDArgumentCaptor.capture())).thenReturn(clientSearch);
        when(analysisRepository.save(analysisEntityArgumentCaptor.capture())).thenReturn(analysisEntityFactory());

        final AnalysisRequest analysisRequest = analysisRequestFactory();
        final AnalysisResponse analysisResponse = analysisService.createAnalysis(analysisRequest);

        assertNotNull(analysisResponse);
        assertNotNull(analysisResponse.idAnalysis());
    }

    @Test
    void should_throw_client_not_found_exception_when_client_not_found_when_creating_analysis() {
        assertThrows(ClientNotFoundException.class, () -> analysisService.createAnalysis(analysisRequestFactory()));
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

    public static ClientSearch clientSearchFactory() {
        return ClientSearch.builder()
                .id(UUID.randomUUID())
                .name("João Vitor Sales")
                .cpf("44422200022")
                .birthdate(LocalDate.now().minusYears(15))
                .build();
    }

    public static AnalysisRequest analysisRequestFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id())
                .build();
    }

    public static AnalysisEntity analysisEntityFactory() {
        return AnalysisEntity.builder()
                .idAnalysis(UUID.randomUUID())
                .approved(true)
                .approvedLimit(BigDecimal.valueOf(10000))
                .withdraw(BigDecimal.valueOf(1000))
                .annualInterest(BigDecimal.valueOf(0.15))
                .clientId(UUID.randomUUID())
                .build();
    }

    public static List<AnalysisEntity> analysisEntityListFactory() {
        return List.of(analysisEntityFactory(), analysisEntityFactory(), analysisEntityFactory());
    }

    public static UUID uuidParamFactory(String uuid) {
        return UUID.fromString(uuid);
    }
}