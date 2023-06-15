package org.example.service;

import feign.FeignException;
import feign.RetryableException;
import java.util.Collections;
import java.util.Optional;
import org.example.controller.request.AnalysisRequest;
import org.example.controller.response.AnalysisResponse;
import org.example.credit.analysis.ClientApiClient;
import org.example.credit.analysis.dto.ClientSearch;
import org.example.exception.AnalysisNotFoundException;
import org.example.exception.ApiConnectionException;
import org.example.exception.ClientNotFoundException;
import org.example.exception.CustomIllegalArgumentException;
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
    private ArgumentCaptor<String> clientSearchParamArgumentCaptor;

    @Captor
    private ArgumentCaptor<AnalysisRequest> analysisRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<AnalysisEntity> analysisEntityArgumentCaptor;

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
    void should_return_all_analysis_from_cpf() {
        when(clientApi.getClientBy(clientSearchCpfArgumentCaptor.capture())).thenReturn(Collections.singletonList(clientSearchFactory()));
        when(analysisRepository.findByClientId(clientSearchUUIDArgumentCaptor.capture())).thenReturn(analysisEntityListFactory());

        List<AnalysisEntity> list = analysisService.getAll(clientSearchFactory().cpf());

        assertNotNull(list);
        assertThat(list, is(not(empty())));
        assertInstanceOf(List.class, list);
        assertThat(list, everyItem(instanceOf(AnalysisEntity.class)));
    }

    @Test
    void should_throw_api_connection_exception_when_client_api_is_down() {
        when(clientApi.getClientBy(clientSearchParamArgumentCaptor.capture())).thenThrow(RetryableException.class);
        assertThrows(ApiConnectionException.class, () -> analysisService.getAll(clientSearchFactory().cpf()));
    }

    @Test
    void should_create_analysis() {
        when(analysisRepository.save(analysisEntityArgumentCaptor.capture())).thenReturn(analysisEntityFactory());

        final AnalysisRequest analysisRequest = analysisRequestFactory();
        final AnalysisResponse analysisResponse = analysisService.createAnalysis(analysisRequest);

        assertNotNull(analysisResponse);
        assertNotNull(analysisResponse.idAnalysis());

        final AnalysisEntity analysisEntity = analysisEntityArgumentCaptor.getValue();
        assertEquals(analysisRequest.clientId(), analysisEntity.getClientId().toString());
    }

    @Test
    void should_throw_client_not_found_exception_when_client_not_found_when_creating_analysis() {
        when(clientApi.getClientById(clientSearchUUIDArgumentCaptor.capture())).thenThrow(FeignException.FeignClientException.class);
        assertThrows(ClientNotFoundException.class, () -> analysisService.createAnalysis(analysisRequestFactory()));
    }

    @Test
    void should_throw_client_not_found_exception_when_client_not_found_when_saving_analysis() {
        when(clientApi.getClientById(clientSearchUUIDArgumentCaptor.capture())).thenThrow(FeignException.FeignClientException.class);
        assertThrows(ClientNotFoundException.class, () -> analysisService.saveAnalysis(analysisEntityFactory()));
    }

    @Test
    void should_return_analysis_by_id() {
        when(analysisRepository.findById(clientSearchUUIDArgumentCaptor.capture())).thenReturn(Optional.of(analysisEntityFactory()));
        AnalysisResponse analysisResponse = analysisService.getAnalysisById(clientSearchFactory().id());

        assertNotNull(analysisResponse);
        assertInstanceOf(UUID.class, analysisResponse.idAnalysis());
    }

    @Test
    void should_throw_analysis_not_found_exception_when_analysis_not_found() {
        when(analysisRepository.findById(clientSearchUUIDArgumentCaptor.capture())).thenReturn(Optional.empty());
        assertThrows(AnalysisNotFoundException.class, () -> analysisService.getAnalysisById(clientSearchFactory().id()));
    }

    @Test
    void should_throw_illegal_argument_exception_when_param_does_not_match_regex() {
        assertThrows(CustomIllegalArgumentException.class, () -> analysisService.createAnalysis(invalidAnalysisUUIDFactory()));
    }

    @Test
    void should_approve_credit_if_requested_amount_less_than_monthly_income_and_half_monthly_income() {
        AnalysisModel analysisModel = analysisService.calculate(analysisRequestRequestedAmountLessThanMonthlyIncomeAndLessThanHalfMonthlyIncomeFactory());
        assertTrue(analysisModel.approved());
    }

    @Test
    void should_approve_credit_if_monthly_income_greater_than_max_income() {
        AnalysisModel analysisModel = analysisService.calculate(analysisRequestMonthlyIncomeGreaterThanMaxIncomeFactory());
        assertTrue(analysisModel.approved());
    }

    @Test
    void should_deny_credit_if_requested_amount_less_than_monthly_income_and_greater_than_half_monthly_income() {
        AnalysisModel analysisModel = analysisService.calculate(analysisRequestRequestedAmountLessThanMonthlyIncomeAndGreaterThanHalfMonthlyIncomeFactory());

        assertFalse(analysisModel.approved());
    }

    @Test
    void should_approve_credit_if_requested_amount_less_than_half_monthly_income() {
        AnalysisModel analysisModel = analysisService.calculate(analysisRequestRequestedAmountLessThanHalfMonthlyIncomeFactory());

        assertTrue(analysisModel.approved());
    }

    @Test
    void should_deny_credit_if_monthly_income_less_than_requested_amount_and_greater_than_half_monthly_income() {
        AnalysisModel analysisModel = analysisService.calculate(analysisRequestMonthlyIncomeLessThanRequestedAmountAndGreaterThanHalfMonthlyIncomeFactory());

        assertFalse(analysisModel.approved());
    }

    @Test
    void should_throw_client_not_found_exception_when_cpf_not_found() {
        when(clientApi.getClientBy(clientSearchCpfArgumentCaptor.capture())).thenThrow(FeignException.FeignClientException.class);
        assertThrows(ClientNotFoundException.class, () -> analysisService.getAll(clientSearchFactory().cpf()));
    }

    @Test
    void should_throw_illegal_argument_exception_when_invalid_search_parameter() {
        assertThrows(CustomIllegalArgumentException.class, () -> analysisService.getAll(invalidStringFactory()));
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

    public static AnalysisRequest invalidAnalysisRequestFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId("42e773f2-9693-4f25-a1c7-44579cd08c4")
                .build();
    }

    public static AnalysisRequest invalidAnalysisUUIDFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId("42e773f2-9693-4f25-a1c7-44579cd08c4")
                .build();
    }

    public static AnalysisRequest analysisRequestFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id().toString())
                .build();
    }

    public static AnalysisRequest analysisRequestRequestedAmountLessThanMonthlyIncomeAndLessThanHalfMonthlyIncomeFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id().toString())
                .build();
    }

    public static AnalysisRequest analysisRequestRequestedAmountLessThanMonthlyIncomeAndGreaterThanHalfMonthlyIncomeFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(6000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id().toString())
                .build();
    }

    public static AnalysisRequest analysisRequestRequestedAmountLessThanHalfMonthlyIncomeFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(4000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id().toString())
                .build();
    }

    public static AnalysisRequest analysisRequestRequestedAmountEqualToMonthlyIncomeFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(10000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id().toString())
                .build();
    }

    public static AnalysisRequest analysisRequestMonthlyIncomeGreaterThanMaxIncomeFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(100000))
                .clientId(clientSearchFactory().id().toString())
                .build();
    }

    public static AnalysisRequest analysisRequestMonthlyIncomeGreaterThanRequestedAmountFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(1000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id().toString())
                .build();
    }

    public static AnalysisRequest analysisRequestMonthlyIncomeLessThanRequestedAmountAndLessThanHalfMonthlyIncomeFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(10000))
                .monthlyIncome(BigDecimal.valueOf(1000))
                .clientId(clientSearchFactory().id().toString())
                .build();
    }

    public static AnalysisRequest analysisRequestMonthlyIncomeLessThanRequestedAmountAndGreaterThanHalfMonthlyIncomeFactory() {
        return AnalysisRequest.builder()
                .requestedAmount(BigDecimal.valueOf(6000))
                .monthlyIncome(BigDecimal.valueOf(10000))
                .clientId(clientSearchFactory().id().toString())
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