package org.example.mapper;

import javax.annotation.processing.Generated;
import org.example.controller.request.AnalysisRequest;
import org.example.model.AnalysisModel;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-06-01T21:39:02-0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.7 (Private Build)"
)
@Component
public class AnalysisMapperImpl implements AnalysisMapper {

    @Override
    public AnalysisModel from(AnalysisRequest analysisRequest) {
        if ( analysisRequest == null ) {
            return null;
        }

        AnalysisModel.AnalysisModelBuilder analysisModel = AnalysisModel.builder();

        analysisModel.clientId( analysisRequest.clientId() );

        return analysisModel.build();
    }
}
