package com.app.stack.trackings.application;

import com.app.stack.trackings.application.port.PositionUseCasePort;
import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.domain.entities.PageRequest;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import com.app.stack.trackings.domain.entities.PositionSearchCriteria;
import com.app.stack.trackings.domain.services.PositionService;
import java.time.OffsetDateTime;

public class PositionUseCase implements PositionUseCasePort {
    private final PositionService service;

    public PositionUseCase(PositionService service) {
        this.service = service;
    }

    @Override
    public PositionPage listLatestPositions(
            PageRequest pageRequest,
            Long userId,
            OffsetDateTime recordedAfter,
            BoundingBox bbox) {
        return service.listLatestPositions(pageRequest, userId, recordedAfter, bbox);
    }

    @Override
    public PositionPage listUserHistory(
            Long userId,
            PageRequest pageRequest,
            OffsetDateTime from,
            OffsetDateTime to) {
        return service.listUserHistory(userId, pageRequest, from, to);
    }

    @Override
    public PositionPage searchPositions(PositionSearchCriteria criteria) {
        return service.searchPositions(criteria);
    }

    @Override
    public Position createPosition(Position create) {
        return service.createPosition(create);
    }

    @Override
    public Position getPosition(String id) {
        return service.getPosition(id);
    }

    @Override
    public Position getLatestPosition(Long userId) {
        return service.getLatestPosition(userId);
    }
}
