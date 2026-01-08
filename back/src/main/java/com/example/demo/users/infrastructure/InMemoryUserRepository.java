package com.example.demo.users.infrastructure;

import com.example.demo.generated.model.User;
import com.example.demo.generated.model.UserStatus;
import com.example.demo.users.domain.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailToId = new ConcurrentHashMap<>();

    public InMemoryUserRepository() {
        seed("ana@company.com", "Ana", "Garcia", "user");
        seed("admin@company.com", "Admin", "Root", "admin");
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        String id = emailToId.get(email.toLowerCase());
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId("usr_" + UUID.randomUUID().toString().replace("-", ""));
        }
        User previous = users.put(user.getId(), user);
        if (previous != null && previous.getEmail() != null) {
            emailToId.remove(previous.getEmail().toLowerCase());
        }
        if (user.getEmail() != null) {
            emailToId.put(user.getEmail().toLowerCase(), user.getId());
        }
        return user;
    }

    @Override
    public boolean deleteById(String id) {
        User user = users.remove(id);
        if (user == null) {
            return false;
        }
        if (user.getEmail() != null) {
            emailToId.remove(user.getEmail().toLowerCase());
        }
        return true;
    }

    private void seed(String email, String firstName, String lastName, String role) {
        User user = new User();
        user.setId("usr_" + UUID.randomUUID().toString().replace("-", ""));
        user.setEmail(email.trim());
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setRole(role.trim());
        user.setStatus(UserStatus.ACTIVE);
        OffsetDateTime now = OffsetDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        save(user);
    }
}
