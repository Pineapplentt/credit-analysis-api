package org.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.example.repository.entity.AnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity, UUID> {
    Optional<AnalysisEntity> findById(UUID id);

    List<AnalysisEntity> findByClientId(UUID id);
}
