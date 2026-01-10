package com.app.stack.trackings.infrastructure.web.config;

import com.app.stack.trackings.application.PositionUseCase;
import com.app.stack.trackings.application.port.PositionUseCasePort;
import com.app.stack.trackings.domain.port.PositionRepository;
import com.app.stack.trackings.domain.services.PositionService;
import com.app.stack.trackings.infrastructure.persistence.entities.PositionEntity;
import com.app.stack.trackings.infrastructure.persistence.mappers.PositionPersistenceMapper;
import com.app.stack.trackings.infrastructure.persistence.repositories.JpaPositionRepositoryAdapter;
import com.app.stack.trackings.infrastructure.persistence.repositories.PositionEntityRepository;
import com.app.stack.trackings.infrastructure.web.mappers.PositionApiMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = PositionEntityRepository.class)
@EntityScan(basePackageClasses = PositionEntity.class)
public class Config {
    @Bean
    public PositionUseCasePort positionUseCase(PositionService service) {
        return new PositionUseCase(service);
    }

    @Bean
    public PositionService positionService(PositionRepository repository) {
        return new PositionService(repository);
    }

    @Bean
    public PositionRepository positionRepository(PositionEntityRepository repository, PositionPersistenceMapper mapper) {
        return new JpaPositionRepositoryAdapter(repository, mapper);
    }

    @Bean
    public PositionPersistenceMapper positionPersistenceMapper() {
        return Mappers.getMapper(PositionPersistenceMapper.class);
    }

    @Bean
    public PositionApiMapper positionApiMapper() {
        return Mappers.getMapper(PositionApiMapper.class);
    }
}
