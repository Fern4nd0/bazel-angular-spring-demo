package com.app.stack.users.infrastructure.web;

import com.app.stack.generated.api.UsersApi;
import com.app.stack.generated.model.User;
import com.app.stack.generated.model.UserCreate;
import com.app.stack.generated.model.UserListResponse;
import com.app.stack.generated.model.UserStatus;
import com.app.stack.generated.model.UserUpdate;
import com.app.stack.users.application.UserUseCase;
import com.app.stack.users.domain.UserPage;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UsersApi {
    private final UserUseCase service;
    private final UserApiMapper mapper;

    public UserController(UserUseCase service, UserApiMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public UserListResponse usersGet(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role) {
        UserPage result = service.listUsers(page, pageSize, sort, mapper.toDomain(status), role);
        return mapper.toApi(result);
    }

    @Override
    public User usersPost(UserCreate userCreate) {
        return mapper.toApi(service.createUser(mapper.toDomain(userCreate)));
    }

    @Override
    public UserListResponse usersSearchGet(
            String q,
            Integer page,
            Integer pageSize,
            String sort) {
        UserPage result = service.searchUsers(q, page, pageSize, sort);
        return mapper.toApi(result);
    }

    @Override
    public User usersUserIdGet(Long userId) {
        return mapper.toApi(service.getUser(userId));
    }

    @Override
    public User usersUserIdPatch(Long userId, UserUpdate userUpdate) {
        return mapper.toApi(service.updateUser(userId, mapper.toDomain(userUpdate)));
    }

    @Override
    public void usersUserIdDelete(Long userId) {
        service.deleteUser(userId);
    }
}
