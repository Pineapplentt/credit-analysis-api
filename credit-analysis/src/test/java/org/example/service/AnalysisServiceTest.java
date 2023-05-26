package org.example.service;

import feign.FeignException;
import java.util.Objects;
import java.util.Optional;
import org.example.controller.request.AnalysisRequest;
import org.example.controller.response.AnalysisResponse;
import org.example.credit.analysis.ClientApiClient;
import org.example.credit.analysis.dto.ClientSearch;
import org.example.exception.ClientNotFoundException;
import org.example.exception.IllegalArgumentException;
import org.example.mapper.AnalysisEntityMapper;
import org.example.mapper.AnalysisEntityMapperImpl;
import org.example.mapper.AnalysisMapper;
import org.example.mapper.AnalysisMapperImpl;
import org.example.mapper.AnalysisResponseMapper;
import org.example.mapper.AnalysisResponseMapperImpl;
import org.example.model.AnalysisModel;
import org.example.repository.AnalysisRepository;
import org.example.repository.entity.AnalysisEntity;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


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
        when(clientApi.getClientByCpf(clientSearchCpfArgumentCaptor.capture())).thenReturn(clientSearchFactory());
        when(analysisRepository.findByClientId(clientSearchUUIDArgumentCaptor.capture())).thenReturn(analysisEntityListFactory());

        List<AnalysisEntity> list = analysisService.getAll(clientSearchFactory().cpf());

        assertNotNull(list);
        assertThat(list, is(not(empty())));
        assertInstanceOf(List.class, list);
        assertThat(list, everyItem(instanceOf(AnalysisEntity.class)));
    }

    @Test
    void should_return_all_analysis_from_uuid() {
        when(analysisRepository.findByClientId(clientSearchUUIDArgumentCaptor.capture())).thenReturn(analysisEntityListFactory());

        List<AnalysisEntity> list = analysisService.getAll(clientSearchFactory().id().toString());

        assertNotNull(list);
        assertThat(list, is(not(empty())));
        assertInstanceOf(List.class, list);
        assertThat(list, everyItem(instanceOf(AnalysisEntity.class)));
    }

    @Test
    void should_create_analysis() {
        when(analysisRepository.save(analysisEntityArgumentCaptor.capture())).thenReturn(analysisEntityFactory());
        when(analysisService.hasClient(clientSearchUUIDArgumentCaptor.capture())).thenReturn(true);

        final AnalysisRequest analysisRequest = analysisRequestFactory();
        final AnalysisResponse analysisResponse = analysisService.createAnalysis(analysisRequest);

        assertNotNull(analysisResponse);
        assertNotNull(analysisResponse.idAnalysis());

        assertEquals(analysisRequest.clientId(), clientSearchUUIDArgumentCaptor.getValue());

        final AnalysisEntity analysisEntity = analysisEntityArgumentCaptor.getValue();
        assertEquals(analysisRequest.clientId(), analysisEntity.getClientId());
    }

    @Test
    void should_throw_client_not_found_exception_when_client_not_found_when_creating_analysis() {
        assertThrows(ClientNotFoundException.class, () -> analysisService.createAnalysis(analysisRequestFactory()));
    }

    @Test
    void should_throw_illegal_argument_exception_when_uuid_does_not_match_regex() {
        assertThrows(IllegalArgumentException.class, () -> analysisService.createAnalysis(invalidAnalysisUUIDFactory()));
    }

    @Test
    void should_deny_credit_if_requested_amount_greater_than_monthly_income() {
        AnalysisModel analysisModel = analysisService.calculate(analysisRequestRequestedAmountGreaterThanMonthlyIncomeFactory());

        assertFalse(analysisModel.approved());
    }

    @Test
    void should_approve_credit_if_monthly_income_greater_than_requested_amount() {
        AnalysisModel analysisModel = analysisService.calculate(analysisRequestMonthlyIncomeGreaterThanRequestedAmountFactory());

        assertTrue(analysisModel.approved());
    }

    @Test
    void should_throw_client_not_found_exception_when_cpf_not_found() {
        when(clientApi.getClientByCpf(clientSearchCpfArgumentCaptor.capture())).thenThrow(FeignException.FeignClientException.class);

        assertThrows(ClientNotFoundException.class, () -> analysisService.getAll(clientSearchFactory().cpf()));
    }

    @Test
    void should_throw_illegal_argument_exception_when_invalid_search_parameter() {
        assertThrows(IllegalArgumentException.class, () -> analysisService.getAll(invalidStringFactory()));
    }

    public static ClientSearch clientSearchFactory() {
        return ClientSearch.builder()
                .id(UUID.randomUUID())
                .name("João Vitor Sales")
                .cpf("44422200022")
                .birthdate(LocalDate.now().minusYears(15))
                .build();
    }

    public static String invalidStringFactory() {
        return "Anything different from cpf: '\\d{3}(\\.?\\d{3}){2}-?\\d{2}'\n" +
                " or UUID: '[a-fA-F0-9]{8}(?:-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}'";
    }

    public static AnalysisRequest invalidAnalysisUUIDFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(UUID.fromString("42e773f2-9693-4f25-a1c7-44579cd08c4"))
                .build();
    }

    public static AnalysisRequest analysisRequestFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id())
                .build();
    }

    public static AnalysisRequest analysisRequestRequestedAmountGreaterThanMonthlyIncomeFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(10000))
                .monthlyIncome(BigDecimal.valueOf(1000))
                .clientId(clientSearchFactory().id())
                .build();
    }

    public static AnalysisRequest analysisRequestMonthlyIncomeGreaterThanRequestedAmountFactory() {
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
                .clientId(clientSearchFactory().id())
                .build();
    }

    public static List<AnalysisEntity> analysisEntityListFactory() {
        return List.of(analysisEntityFactory(), analysisEntityFactory(), analysisEntityFactory());
    }
}