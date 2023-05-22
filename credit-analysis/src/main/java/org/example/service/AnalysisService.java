package org.example.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.example.controller.request.AnalysisRequest;
import org.example.controller.response.AnalysisResponse;
import org.example.credit.analysis.ClientApiClient;
import org.example.credit.analysis.dto.ClientSearch;
import org.example.exception.AnalysisNotFoundException;
import org.example.exception.ClientNotFoundException;
import org.example.exception.IllegalArgumentException;
import org.example.mapper.AnalysisEntityMapper;
import org.example.mapper.AnalysisMapper;
import org.example.mapper.AnalysisResponseMapper;
import org.example.model.AnalysisModel;
import org.example.repository.AnalysisRepository;
import org.example.repository.Entity.AnalysisEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final String teste = "teste";
    private final ClientApiClient clientApi;
    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;
    private final AnalysisResponseMapper analysisResponseMapper;
    private final AnalysisEntityMapper analysisEntityMapper;
    private final BigDecimal MAX_INCOME = BigDecimal.valueOf(50000.00);
    private final BigDecimal MONTHLY_INCOME_DIVIDE = BigDecimal.valueOf(2.0);
    private final BigDecimal ANNUAL_INTEREST = BigDecimal.valueOf(0.15);
    private final BigDecimal MONTHLY_INCOME_LESS_THAN_50_PERCENTAGE = BigDecimal.valueOf(0.30);
    private final BigDecimal WITHDRAW_VALUE = BigDecimal.valueOf(0.10);

    public List<AnalysisEntity> getAll(String param) {
        List<AnalysisEntity> analysisList;
        String regexCpf = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";
        String regexUuid = "[a-fA-F0-9]{8}(?:-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}";
        if (param.matches(regexCpf)) {
            try {
                ClientSearch client = clientApi.getClientByCpf(param);
                return this.analysisRepository.findByClientId(client.uuid());
            } catch (FeignException.FeignClientException exception) {
                throw new ClientNotFoundException("Cpf não encontrado");
            }
        }
        if (param.matches(regexUuid)) {
            UUID uuid = UUID.fromString(param);
            return this.analysisRepository.findByClientId(uuid);
        }
        throw new IllegalArgumentException("O parâmetro é inválido");
    }

    public AnalysisResponse createAnalysis(AnalysisRequest analysisRequest) { //Mapeia de uma request para uma response
        if (analysisRequest.clientId().toString().length() < 36) {
            throw new IllegalArgumentException("UUID inválido");
        }
        AnalysisEntity entity = analysisEntityMapper.from(calculate(analysisRequest));
        AnalysisEntity savedAnalysis = saveAnalysis(entity);
        return analysisResponseMapper.from(savedAnalysis);
    }

    public AnalysisModel calculate(AnalysisRequest analysisRequest) {
        boolean approved = false;
        BigDecimal approvedLimit = BigDecimal.valueOf(0.0);
        BigDecimal monthlyIncome = analysisRequest.monthlyIncome();
        BigDecimal requestedAmount = analysisRequest.requestedAmount();
        BigDecimal halfMonthlyIncome = monthlyIncome.divide(MONTHLY_INCOME_DIVIDE, 2, RoundingMode.HALF_UP);
        BigDecimal withdraw = BigDecimal.valueOf(0.0);

        if (monthlyIncome.compareTo(MAX_INCOME) > 0) {
            monthlyIncome = MAX_INCOME;
        }

        if (requestedAmount.compareTo(monthlyIncome) < 0
                &&
                (requestedAmount.compareTo(halfMonthlyIncome) < 0)) { //Requested amount is greater than half monthly income? y=1, n=-1
            approved = true;
            approvedLimit = monthlyIncome.multiply(MONTHLY_INCOME_LESS_THAN_50_PERCENTAGE);
            withdraw = approvedLimit.multiply(WITHDRAW_VALUE);

        }
        return AnalysisModel.builder()
                .clientId(analysisRequest.clientId())
                .approved(approved)
                .approvedLimit(approvedLimit)
                .withdraw(withdraw)
                .annualInterest(ANNUAL_INTEREST)
                .build();
    }

    public AnalysisEntity saveAnalysis(AnalysisEntity analysisEntity) {
        final AnalysisEntity savedAnalysis;
        try {
            if (hasClient(analysisEntity.getClientId())) {
                savedAnalysis = this.analysisRepository.save(analysisEntity);
                return savedAnalysis;
            }
        } catch (FeignException.FeignClientException exception) {
            throw new ClientNotFoundException("Client not found");
        }
        throw new ClientNotFoundException("Client not found");
    }

    public AnalysisResponse getAnalysisById(UUID id) {
        Optional<AnalysisEntity> entity = Optional.of(this.analysisRepository.findById(id)
                .orElseThrow(() -> new AnalysisNotFoundException("Analysis not found")));
        return analysisResponseMapper.from(entity.get());
    }

    public boolean hasClient(UUID id) {
        return clientApi.getClientExistsById(id);
    }
}
