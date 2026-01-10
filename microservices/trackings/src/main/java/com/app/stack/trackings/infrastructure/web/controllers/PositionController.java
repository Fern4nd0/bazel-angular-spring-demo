package com.app.stack.trackings.infrastructure.web.controllers;

import com.app.stack.generated.api.PositionsApi;
import com.app.stack.generated.model.PositionCreate;
import com.app.stack.generated.model.PositionListResponse;
import com.app.stack.generated.model.PositionSearchRequest;
import com.app.stack.generated.model.UserPosition;
import com.app.stack.trackings.application.port.PositionUseCasePort;
import com.app.stack.trackings.domain.entities.BoundingBox;
import com.app.stack.trackings.domain.entities.PageRequest;
import com.app.stack.trackings.domain.entities.PositionPage;
import com.app.stack.trackings.domain.entities.PositionSearchCriteria;
import com.app.stack.trackings.domain.entities.SortDirection;
import com.app.stack.trackings.domain.errors.DomainErrorCode;
import com.app.stack.trackings.domain.errors.DomainException;
import com.app.stack.trackings.infrastructure.web.mappers.PositionApiMapper;
import java.time.OffsetDateTime;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PositionController implements PositionsApi {
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final int MAX_PAGE_SIZE = 200;

    private final PositionUseCasePort service;
    private final PositionApiMapper mapper;

    public PositionController(PositionUseCasePort service, PositionApiMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public UserPosition positionsPost(PositionCreate positionCreate) {
        return mapper.toApi(service.createPosition(mapper.toDomain(positionCreate)));
    }

    @Override
    public PositionListResponse positionsGet(
            Integer page,
            Integer pageSize,
            String sort,
            String userId,
            OffsetDateTime recordedAfter,
            String bbox) {
        BoundingBox boundingBox = parseBbox(bbox);
        PositionPage result = service.listLatestPositions(
                buildPageRequest(page, pageSize, sort),
                userId,
                recordedAfter,
                boundingBox);
        return mapper.toApi(result);
    }

    @Override
    public PositionListResponse usersUserIdPositionsGet(
            String userId,
            Integer page,
            Integer pageSize,
            String sort,
            OffsetDateTime from,
            OffsetDateTime to) {
        PositionPage result = service.listUserHistory(userId, buildPageRequest(page, pageSize, sort), from, to);
        return mapper.toApi(result);
    }

    @Override
    public UserPosition usersUserIdPositionsLatestGet(String userId) {
        return mapper.toApi(service.getLatestPosition(userId));
    }

    @Override
    public PositionListResponse positionsSearchPost(PositionSearchRequest request) {
        if (request == null) {
            throw new DomainException(DomainErrorCode.INVALID_SEARCH_QUERY, "Missing request body.");
        }
        PageRequest pageRequest = buildPageRequest(request.getPage(), request.getPageSize(), request.getSort());
        PositionSearchCriteria criteria = new PositionSearchCriteria();
        criteria.setUserIds(request.getUserIds());
        criteria.setFrom(request.getFrom());
        criteria.setTo(request.getTo());
        criteria.setBbox(mapper.toDomain(request.getBbox()));
        criteria.setPageRequest(pageRequest);
        PositionPage result = service.searchPositions(criteria);
        return mapper.toApi(result);
    }

    @Override
    public UserPosition positionsPositionIdGet(String positionId) {
        return mapper.toApi(service.getPosition(positionId));
    }

    private PageRequest buildPageRequest(Integer page, Integer pageSize, String sort) {
        PageRequest request = new PageRequest();
        request.setPage(normalizePage(page));
        request.setPageSize(normalizePageSize(pageSize));
        applySort(request, sort);
        return request;
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private void applySort(PageRequest request, String sort) {
        if (sort == null || sort.isBlank()) {
            return;
        }
        String[] parts = sort.split(":", 2);
        String field = parts[0].trim();
        if (!isSortableField(field)) {
            return;
        }
        SortDirection direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                ? SortDirection.DESC
                : SortDirection.ASC;
        request.setSortField(field);
        request.setSortDirection(direction);
    }

    private boolean isSortableField(String field) {
        switch (field) {
            case "recordedAt":
            case "receivedAt":
                return true;
            default:
                return false;
        }
    }

    private BoundingBox parseBbox(String bbox) {
        if (bbox == null || bbox.isBlank()) {
            return null;
        }
        String[] parts = bbox.split(",");
        if (parts.length != 4) {
            return null;
        }
        try {
            BoundingBox result = new BoundingBox();
            result.setWest(Double.valueOf(parts[0].trim()));
            result.setSouth(Double.valueOf(parts[1].trim()));
            result.setEast(Double.valueOf(parts[2].trim()));
            result.setNorth(Double.valueOf(parts[3].trim()));
            return result;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
