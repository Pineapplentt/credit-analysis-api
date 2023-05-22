package org.example.repository;

import org.example.repository.Entity.AnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity, UUID> {
    Optional<AnalysisEntity> findById(UUID id);
    List<AnalysisEntity> findByClientId(UUID id);
}
