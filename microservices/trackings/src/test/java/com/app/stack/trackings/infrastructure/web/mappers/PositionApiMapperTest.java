package com.app.stack.trackings.infrastructure.web.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.app.stack.generated.model.Pagination;
import com.app.stack.generated.model.PositionListResponse;
import com.app.stack.generated.model.PositionSource;
import com.app.stack.trackings.domain.entities.GeoPoint;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionPage;
import java.util.Collections;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

public class PositionApiMapperTest {
    @Test
    public void mapsPositionPageToResponse() {
        PositionApiMapper mapper = Mappers.getMapper(PositionApiMapper.class);
        Position position = new Position();
        position.setUserId(1L);
        position.setSource(com.app.stack.trackings.domain.entities.PositionSource.GPS);
        GeoPoint point = new GeoPoint();
        point.setLatitude(40.0);
        point.setLongitude(-3.0);
        position.setPoint(point);

        PositionPage page = new PositionPage();
        page.setItems(Collections.singletonList(position));
        page.setPage(2);
        page.setPageSize(5);
        page.setTotalItems(1);
        page.setTotalPages(1);

        PositionListResponse response = mapper.toApi(page);

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals(PositionSource.GPS, response.getData().get(0).getSource());
        Pagination pagination = response.getPagination();
        assertNotNull(pagination);
        assertEquals(Integer.valueOf(2), pagination.getPage());
        assertEquals(Integer.valueOf(5), pagination.getPageSize());
        assertEquals(Integer.valueOf(1), pagination.getTotalItems());
        assertEquals(Integer.valueOf(1), pagination.getTotalPages());
    }
}
