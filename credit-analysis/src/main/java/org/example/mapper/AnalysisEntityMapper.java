package org.example.mapper;

import org.example.model.AnalysisModel;
import org.example.repository.entity.AnalysisEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnalysisEntityMapper { //Mapeia de uma model para uma entity
    AnalysisEntity from(AnalysisModel analysisModel);
}
