package com.app.stack.users.infrastructure.persistence.repositories;

import com.app.stack.users.domain.entities.UserStatus;
import com.app.stack.users.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.jpa.domain.Specification;

final class UserSpecifications {
    private UserSpecifications() {
    }

    static Specification<UserEntity> hasStatus(UserStatus status) {
        if (status == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    static Specification<UserEntity> hasRole(String role) {
        if (role == null || role.isBlank()) {
            return null;
        }
        String normalized = role.trim().toLowerCase();
        return (root, query, cb) -> cb.equal(cb.lower(root.get("role")), normalized);
    }

    static Specification<UserEntity> matchesQuery(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase();
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("email")), "%" + normalized + "%"),
                cb.like(cb.lower(root.get("firstName")), "%" + normalized + "%"),
                cb.like(cb.lower(root.get("lastName")), "%" + normalized + "%")
        );
    }
}
