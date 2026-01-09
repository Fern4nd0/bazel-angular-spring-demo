package com.app.stack.users.application;

import com.app.stack.users.domain.port.UserRepository;
import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.domain.entities.UserStatus;
import com.app.stack.users.domain.services.UserService;

public class UserUseCase {
    private final UserService service;

    public UserUseCase(UserRepository repository) {
        this.service = new UserService(repository);
    }

    public UserPage listUsers(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role) {
        return service.listUsers(page, pageSize, sort, status, role);
    }

    public UserPage searchUsers(
            String query,
            Integer page,
            Integer pageSize,
            String sort) {
        return service.searchUsers(query, page, pageSize, sort);
    }

    public User createUser(User create) {
        return service.createUser(create);
    }

    public User getUser(Long id) {
        return service.getUser(id);
    }

    public User updateUser(Long id, User update) {
        return service.updateUser(id, update);
    }

    public void deleteUser(Long id) {
        service.deleteUser(id);
    }
}
