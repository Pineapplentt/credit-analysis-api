package org.example.service;

import feign.FeignException;
import feign.RetryableException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.controller.request.AnalysisRequest;
import org.example.controller.response.AnalysisResponse;
import org.example.credit.analysis.ClientApiClient;
import org.example.credit.analysis.dto.ClientSearch;
import org.example.exception.AnalysisNotFoundException;
import org.example.exception.ApiConnectionException;
import org.example.exception.ClientNotFoundException;
import org.example.exception.IllegalArgumentException;
import org.example.mapper.AnalysisEntityMapper;
import org.example.mapper.AnalysisMapper;
import org.example.mapper.AnalysisResponseMapper;
import org.example.model.AnalysisModel;
import org.example.repository.AnalysisRepository;
import org.example.repository.entity.AnalysisEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final ClientApiClient clientApi;
    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;
    private final AnalysisResponseMapper analysisResponseMapper;
    private final AnalysisEntityMapper analysisEntityMapper;
    private final BigDecimal maxIncome = BigDecimal.valueOf(50000.00);
    private final BigDecimal monthlyIncomeDivide = BigDecimal.valueOf(2.0);
    private final BigDecimal annualInterest = BigDecimal.valueOf(0.15);
    private final BigDecimal monthlyIncomeLessThan50Percentage = BigDecimal.valueOf(0.30);
    private final BigDecimal withdrawValue = BigDecimal.valueOf(0.10);

    public List<AnalysisEntity> getAll(String param) {
        // Concordo que usar o validator é o melhor dos casos para o cpf, mas a api não salva o cpf do cliente, só o id
        final String regexCpf = "\\d{3}(\\.?\\d{3}){2}-?\\d{2}";
        final String regexUuid = "[a-fA-F0-9]{8}(?:-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}";

        if (param.matches(regexUuid)) {
            final UUID uuid = UUID.fromString(param);
            return this.analysisRepository.findByClientId(uuid);
        }
        if (param.matches(regexCpf)) {
            try {
                final List<ClientSearch> client = clientApi.getClientBy(param);
                return this.analysisRepository.findByClientId(client.get(0).id());
            } catch (FeignException.FeignClientException exception) {
                throw new ClientNotFoundException("Cpf não encontrado");
            } catch (RetryableException exception) {
                throw new ApiConnectionException("Não foi possível a conexão com a api de clientes");
            }
        }
        throw new IllegalArgumentException("O parâmetro é inválido");
    }

    public AnalysisResponse createAnalysis(AnalysisRequest analysisRequest) { //Mapeia de uma request para uma response
        final String regexUuid = "[a-fA-F0-9]{8}(?:-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}";
        if (analysisRequest.clientId().toString().matches(regexUuid)) {
            final AnalysisEntity entity = analysisEntityMapper.from(calculate(analysisRequest));
            final AnalysisEntity savedAnalysis = saveAnalysis(entity);
            return analysisResponseMapper.from(savedAnalysis);
        }
        throw new IllegalArgumentException("UUID inválido");
    }

    public AnalysisModel calculate(AnalysisRequest analysisRequest) {
        boolean approved = false;
        BigDecimal approvedLimit = BigDecimal.valueOf(0.0);
        BigDecimal monthlyIncome = analysisRequest.monthlyIncome();
        final BigDecimal requestedAmount = analysisRequest.requestedAmount();
        final BigDecimal halfMonthlyIncome = monthlyIncome.divide(monthlyIncomeDivide, 2, RoundingMode.HALF_UP);
        BigDecimal withdraw = BigDecimal.valueOf(0.0);

        if (monthlyIncome.compareTo(maxIncome) > 0) {
            monthlyIncome = maxIncome;
        }

        //Requested amount is greater than half monthly income? y=1, n=-1
        if (requestedAmount.compareTo(monthlyIncome) < 0 && requestedAmount.compareTo(halfMonthlyIncome) < 0) {
            approved = true;
            approvedLimit = monthlyIncome.multiply(monthlyIncomeLessThan50Percentage);
            withdraw = approvedLimit.multiply(withdrawValue);

        }
        return AnalysisModel.builder().clientId(analysisRequest.clientId()).approved(approved).approvedLimit(approvedLimit).withdraw(withdraw)
                .annualInterest(annualInterest).build();
    }

    public AnalysisEntity saveAnalysis(AnalysisEntity analysisEntity) {
        final AnalysisEntity savedAnalysis;
        try {
            getClient(analysisEntity.getClientId());
            savedAnalysis = this.analysisRepository.save(analysisEntity);
            return savedAnalysis;
        } catch (FeignException exception) {
            throw new ClientNotFoundException("Client not found");
        }
    }

    public AnalysisResponse getAnalysisById(UUID id) {
        final AnalysisEntity entity = this.analysisRepository.findById(id).orElseThrow(() -> new AnalysisNotFoundException("Analysis not found"));
        return analysisResponseMapper.from(entity);
    }

    public ClientSearch getClient(UUID id) {
        return clientApi.getClientById(id);
    }
}
