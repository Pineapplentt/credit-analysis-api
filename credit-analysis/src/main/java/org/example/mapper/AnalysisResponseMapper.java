package org.example.mapper;

import org.example.controller.response.AnalysisResponse;
import org.example.repository.Entity.AnalysisEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnalysisResponseMapper { //mapeia de uma entity para uma response
    AnalysisResponse from(AnalysisEntity analysisEntity);
}
