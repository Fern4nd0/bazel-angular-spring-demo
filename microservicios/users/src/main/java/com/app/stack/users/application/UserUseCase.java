package com.app.stack.users.application;

import com.app.stack.users.domain.PageRequest;
import com.app.stack.users.domain.SortDirection;
import com.app.stack.users.domain.User;
import com.app.stack.users.domain.UserPage;
import com.app.stack.users.domain.UserStatus;
import com.app.stack.users.domain.errors.DomainErrorCode;
import com.app.stack.users.domain.errors.DomainException;
import com.app.stack.users.application.port.UserRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

public class UserUseCase {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;
    private static final String DEFAULT_ROLE = "user";
    private static final String MISSING_REQUIRED_FIELDS = "Missing required fields.";
    private static final String MISSING_REQUEST_BODY = "Missing request body.";
    private static final String MISSING_SEARCH_TEXT = "Missing search text.";
    private static final String USER_NOT_FOUND = "User not found.";
    private static final String EMAIL_ALREADY_REGISTERED = "Email is already registered.";

    private final UserRepository repository;

    public UserUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public UserPage listUsers(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role) {
        PageRequest pageRequest = buildPageRequest(page, pageSize, sort);
        return repository.findUsers(pageRequest, status, role);
    }

    public UserPage searchUsers(
            String query,
            Integer page,
            Integer pageSize,
            String sort) {
        if (query == null || query.trim().isEmpty()) {
            throw new DomainException(DomainErrorCode.INVALID_SEARCH_QUERY, MISSING_SEARCH_TEXT);
        }
        PageRequest pageRequest = buildPageRequest(page, pageSize, sort);
        return repository.searchUsers(query.trim(), pageRequest);
    }

    public User createUser(User create) {
        if (create == null || isBlank(create.getEmail())
                || isBlank(create.getFirstName()) || isBlank(create.getLastName())) {
            throw new DomainException(DomainErrorCode.INVALID_USER_DATA, MISSING_REQUIRED_FIELDS);
        }
        if (create.getId() != null || create.getStatus() != null
                || create.getCreatedAt() != null || create.getUpdatedAt() != null) {
            throw new DomainException(DomainErrorCode.INVALID_USER_DATA, "Immutable fields are not allowed on create.");
        }
        String email = create.getEmail().trim();
        ensureEmailAvailable(email, null);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        User user = new User();
        user.setEmail(email);
        user.setFirstName(create.getFirstName().trim());
        user.setLastName(create.getLastName().trim());
        user.setRole(isBlank(create.getRole()) ? DEFAULT_ROLE : create.getRole().trim());
        user.setStatus(UserStatus.ACTIVE);
        user.setPhone(trimToNull(create.getPhone()));
        user.setMetadata(create.getMetadata());
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return repository.save(user);
    }

    public User getUser(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new DomainException(DomainErrorCode.USER_NOT_FOUND, USER_NOT_FOUND));
    }

    public User updateUser(Long id, User update) {
        if (update == null) {
            throw new DomainException(DomainErrorCode.INVALID_USER_DATA, MISSING_REQUEST_BODY);
        }
        if (update.getId() != null || update.getCreatedAt() != null || update.getUpdatedAt() != null) {
            throw new DomainException(DomainErrorCode.INVALID_USER_DATA, "Immutable fields are not allowed on update.");
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
            user.setPhone(trimToNull(update.getPhone()));
        }
        if (update.getMetadata() != null) {
            user.setMetadata(update.getMetadata());
        }
        user.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return repository.save(user);
    }

    public void deleteUser(Long id) {
        if (!repository.deleteById(id)) {
            throw new DomainException(DomainErrorCode.USER_NOT_FOUND, USER_NOT_FOUND);
        }
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
        Optional<User> existing = repository.findByEmail(email);
        if (existing.isPresent() && !existing.get().getId().equals(currentUserId)) {
            throw new DomainException(DomainErrorCode.EMAIL_ALREADY_USED, EMAIL_ALREADY_REGISTERED);
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

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private PageRequest buildPageRequest(Integer page, Integer pageSize, String sort) {
        PageRequest request = new PageRequest();
        request.setPage(normalizePage(page));
        request.setPageSize(normalizePageSize(pageSize));
        applySort(request, sort);
        return request;
    }

    private void applySort(PageRequest request, String sort) {
        if (sort == null || sort.isBlank()) {
            return;
        }
        String[] parts = sort.split(":", 2);
        String field = parts[0].trim();
        if (!isSortableField(field)) {
            return;
        }
        SortDirection direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                ? SortDirection.DESC
                : SortDirection.ASC;
        request.setSortField(field);
        request.setSortDirection(direction);
    }

    private boolean isSortableField(String field) {
        switch (field) {
            case "createdAt":
            case "email":
            case "firstName":
            case "lastName":
                return true;
            default:
                return false;
        }
    }
}
