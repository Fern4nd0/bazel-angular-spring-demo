package com.app.stack.users.infrastructure;

import com.app.stack.generated.model.User;
import com.app.stack.generated.model.UserStatus;
import com.app.stack.users.domain.UserRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class JpaUserRepository implements UserRepository {
    private final UserEntityRepository repository;

    public JpaUserRepository(UserEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return repository.findByEmailIgnoreCase(email).map(this::toModel);
    }

    @Override
    public Page<User> findAll(Pageable pageable, UserStatus status, String role) {
        Specification<UserEntity> spec = Specification.where(null);
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (role != null && !role.isBlank()) {
            String normalized = role.trim().toLowerCase();
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("role")), normalized));
        }
        return repository.findAll(spec, pageable).map(this::toModel);
    }

    @Override
    public Page<User> search(String query, Pageable pageable) {
        String normalized = query == null ? "" : query.trim().toLowerCase();
        Specification<UserEntity> spec = (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("email")), "%" + normalized + "%"),
                cb.like(cb.lower(root.get("firstName")), "%" + normalized + "%"),
                cb.like(cb.lower(root.get("lastName")), "%" + normalized + "%")
        );
        return repository.findAll(spec, pageable).map(this::toModel);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = repository.save(entity);
        return toModel(saved);
    }

    @Override
    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    private User toModel(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setEmail(entity.getEmail());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setRole(entity.getRole());
        user.setStatus(entity.getStatus());
        user.setPhone(entity.getPhone());
        user.setMetadata(entity.getMetadata());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        return user;
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setRole(user.getRole());
        entity.setStatus(user.getStatus());
        entity.setPhone(user.getPhone());
        entity.setMetadata(user.getMetadata());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
}
