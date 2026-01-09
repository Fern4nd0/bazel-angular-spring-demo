package com.app.stack.users.infrastructure.persistence;

import com.app.stack.users.domain.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserPersistenceMapper {
    User toDomain(UserEntity entity);

    UserEntity toEntity(User user);
}
