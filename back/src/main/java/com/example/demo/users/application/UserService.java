package com.example.demo.users.application;

import com.example.demo.generated.model.Pagination;
import com.example.demo.generated.model.User;
import com.example.demo.generated.model.UserCreate;
import com.example.demo.generated.model.UserListResponse;
import com.example.demo.generated.model.UserStatus;
import com.example.demo.generated.model.UserUpdate;
import com.example.demo.users.domain.UserRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserListResponse listUsers(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role) {
        List<User> filtered = filterUsers(repository.findAll(), role, status);
        sortUsers(filtered, sort);
        return paginate(filtered, normalizePage(page), normalizePageSize(pageSize));
    }

    public UserListResponse searchUsers(
            String query,
            Integer page,
            Integer pageSize,
            String sort) {
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing search text.");
        }
        List<User> filtered = search(repository.findAll(), query.trim());
        sortUsers(filtered, sort);
        return paginate(filtered, normalizePage(page), normalizePageSize(pageSize));
    }

    public User createUser(UserCreate create) {
        if (create == null || isBlank(create.getEmail())
                || isBlank(create.getFirstName()) || isBlank(create.getLastName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields.");
        }
        String email = create.getEmail().trim();
        ensureEmailAvailable(email, null);

        OffsetDateTime now = OffsetDateTime.now();
        User user = new User();
        user.setId("usr_" + UUID.randomUUID().toString().replace("-", ""));
        user.setEmail(email);
        user.setFirstName(create.getFirstName().trim());
        user.setLastName(create.getLastName().trim());
        user.setRole(isBlank(create.getRole()) ? "user" : create.getRole().trim());
        user.setStatus(UserStatus.ACTIVE);
        user.setPhone(normalizeNullable(create.getPhone()));
        user.setMetadata(create.getMetadata());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return repository.save(user);
    }

    public User getUser(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    public User updateUser(String id, UserUpdate update) {
        if (update == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing request body.");
        }
        User user = getUser(id);
        if (!isBlank(update.getEmail())) {
            String email = update.getEmail().trim();
            ensureEmailAvailable(email, user.getId());
            user.setEmail(email);
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
        return repository.save(user);
    }

    public void deleteUser(String id) {
        if (!repository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
    }

    private List<User> filterUsers(List<User> users, String role, UserStatus status) {
        List<User> result = new ArrayList<>();
        for (User user : users) {
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

    private List<User> search(List<User> users, String query) {
        String normalized = query.toLowerCase();
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (containsIgnoreCase(user.getEmail(), normalized)
                    || containsIgnoreCase(user.getFirstName(), normalized)
                    || containsIgnoreCase(user.getLastName(), normalized)) {
                result.add(user);
            }
        }
        return result;
    }

    private void sortUsers(List<User> users, String sort) {
        if (sort == null || sort.isBlank()) {
            return;
        }
        String[] parts = sort.split(":", 2);
        String field = parts[0].trim();
        boolean desc = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]);
        Comparator<User> comparator = comparatorFor(field);
        if (comparator == null) {
            return;
        }
        users.sort(desc ? comparator.reversed() : comparator);
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

    private UserListResponse paginate(List<User> users, int page, int pageSize) {
        int totalItems = users.size();
        int totalPages = totalItems == 0 ? 1 : (int) Math.ceil(totalItems / (double) pageSize);
        int fromIndex = Math.min((page - 1) * pageSize, totalItems);
        int toIndex = Math.min(fromIndex + pageSize, totalItems);
        List<User> items = fromIndex >= toIndex
                ? Collections.emptyList()
                : users.subList(fromIndex, toIndex);
        Pagination pagination = new Pagination(page, pageSize, totalItems, totalPages);
        return new UserListResponse(items, pagination);
    }

    private void ensureEmailAvailable(String email, String currentUserId) {
        Optional<User> existing = repository.findByEmail(email);
        if (existing.isPresent() && !existing.get().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered.");
        }
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
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
