package com.app.stack.users.domain;

import com.app.stack.generated.model.User;
import com.app.stack.generated.model.UserStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository {
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable, UserStatus status, String role);

    Page<User> search(String query, Pageable pageable);

    User save(User user);

    boolean deleteById(Long id);
}
