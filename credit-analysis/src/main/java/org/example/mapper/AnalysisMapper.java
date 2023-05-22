package org.example.mapper;

import org.example.controller.request.AnalysisRequest;
import org.example.model.AnalysisModel;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnalysisMapper { //Mapeia de uma request para uma model
    AnalysisModel from(AnalysisRequest analysisRequest);
}
