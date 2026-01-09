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
    public Page<User> findUsers(Pageable pageable, UserStatus status, String role) {
        Specification<UserEntity> spec = Specification
                .where(UserSpecifications.hasStatus(status))
                .and(UserSpecifications.hasRole(role));
        return repository.findAll(spec, pageable).map(this::toModel);
    }

    @Override
    public Page<User> searchUsers(String query, Pageable pageable) {
        Specification<UserEntity> spec = UserSpecifications.matchesQuery(query);
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
