package com.app.stack.trackings.application.port;

import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.domain.entities.PageRequest;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import com.app.stack.trackings.domain.entities.PositionSearchCriteria;
import java.time.OffsetDateTime;

public interface PositionUseCasePort {
    PositionPage listLatestPositions(
            PageRequest pageRequest,
            Long userId,
            OffsetDateTime recordedAfter,
            BoundingBox bbox);

    PositionPage listUserHistory(
            Long userId,
            PageRequest pageRequest,
            OffsetDateTime from,
            OffsetDateTime to);

    PositionPage searchPositions(PositionSearchCriteria criteria);

    Position createPosition(Position create);

    Position getPosition(String id);

    Position getLatestPosition(Long userId);
}
