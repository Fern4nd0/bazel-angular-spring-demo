package com.example.demo.adapters.web;

import com.example.demo.generated.api.UsersApi;
import com.example.demo.generated.model.User;
import com.example.demo.generated.model.UserCreate;
import com.example.demo.generated.model.UserListResponse;
import com.example.demo.generated.model.UserStatus;
import com.example.demo.generated.model.UserUpdate;
import com.example.demo.users.application.UserService;
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
