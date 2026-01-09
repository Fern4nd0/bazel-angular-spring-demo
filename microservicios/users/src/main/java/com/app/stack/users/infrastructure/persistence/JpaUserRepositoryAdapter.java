package com.app.stack.users.infrastructure.persistence;

import com.app.stack.users.domain.entities.PageRequest;
import com.app.stack.users.domain.entities.SortDirection;
import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.domain.entities.UserStatus;
import com.app.stack.users.application.port.UserRepository;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class JpaUserRepositoryAdapter implements UserRepository {
    private final UserEntityRepository repository;
    private final UserPersistenceMapper mapper;

    public JpaUserRepositoryAdapter(UserEntityRepository repository, UserPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(Long id) {
        return execute("Failed to load user by id.", () -> repository.findById(id).map(mapper::toDomain));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return execute("Failed to load user by email.", () -> repository.findByEmailIgnoreCase(email).map(mapper::toDomain));
    }

    @Override
    public UserPage findUsers(PageRequest pageRequest, UserStatus status, String role) {
        Specification<UserEntity> spec = Specification
                .where(UserSpecifications.hasStatus(status))
                .and(UserSpecifications.hasRole(role));
        Page<User> page = execute("Failed to list users.", () -> repository.findAll(spec, toPageable(pageRequest)).map(mapper::toDomain));
        return toUserPage(page);
    }

    @Override
    public UserPage searchUsers(String query, PageRequest pageRequest) {
        Specification<UserEntity> spec = UserSpecifications.matchesQuery(query);
        Page<User> page = execute("Failed to search users.", () -> repository.findAll(spec, toPageable(pageRequest)).map(mapper::toDomain));
        return toUserPage(page);
    }

    @Override
    public User save(User user) {
        return execute("Failed to save user.", () -> {
            UserEntity entity = mapper.toEntity(user);
            UserEntity saved = repository.save(entity);
            return mapper.toDomain(saved);
        });
    }

    @Override
    public boolean deleteById(Long id) {
        return execute("Failed to delete user.", () -> {
            if (!repository.existsById(id)) {
                return false;
            }
            repository.deleteById(id);
            return true;
        });
    }

    private Pageable toPageable(PageRequest pageRequest) {
        int page = Math.max(0, pageRequest.getPage() - 1);
        int pageSize = Math.max(1, pageRequest.getPageSize());
        Sort sort = toSort(pageRequest);
        return org.springframework.data.domain.PageRequest.of(page, pageSize, sort);
    }

    private Sort toSort(PageRequest pageRequest) {
        if (pageRequest.getSortField() == null || pageRequest.getSortField().isBlank()) {
            return Sort.unsorted();
        }
        Sort.Direction direction = pageRequest.getSortDirection() == SortDirection.DESC
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        switch (pageRequest.getSortField()) {
            case "createdAt":
            case "email":
            case "firstName":
            case "lastName":
                return Sort.by(direction, pageRequest.getSortField());
            default:
                return Sort.unsorted();
        }
    }

    private UserPage toUserPage(Page<User> page) {
        UserPage result = new UserPage();
        result.setItems(page.getContent());
        result.setPage(page.getNumber() + 1);
        result.setPageSize(page.getSize());
        result.setTotalItems((int) page.getTotalElements());
        result.setTotalPages(Math.max(1, page.getTotalPages()));
        return result;
    }

    private <T> T execute(String message, Supplier<T> action) {
        try {
            return action.get();
        } catch (DataAccessException ex) {
            throw new UserPersistenceException(message, ex);
        }
    }
}
