package com.app.stack.adapters.web;

import com.app.stack.generated.api.UsersApi;
import com.app.stack.generated.model.User;
import com.app.stack.generated.model.UserCreate;
import com.app.stack.generated.model.UserListResponse;
import com.app.stack.generated.model.UserStatus;
import com.app.stack.generated.model.UserUpdate;
import com.app.stack.users.application.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UsersApi {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Override
    public UserListResponse usersGet(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role) {
        return service.listUsers(page, pageSize, sort, status, role);
    }

    @Override
    public User usersPost(UserCreate userCreate) {
        return service.createUser(userCreate);
    }

    @Override
    public UserListResponse usersSearchGet(
            String q,
            Integer page,
            Integer pageSize,
            String sort) {
        return service.searchUsers(q, page, pageSize, sort);
    }

    @Override
    public User usersUserIdGet(String userId) {
        return service.getUser(userId);
    }

    @Override
    public User usersUserIdPatch(String userId, UserUpdate userUpdate) {
        return service.updateUser(userId, userUpdate);
    }

    @Override
    public void usersUserIdDelete(String userId) {
        service.deleteUser(userId);
    }
}
