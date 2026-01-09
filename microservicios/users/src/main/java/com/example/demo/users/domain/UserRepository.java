package com.example.demo.users.domain;

import com.example.demo.generated.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User save(User user);

    boolean deleteById(String id);
}
