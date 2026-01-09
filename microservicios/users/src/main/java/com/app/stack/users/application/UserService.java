package com.app.stack.users.application;

import com.app.stack.generated.model.Pagination;
import com.app.stack.generated.model.User;
import com.app.stack.generated.model.UserCreate;
import com.app.stack.generated.model.UserListResponse;
import com.app.stack.generated.model.UserStatus;
import com.app.stack.generated.model.UserUpdate;
import com.app.stack.users.domain.UserRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Pageable pageable = buildPageable(page, pageSize, sort);
        Page<User> result = repository.findAll(pageable, status, role);
        return toListResponse(result, normalizePage(page), normalizePageSize(pageSize));
    }

    public UserListResponse searchUsers(
            String query,
            Integer page,
            Integer pageSize,
            String sort) {
        if (query == null || query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing search text.");
        }
        Pageable pageable = buildPageable(page, pageSize, sort);
        Page<User> result = repository.search(query.trim(), pageable);
        return toListResponse(result, normalizePage(page), normalizePageSize(pageSize));
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

    public User getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    public User updateUser(Long id, UserUpdate update) {
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

    public void deleteUser(Long id) {
        if (!repository.deleteById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
    }

    private UserListResponse toListResponse(Page<User> page, int requestedPage, int requestedPageSize) {
        int totalPages = Math.max(1, page.getTotalPages());
        Pagination pagination = new Pagination(requestedPage, requestedPageSize, (int) page.getTotalElements(), totalPages);
        return new UserListResponse(page.getContent(), pagination);
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        return value;
    }

    private Pageable buildPageable(Integer page, Integer pageSize, String sort) {
        int normalizedPage = normalizePage(page) - 1;
        int normalizedPageSize = normalizePageSize(pageSize);
        Sort sortSpec = toSort(sort);
        return PageRequest.of(normalizedPage, normalizedPageSize, sortSpec);
    }

    private Sort toSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(":", 2);
        String field = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        switch (field) {
            case "createdAt":
            case "email":
            case "firstName":
            case "lastName":
                return Sort.by(direction, field);
            default:
                return Sort.unsorted();
        }
    }
}
