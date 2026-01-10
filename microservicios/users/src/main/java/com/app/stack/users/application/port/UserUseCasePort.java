package com.app.stack.users.application.port;

import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.domain.entities.UserStatus;

public interface UserUseCasePort {
    UserPage listUsers(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role);

    UserPage searchUsers(
            String query,
            Integer page,
            Integer pageSize,
            String sort);

    User createUser(User create);

    User getUser(Long id);

    User updateUser(Long id, User update);

    void deleteUser(Long id);
}
