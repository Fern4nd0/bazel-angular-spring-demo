package com.app.stack.trackings.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.domain.entities.PageRequest;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import com.app.stack.trackings.domain.entities.PositionSearchCriteria;
import com.app.stack.trackings.domain.port.PositionRepository;
import com.app.stack.trackings.domain.services.PositionService;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.Test;

public class PositionUseCaseTest {
    @Test
    public void listLatestPositionsDelegatesToService() {
        RecordingPositionService service = new RecordingPositionService();
        PositionUseCase useCase = new PositionUseCase(service);
        PositionPage expected = new PositionPage();
        service.listLatestResult = expected;

        PageRequest request = new PageRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setSortField("recordedAt");
        PositionPage result = useCase.listLatestPositions(request, "usr_1", null, null);

        assertSame(expected, result);
        assertSame(request, service.pageRequest);
        assertEquals("usr_1", service.userId);
    }

    @Test
    public void searchPositionsDelegatesToService() {
        RecordingPositionService service = new RecordingPositionService();
        PositionUseCase useCase = new PositionUseCase(service);
        PositionSearchCriteria criteria = new PositionSearchCriteria();
        PositionPage expected = new PositionPage();
        service.searchResult = expected;

        PositionPage result = useCase.searchPositions(criteria);

        assertSame(expected, result);
        assertSame(criteria, service.criteria);
    }

    @Test
    public void createPositionDelegatesToService() {
        RecordingPositionService service = new RecordingPositionService();
        PositionUseCase useCase = new PositionUseCase(service);
        Position expected = new Position();
        service.createResult = expected;

        Position result = useCase.createPosition(new Position());

        assertSame(expected, result);
        assertTrue(service.createCalled);
    }

    @Test
    public void getPositionDelegatesToService() {
        RecordingPositionService service = new RecordingPositionService();
        PositionUseCase useCase = new PositionUseCase(service);
        Position expected = new Position();
        service.getResult = expected;

        Position result = useCase.getPosition("pos_1");

        assertSame(expected, result);
        assertEquals("pos_1", service.positionId);
    }

    @Test
    public void getLatestPositionDelegatesToService() {
        RecordingPositionService service = new RecordingPositionService();
        PositionUseCase useCase = new PositionUseCase(service);
        Position expected = new Position();
        service.latestResult = expected;

        Position result = useCase.getLatestPosition("usr_1");

        assertSame(expected, result);
        assertEquals("usr_1", service.userId);
    }

    @Test
    public void listUserHistoryDelegatesToService() {
        RecordingPositionService service = new RecordingPositionService();
        PositionUseCase useCase = new PositionUseCase(service);
        PositionPage expected = new PositionPage();
        service.historyResult = expected;

        PageRequest request = new PageRequest();
        OffsetDateTime from = OffsetDateTime.now();
        OffsetDateTime to = from.plusHours(1);
        PositionPage result = useCase.listUserHistory("usr_9", request, from, to);

        assertSame(expected, result);
        assertSame(request, service.pageRequest);
        assertEquals("usr_9", service.userId);
        assertSame(from, service.from);
        assertSame(to, service.to);
    }

    private static class RecordingPositionService extends PositionService {
        private PageRequest pageRequest;
        private String userId;
        private String positionId;
        private OffsetDateTime from;
        private OffsetDateTime to;
        private PositionSearchCriteria criteria;
        private boolean createCalled;
        private PositionPage listLatestResult;
        private PositionPage historyResult;
        private PositionPage searchResult;
        private Position createResult;
        private Position getResult;
        private Position latestResult;

        RecordingPositionService() {
            super(new NoOpRepository());
        }

        @Override
        public PositionPage listLatestPositions(PageRequest pageRequest, String userId, OffsetDateTime recordedAfter, BoundingBox bbox) {
            this.pageRequest = pageRequest;
            this.userId = userId;
            return listLatestResult;
        }

        @Override
        public PositionPage listUserHistory(String userId, PageRequest pageRequest, OffsetDateTime from, OffsetDateTime to) {
            this.userId = userId;
            this.pageRequest = pageRequest;
            this.from = from;
            this.to = to;
            return historyResult;
        }

        @Override
        public PositionPage searchPositions(PositionSearchCriteria criteria) {
            this.criteria = criteria;
            return searchResult;
        }

        @Override
        public Position createPosition(Position create) {
            createCalled = true;
            return createResult;
        }

        @Override
        public Position getPosition(String id) {
            positionId = id;
            return getResult;
        }

        @Override
        public Position getLatestPosition(String userId) {
            this.userId = userId;
            return latestResult;
        }
    }

    private static class NoOpRepository implements PositionRepository {
        @Override
        public Optional<Position> findById(String id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Position> findLatestByUserId(String userId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PositionPage findLatestPositions(PageRequest pageRequest, String userId, OffsetDateTime recordedAfter, BoundingBox bbox) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PositionPage findUserHistory(String userId, PageRequest pageRequest, OffsetDateTime from, OffsetDateTime to) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PositionPage searchPositions(PositionSearchCriteria criteria) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Position save(Position position) {
            throw new UnsupportedOperationException();
        }
    }
}
