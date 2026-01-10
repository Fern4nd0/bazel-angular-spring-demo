package com.app.stack.users.application;

import com.app.stack.users.application.port.UserUseCasePort;
import com.app.stack.users.domain.entities.PageRequest;
import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.domain.entities.UserStatus;
import com.app.stack.users.domain.services.UserService;

public class UserUseCase implements UserUseCasePort {
    private final UserService service;

    public UserUseCase(UserService service) {
        this.service = service;
    }

    @Override
    public UserPage listUsers(PageRequest pageRequest, UserStatus status, String role) {
        return service.listUsers(pageRequest, status, role);
    }

    @Override
    public UserPage searchUsers(String query, PageRequest pageRequest) {
        return service.searchUsers(query, pageRequest);
    }

    @Override
    public User createUser(User create) {
        return service.createUser(create);
    }

    @Override
    public User getUser(Long id) {
        return service.getUser(id);
    }

    @Override
    public User updateUser(Long id, User update) {
        return service.updateUser(id, update);
    }

    @Override
    public void deleteUser(Long id) {
        service.deleteUser(id);
    }

}
