package org.example.mapper;

import javax.annotation.processing.Generated;
import org.example.controller.response.AnalysisResponse;
import org.example.repository.entity.AnalysisEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-01T21:39:02-0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Private Build)"
)
@Component
public class AnalysisResponseMapperImpl implements AnalysisResponseMapper {

    @Override
    public AnalysisResponse from(AnalysisEntity analysisEntity) {
        if ( analysisEntity == null ) {
            return null;
        }

        AnalysisResponse.AnalysisResponseBuilder analysisResponse = AnalysisResponse.builder();

        analysisResponse.idAnalysis( analysisEntity.getIdAnalysis() );
        analysisResponse.approved( analysisEntity.isApproved() );
        analysisResponse.approvedLimit( analysisEntity.getApprovedLimit() );
        analysisResponse.withdraw( analysisEntity.getWithdraw() );
        analysisResponse.annualInterest( analysisEntity.getAnnualInterest() );
        analysisResponse.clientId( analysisEntity.getClientId() );

        return analysisResponse.build();
    }
}
