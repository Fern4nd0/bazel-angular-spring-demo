package com.example.demo.users;

import com.example.demo.generated.model.User;
import com.example.demo.generated.model.UserCreate;
import com.example.demo.generated.model.UserStatus;
import com.example.demo.generated.model.UserUpdate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserStore {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailToId = new ConcurrentHashMap<>();

    public UserStore() {
        seed("ana@company.com", "Ana", "Garcia", "user");
        seed("admin@company.com", "Admin", "Root", "admin");
    }

    public User create(UserCreate create) {
        if (create == null || isBlank(create.getEmail())
                || isBlank(create.getFirstName()) || isBlank(create.getLastName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields.");
        }
        String emailKey = create.getEmail().trim().toLowerCase();
        if (emailToId.containsKey(emailKey)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
        }

        User user = new User();
        user.setId("usr_" + UUID.randomUUID().toString().replace("-", ""));
        user.setEmail(create.getEmail().trim());
        user.setFirstName(create.getFirstName().trim());
        user.setLastName(create.getLastName().trim());
        user.setRole(isBlank(create.getRole()) ? "user" : create.getRole().trim());
        user.setStatus(UserStatus.ACTIVE);
        user.setPhone(normalizeNullable(create.getPhone()));
        user.setMetadata(create.getMetadata());
        OffsetDateTime now = OffsetDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        users.put(user.getId(), user);
        emailToId.put(emailKey, user.getId());
        return user;
    }

    public User findById(String id) {
        User user = users.get(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        return user;
    }

    public List<User> list(String role, UserStatus status) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (role != null && !role.equalsIgnoreCase(user.getRole())) {
                continue;
            }
            if (status != null && status != user.getStatus()) {
                continue;
            }
            result.add(user);
        }
        return result;
    }

    public List<User> search(String query) {
        String normalized = query.toLowerCase();
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (containsIgnoreCase(user.getEmail(), normalized)
                    || containsIgnoreCase(user.getFirstName(), normalized)
                    || containsIgnoreCase(user.getLastName(), normalized)) {
                result.add(user);
            }
        }
        return result;
    }

    public User update(String id, UserUpdate update) {
        if (update == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing request body.");
        }
        User user = findById(id);
        if (!isBlank(update.getEmail())) {
            String emailKey = update.getEmail().trim().toLowerCase();
            String existingId = emailToId.get(emailKey);
            if (existingId != null && !existingId.equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
            }
            emailToId.remove(user.getEmail().toLowerCase());
            user.setEmail(update.getEmail().trim());
            emailToId.put(emailKey, id);
        }
        if (!isBlank(update.getFirstName())) {
            user.setFirstName(update.getFirstName().trim());
        }
        if (!isBlank(update.getLastName())) {
            user.setLastName(update.getLastName().trim());
        }
        if (!isBlank(update.getRole())) {
            user.setRole(update.getRole().trim());
        }
        if (update.getStatus() != null) {
            user.setStatus(update.getStatus());
        }
        if (update.getPhone() != null) {
            user.setPhone(normalizeNullable(update.getPhone()));
        }
        if (update.getMetadata() != null) {
            user.setMetadata(update.getMetadata());
        }
        user.setUpdatedAt(OffsetDateTime.now());
        return user;
    }

    public void delete(String id) {
        User user = users.remove(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
        emailToId.remove(user.getEmail().toLowerCase());
    }

    public List<User> sort(List<User> users, String sort) {
        if (sort == null || sort.isBlank()) {
            return users;
        }
        String[] parts = sort.split(":", 2);
        String field = parts[0].trim();
        boolean desc = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]);
        Comparator<User> comparator = comparatorFor(field);
        if (comparator == null) {
            return users;
        }
        users.sort(desc ? comparator.reversed() : comparator);
        return users;
    }

    private Comparator<User> comparatorFor(String field) {
        switch (field) {
            case "createdAt":
                return Comparator.comparing(User::getCreatedAt);
            case "email":
                return Comparator.comparing(User::getEmail, String.CASE_INSENSITIVE_ORDER);
            case "firstName":
                return Comparator.comparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER);
            case "lastName":
                return Comparator.comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER);
            default:
                return null;
        }
    }

    private void seed(String email, String firstName, String lastName, String role) {
        UserCreate create = new UserCreate();
        create.setEmail(email);
        create.setFirstName(firstName);
        create.setLastName(lastName);
        create.setRole(role);
        create(create);
    }

    private boolean containsIgnoreCase(String value, String query) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains(query);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        return value;
    }
}
