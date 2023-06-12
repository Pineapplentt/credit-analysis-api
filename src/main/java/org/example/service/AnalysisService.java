package org.example.service;

import feign.FeignException;
import feign.RetryableException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.example.controller.request.AnalysisRequest;
import org.example.controller.response.AnalysisResponse;
import org.example.credit.analysis.ClientApiClient;
import org.example.credit.analysis.dto.ClientSearch;
import org.example.exception.AnalysisNotFoundException;
import org.example.exception.ApiConnectionException;
import org.example.exception.ClientNotFoundException;
import org.example.exception.CustomIllegalArgumentException;
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
    static final BigDecimal MAX_INCOME = BigDecimal.valueOf(50000.00);
    private static final BigDecimal MONTHLY_INCOME_DIVIDE = BigDecimal.valueOf(2.0);
    private static final BigDecimal ANNUAL_INTEREST = BigDecimal.valueOf(0.15);
    private static final BigDecimal MONTHLY_INCOME_LESS_THAN_50_PERCENTAGE = BigDecimal.valueOf(0.30);
    private static final BigDecimal WITHDRAW_VALUE = BigDecimal.valueOf(0.10);
    private static final String REGEX_CPF = "\\d{3}(\\.?\\d{3}){2}-?\\d{2}";
    private static final String REGEX_UUID = "[a-fA-F0-9]{8}(?:-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}";
    private final Pattern patternCpf = Pattern.compile(REGEX_CPF);
    private final Pattern patternUuid = Pattern.compile(REGEX_UUID);

    public List<AnalysisEntity> getAll(String param) {
        // Concordo que usar o validator é o melhor dos casos para o cpf, mas a api não salva o cpf do cliente, só o id

        if (patternUuid.matcher(param).matches()) {
            final UUID uuid = UUID.fromString(param);
            return this.analysisRepository.findByClientId(uuid);
        }
        if (patternCpf.matcher(param).matches()) {
            try {
                final List<ClientSearch> client = clientApi.getClientBy(param);
                return this.analysisRepository.findByClientId(client.get(0).id());
            } catch (FeignException.FeignClientException exception) {
                throw new ClientNotFoundException("Cpf não encontrado");
            } catch (RetryableException exception) {
                throw new ApiConnectionException("Não foi possível a conexão com a api de clientes");
            }
        }
        throw new CustomIllegalArgumentException("O parâmetro é inválido");
    }

    public AnalysisResponse createAnalysis(AnalysisRequest analysisRequest) { //Mapeia de uma request para uma response
        final String clientId = analysisRequest.clientId();

        if (patternUuid.matcher(clientId).matches()) {
            final AnalysisEntity analysisEntity = analysisEntityMapper.from(calculate(analysisRequest));
            final AnalysisEntity savedAnalysis = saveAnalysis(analysisEntity);
            return analysisResponseMapper.from(savedAnalysis);
        }
        throw new CustomIllegalArgumentException("O parâmetro é inválido");
    }

    public AnalysisModel calculate(AnalysisRequest analysisRequest) {
        boolean approved = false;
        BigDecimal approvedLimit = BigDecimal.valueOf(0.0);
        BigDecimal monthlyIncome = analysisRequest.monthlyIncome();
        final BigDecimal requestedAmount = analysisRequest.requestedAmount();
        final BigDecimal halfMonthlyIncome = monthlyIncome.divide(MONTHLY_INCOME_DIVIDE, 2, RoundingMode.HALF_UP);
        BigDecimal withdraw = BigDecimal.valueOf(0.0);

        if (monthlyIncome.compareTo(MAX_INCOME) > 0) {
            monthlyIncome = MAX_INCOME;
        }

        //Requested amount is greater than half monthly income? y=1, n=-1
        if (requestedAmount.compareTo(monthlyIncome) < 0 && requestedAmount.compareTo(halfMonthlyIncome) < 0) {
            approved = true;
            approvedLimit = monthlyIncome.multiply(MONTHLY_INCOME_LESS_THAN_50_PERCENTAGE);
            withdraw = approvedLimit.multiply(WITHDRAW_VALUE);

        }
        return AnalysisModel.builder().clientId(analysisRequest.clientId()).approved(approved).approvedLimit(approvedLimit).withdraw(withdraw)
                .annualInterest(ANNUAL_INTEREST).build();
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
