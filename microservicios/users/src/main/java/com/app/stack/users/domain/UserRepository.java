package com.app.stack.users.domain;

import com.app.stack.generated.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User save(User user);

    boolean deleteById(String id);
}
