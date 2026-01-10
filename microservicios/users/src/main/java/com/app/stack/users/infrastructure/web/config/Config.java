package com.app.stack.users.infrastructure.web.config;

import com.app.stack.users.infrastructure.web.mappers.UserApiMapper;
import com.app.stack.users.infrastructure.persistence.entities.UserEntity;
import com.app.stack.users.infrastructure.persistence.mappers.UserPersistenceMapper;
import com.app.stack.users.infrastructure.persistence.repositories.JpaUserRepositoryAdapter;
import com.app.stack.users.infrastructure.persistence.repositories.UserEntityRepository;
import com.app.stack.users.application.UserUseCase;
import com.app.stack.users.application.port.UserUseCasePort;
import com.app.stack.users.domain.port.UserRepository;
import com.app.stack.users.domain.services.UserService;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = UserEntityRepository.class)
@EntityScan(basePackageClasses = UserEntity.class)
public class Config {
    @Bean
    public UserUseCasePort userUseCase(UserService service) {
        return new UserUseCase(service);
    }

    @Bean
    public UserService userService(UserRepository repository) {
        return new UserService(repository);
    }

    @Bean
    public UserRepository userRepository(UserEntityRepository repository, UserPersistenceMapper mapper) {
        return new JpaUserRepositoryAdapter(repository, mapper);
    }

    @Bean
    public UserPersistenceMapper userPersistenceMapper() {
        return Mappers.getMapper(UserPersistenceMapper.class);
    }

    @Bean
    public UserApiMapper userApiMapper() {
        return Mappers.getMapper(UserApiMapper.class);
    }
}
