package org.example.mapper;

import javax.annotation.processing.Generated;
import org.example.model.AnalysisModel;
import org.example.repository.entity.AnalysisEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-01T21:39:02-0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Private Build)"
)
@Component
public class AnalysisEntityMapperImpl implements AnalysisEntityMapper {

    @Override
    public AnalysisEntity from(AnalysisModel analysisModel) {
        if ( analysisModel == null ) {
            return null;
        }

        AnalysisEntity.AnalysisEntityBuilder analysisEntity = AnalysisEntity.builder();

        analysisEntity.clientId( analysisModel.clientId() );
        analysisEntity.approved( analysisModel.approved() );
        analysisEntity.approvedLimit( analysisModel.approvedLimit() );
        analysisEntity.withdraw( analysisModel.withdraw() );
        analysisEntity.annualInterest( analysisModel.annualInterest() );

        return analysisEntity.build();
    }
}
