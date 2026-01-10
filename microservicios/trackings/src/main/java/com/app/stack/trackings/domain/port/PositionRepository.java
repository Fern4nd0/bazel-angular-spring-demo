package com.app.stack.trackings.domain.port;

import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.domain.entities.PageRequest;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import com.app.stack.trackings.domain.entities.PositionSearchCriteria;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface PositionRepository {
    Optional<Position> findById(String id);

    Optional<Position> findLatestByUserId(String userId);

    PositionPage findLatestPositions(
            PageRequest pageRequest,
            String userId,
            OffsetDateTime recordedAfter,
            BoundingBox bbox);

    PositionPage findUserHistory(
            String userId,
            PageRequest pageRequest,
            OffsetDateTime from,
            OffsetDateTime to);

    PositionPage searchPositions(PositionSearchCriteria criteria);

    Position save(Position position);
}
