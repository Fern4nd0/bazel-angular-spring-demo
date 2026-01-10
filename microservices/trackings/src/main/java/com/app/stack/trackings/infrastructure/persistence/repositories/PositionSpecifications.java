package com.app.stack.trackings.infrastructure.persistence.repositories;

import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.infrastructure.persistence.entities.PositionEntity;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class PositionSpecifications {
    private PositionSpecifications() {}

    public static Specification<PositionEntity> hasUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return null;
            }
            return cb.equal(root.get("userId"), userId);
        };
    }

    public static Specification<PositionEntity> hasUserIds(List<Long> userIds) {
        return (root, query, cb) -> {
            if (userIds == null || userIds.isEmpty()) {
                return null;
            }
            return root.get("userId").in(userIds);
        };
    }

    public static Specification<PositionEntity> recordedAfter(OffsetDateTime recordedAfter) {
        return (root, query, cb) -> {
            if (recordedAfter == null) {
                return null;
            }
            return cb.greaterThan(root.get("recordedAt"), recordedAfter);
        };
    }

    public static Specification<PositionEntity> recordedFrom(OffsetDateTime from) {
        return (root, query, cb) -> {
            if (from == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get("recordedAt"), from);
        };
    }

    public static Specification<PositionEntity> recordedTo(OffsetDateTime to) {
        return (root, query, cb) -> {
            if (to == null) {
                return null;
            }
            return cb.lessThan(root.get("recordedAt"), to);
        };
    }

    public static Specification<PositionEntity> withinBoundingBox(BoundingBox bbox) {
        return (root, query, cb) -> {
            if (bbox == null || bbox.getWest() == null || bbox.getSouth() == null
                    || bbox.getEast() == null || bbox.getNorth() == null) {
                return null;
            }
            return cb.and(
                    cb.between(root.get("longitude"), bbox.getWest(), bbox.getEast()),
                    cb.between(root.get("latitude"), bbox.getSouth(), bbox.getNorth())
            );
        };
    }
}
