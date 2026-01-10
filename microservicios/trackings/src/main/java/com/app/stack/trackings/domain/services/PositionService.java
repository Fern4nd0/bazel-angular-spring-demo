package com.app.stack.trackings.domain.services;

import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.domain.entities.GeoPoint;
import com.app.stack.trackings.domain.entities.PageRequest;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import com.app.stack.trackings.domain.entities.PositionSearchCriteria;
import com.app.stack.trackings.domain.entities.PositionSource;
import com.app.stack.trackings.domain.errors.DomainErrorCode;
import com.app.stack.trackings.domain.errors.DomainException;
import com.app.stack.trackings.domain.port.PositionRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class PositionService {
    private static final String MISSING_REQUIRED_FIELDS = "Missing required fields.";
    private static final String MISSING_REQUEST_BODY = "Missing request body.";
    private static final String POSITION_NOT_FOUND = "Position not found.";

    private final PositionRepository repository;

    public PositionService(PositionRepository repository) {
        this.repository = repository;
    }

    public Position createPosition(Position create) {
        if (create == null) {
            throw new DomainException(DomainErrorCode.INVALID_POSITION_DATA, MISSING_REQUEST_BODY);
        }
        if (create.getId() != null || create.getReceivedAt() != null) {
            throw new DomainException(DomainErrorCode.INVALID_POSITION_DATA, "Immutable fields are not allowed on create.");
        }
        if (isBlank(create.getUserId()) || create.getPoint() == null || create.getRecordedAt() == null) {
            throw new DomainException(DomainErrorCode.INVALID_POSITION_DATA, MISSING_REQUIRED_FIELDS);
        }
        GeoPoint point = create.getPoint();
        if (point.getLatitude() == null || point.getLongitude() == null) {
            throw new DomainException(DomainErrorCode.INVALID_POSITION_DATA, MISSING_REQUIRED_FIELDS);
        }

        Position position = new Position();
        position.setId(generateId());
        position.setUserId(create.getUserId().trim());
        position.setPoint(point);
        position.setSource(create.getSource() == null ? PositionSource.UNKNOWN : create.getSource());
        position.setRecordedAt(create.getRecordedAt());
        position.setReceivedAt(OffsetDateTime.now(ZoneOffset.UTC));
        position.setMetadata(create.getMetadata());

        return repository.save(position);
    }

    public Position getPosition(String id) {
        if (isBlank(id)) {
            throw new DomainException(DomainErrorCode.INVALID_POSITION_DATA, MISSING_REQUIRED_FIELDS);
        }
        return repository.findById(id)
                .orElseThrow(() -> new DomainException(DomainErrorCode.POSITION_NOT_FOUND, POSITION_NOT_FOUND));
    }

    public Position getLatestPosition(String userId) {
        if (isBlank(userId)) {
            throw new DomainException(DomainErrorCode.INVALID_POSITION_DATA, MISSING_REQUIRED_FIELDS);
        }
        return repository.findLatestByUserId(userId.trim())
                .orElseThrow(() -> new DomainException(DomainErrorCode.POSITION_NOT_FOUND, POSITION_NOT_FOUND));
    }

    public PositionPage listLatestPositions(
            PageRequest pageRequest,
            String userId,
            OffsetDateTime recordedAfter,
            BoundingBox bbox) {
        return repository.findLatestPositions(pageRequest, trimToNull(userId), recordedAfter, bbox);
    }

    public PositionPage listUserHistory(
            String userId,
            PageRequest pageRequest,
            OffsetDateTime from,
            OffsetDateTime to) {
        if (isBlank(userId)) {
            throw new DomainException(DomainErrorCode.INVALID_SEARCH_QUERY, MISSING_REQUIRED_FIELDS);
        }
        return repository.findUserHistory(userId.trim(), pageRequest, from, to);
    }

    public PositionPage searchPositions(PositionSearchCriteria criteria) {
        if (criteria == null) {
            throw new DomainException(DomainErrorCode.INVALID_SEARCH_QUERY, MISSING_REQUEST_BODY);
        }
        if (criteria.getPageRequest() == null) {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(1);
            pageRequest.setPageSize(25);
            criteria.setPageRequest(pageRequest);
        }
        return repository.searchPositions(criteria);
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

    private String generateId() {
        return "pos_" + UUID.randomUUID().toString().replace("-", "");
    }
}
