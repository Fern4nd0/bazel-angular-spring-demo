package com.app.stack.trackings.infrastructure.web.mappers;

import com.app.stack.generated.model.Pagination;
import com.app.stack.generated.model.PositionCreate;
import com.app.stack.generated.model.PositionListResponse;
import com.app.stack.generated.model.UserPosition;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;

@Mapper
public interface PositionApiMapper {
    UserPosition toApi(Position position);

    Position toDomain(UserPosition position);

    Position toDomain(PositionCreate create);

    com.app.stack.trackings.domain.entities.PositionSource toDomain(
            com.app.stack.generated.model.PositionSource source);

    com.app.stack.generated.model.PositionSource toApi(
            com.app.stack.trackings.domain.entities.PositionSource source);

    com.app.stack.trackings.domain.entities.GeoPoint toDomain(
            com.app.stack.generated.model.GeoPoint point);

    com.app.stack.generated.model.GeoPoint toApi(
            com.app.stack.trackings.domain.entities.GeoPoint point);

    com.app.stack.trackings.domain.entities.BoundingBox toDomain(
            com.app.stack.generated.model.BoundingBox bbox);

    com.app.stack.generated.model.BoundingBox toApi(
            com.app.stack.trackings.domain.entities.BoundingBox bbox);

    default PositionListResponse toApi(PositionPage page) {
        PositionListResponse response = new PositionListResponse();
        response.setData(page.getItems().stream().map(this::toApi).collect(Collectors.toList()));
        response.setPagination(toPagination(page));
        return response;
    }

    default Pagination toPagination(PositionPage page) {
        return new Pagination(page.getPage(), page.getPageSize(), page.getTotalItems(), page.getTotalPages());
    }
}
