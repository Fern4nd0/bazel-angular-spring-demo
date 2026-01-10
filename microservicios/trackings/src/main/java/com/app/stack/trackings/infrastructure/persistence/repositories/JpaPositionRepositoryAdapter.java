package com.app.stack.trackings.infrastructure.persistence.repositories;

import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.domain.entities.PageRequest;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import com.app.stack.trackings.domain.entities.PositionSearchCriteria;
import com.app.stack.trackings.domain.entities.SortDirection;
import com.app.stack.trackings.domain.errors.DomainErrorCode;
import com.app.stack.trackings.domain.errors.DomainException;
import com.app.stack.trackings.domain.port.PositionRepository;
import com.app.stack.trackings.infrastructure.persistence.entities.PositionEntity;
import com.app.stack.trackings.infrastructure.persistence.mappers.PositionPersistenceMapper;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class JpaPositionRepositoryAdapter implements PositionRepository {
    private final PositionEntityRepository repository;
    private final PositionPersistenceMapper mapper;

    public JpaPositionRepositoryAdapter(PositionEntityRepository repository, PositionPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Position> findById(String id) {
        return execute("Failed to load position by id.", () -> repository.findById(id).map(mapper::toDomain));
    }

    @Override
    public Optional<Position> findLatestByUserId(String userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return execute("Failed to load latest position.",
                () -> repository.findTopByUserIdOrderByRecordedAtDesc(userId).map(mapper::toDomain));
    }

    @Override
    public PositionPage findLatestPositions(PageRequest pageRequest, String userId, OffsetDateTime recordedAfter, BoundingBox bbox) {
        Specification<PositionEntity> spec = Specification
                .where(PositionSpecifications.hasUserId(userId))
                .and(PositionSpecifications.recordedAfter(recordedAfter))
                .and(PositionSpecifications.withinBoundingBox(bbox));
        Page<Position> page = execute("Failed to list positions.",
                () -> repository.findAll(spec, toPageable(pageRequest)).map(mapper::toDomain));
        return toPositionPage(page);
    }

    @Override
    public PositionPage findUserHistory(String userId, PageRequest pageRequest, OffsetDateTime from, OffsetDateTime to) {
        Specification<PositionEntity> spec = Specification
                .where(PositionSpecifications.hasUserId(userId))
                .and(PositionSpecifications.recordedFrom(from))
                .and(PositionSpecifications.recordedTo(to));
        Page<Position> page = execute("Failed to list user positions.",
                () -> repository.findAll(spec, toPageable(pageRequest)).map(mapper::toDomain));
        return toPositionPage(page);
    }

    @Override
    public PositionPage searchPositions(PositionSearchCriteria criteria) {
        Specification<PositionEntity> spec = Specification
                .where(PositionSpecifications.hasUserIds(criteria.getUserIds()))
                .and(PositionSpecifications.recordedFrom(criteria.getFrom()))
                .and(PositionSpecifications.recordedTo(criteria.getTo()))
                .and(PositionSpecifications.withinBoundingBox(criteria.getBbox()));
        Page<Position> page = execute("Failed to search positions.",
                () -> repository.findAll(spec, toPageable(criteria.getPageRequest())).map(mapper::toDomain));
        return toPositionPage(page);
    }

    @Override
    public Position save(Position position) {
        return execute("Failed to save position.", () -> {
            PositionEntity entity = mapper.toEntity(position);
            PositionEntity saved = repository.save(entity);
            return mapper.toDomain(saved);
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
            return Sort.by(Sort.Direction.DESC, "recordedAt");
        }
        Sort.Direction direction = pageRequest.getSortDirection() == SortDirection.DESC
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        switch (pageRequest.getSortField()) {
            case "recordedAt":
            case "receivedAt":
                return Sort.by(direction, pageRequest.getSortField());
            default:
                return Sort.by(Sort.Direction.DESC, "recordedAt");
        }
    }

    private PositionPage toPositionPage(Page<Position> page) {
        PositionPage result = new PositionPage();
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
            throw new DomainException(DomainErrorCode.PERSISTENCE_ERROR, message, ex);
        }
    }
}
