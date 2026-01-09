package com.app.stack.users.application.port;

import com.app.stack.users.domain.entities.PageRequest;
import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.domain.entities.UserStatus;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    UserPage findUsers(PageRequest pageRequest, UserStatus status, String role);

    UserPage searchUsers(String query, PageRequest pageRequest);

    User save(User user);

    boolean deleteById(Long id);
}
