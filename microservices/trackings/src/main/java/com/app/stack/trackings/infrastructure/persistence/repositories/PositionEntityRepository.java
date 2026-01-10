package com.app.stack.trackings.infrastructure.persistence.repositories;

import com.app.stack.trackings.infrastructure.persistence.entities.PositionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PositionEntityRepository extends JpaRepository<PositionEntity, String>, JpaSpecificationExecutor<PositionEntity> {
    Optional<PositionEntity> findTopByUserIdOrderByRecordedAtDesc(Long userId);
}
