package com.app.stack.trackings.domain.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.domain.entities.GeoPoint;
import com.app.stack.trackings.domain.entities.PageRequest;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import com.app.stack.trackings.domain.entities.PositionSearchCriteria;
import com.app.stack.trackings.domain.entities.PositionSource;
import com.app.stack.trackings.domain.entities.SortDirection;
import com.app.stack.trackings.domain.errors.DomainErrorCode;
import com.app.stack.trackings.domain.errors.DomainException;
import com.app.stack.trackings.domain.port.PositionRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;

public class PositionServiceTest {
    @Test
    public void createPositionDefaultsAndSetsReceivedAt() {
        PositionService service = new PositionService(new InMemoryPositionRepository());

        Position create = new Position();
        create.setUserId("  usr_123 ");
        GeoPoint point = new GeoPoint();
        point.setLatitude(40.0);
        point.setLongitude(-3.0);
        create.setPoint(point);
        create.setRecordedAt(OffsetDateTime.now());

        Position saved = service.createPosition(create);

        assertNotNull(saved.getId());
        assertEquals("usr_123", saved.getUserId());
        assertEquals(PositionSource.UNKNOWN, saved.getSource());
        assertNotNull(saved.getReceivedAt());
        assertSame(point, saved.getPoint());
    }

    @Test
    public void createPositionRejectsImmutableFields() {
        PositionService service = new PositionService(new InMemoryPositionRepository());
        Position create = new Position();
        create.setId("pos_1");
        create.setUserId("usr_123");
        GeoPoint point = new GeoPoint();
        point.setLatitude(40.0);
        point.setLongitude(-3.0);
        create.setPoint(point);
        create.setRecordedAt(OffsetDateTime.now());

        try {
            service.createPosition(create);
            fail("Expected DomainException");
        } catch (DomainException ex) {
            assertEquals(DomainErrorCode.INVALID_POSITION_DATA, ex.getCode());
        }
    }

    @Test
    public void listUserHistoryRequiresUserId() {
        PositionService service = new PositionService(new InMemoryPositionRepository());
        try {
            service.listUserHistory(" ", pageRequest(1, 10, null, null), null, null);
            fail("Expected DomainException");
        } catch (DomainException ex) {
            assertEquals(DomainErrorCode.INVALID_SEARCH_QUERY, ex.getCode());
        }
    }

    @Test
    public void listLatestPositionsUsesProvidedPageRequest() {
        InMemoryPositionRepository repository = new InMemoryPositionRepository();
        PositionService service = new PositionService(repository);

        PageRequest request = pageRequest(2, 50, "recordedAt", SortDirection.DESC);
        PositionPage result = service.listLatestPositions(request, null, null, null);

        assertSame(request, repository.lastPageRequest);
        assertSame(repository.lastPageRequest, repository.lastReturnedPageRequest);
        assertEquals(2, result.getPage());
        assertEquals(50, result.getPageSize());
    }

    @Test
    public void getPositionNotFound() {
        PositionService service = new PositionService(new InMemoryPositionRepository());
        try {
            service.getPosition("pos_missing");
            fail("Expected DomainException");
        } catch (DomainException ex) {
            assertEquals(DomainErrorCode.POSITION_NOT_FOUND, ex.getCode());
        }
    }

    private static class InMemoryPositionRepository implements PositionRepository {
        private final Map<String, Position> positions = new HashMap<>();
        private PageRequest lastPageRequest;
        private PageRequest lastReturnedPageRequest;

        @Override
        public Optional<Position> findById(String id) {
            return Optional.ofNullable(positions.get(id));
        }

        @Override
        public Optional<Position> findLatestByUserId(String userId) {
            return Optional.empty();
        }

        @Override
        public PositionPage findLatestPositions(PageRequest pageRequest, String userId, OffsetDateTime recordedAfter, BoundingBox bbox) {
            lastPageRequest = pageRequest;
            return buildPage(pageRequest, Collections.emptyList());
        }

        @Override
        public PositionPage findUserHistory(String userId, PageRequest pageRequest, OffsetDateTime from, OffsetDateTime to) {
            lastPageRequest = pageRequest;
            return buildPage(pageRequest, Collections.emptyList());
        }

        @Override
        public PositionPage searchPositions(PositionSearchCriteria criteria) {
            lastPageRequest = criteria.getPageRequest();
            return buildPage(criteria.getPageRequest(), Collections.emptyList());
        }

        @Override
        public Position save(Position position) {
            positions.put(position.getId(), position);
            return position;
        }

        private PositionPage buildPage(PageRequest request, java.util.List<Position> items) {
            PositionPage page = new PositionPage();
            page.setItems(new ArrayList<>(items));
            page.setPage(request.getPage());
            page.setPageSize(request.getPageSize());
            page.setTotalItems(items.size());
            page.setTotalPages(1);
            lastReturnedPageRequest = request;
            return page;
        }
    }

    private static PageRequest pageRequest(
            int page,
            int pageSize,
            String sortField,
            SortDirection sortDirection) {
        PageRequest request = new PageRequest();
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setSortField(sortField);
        request.setSortDirection(sortDirection);
        return request;
    }
}
