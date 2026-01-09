package com.app.stack.users.infrastructure.persistence.mappers;

import com.app.stack.users.domain.entities.User;
import com.app.stack.users.infrastructure.persistence.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserPersistenceMapper {
    User toDomain(UserEntity entity);

    UserEntity toEntity(User user);
}
