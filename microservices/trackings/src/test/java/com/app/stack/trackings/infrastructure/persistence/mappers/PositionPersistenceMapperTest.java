package com.app.stack.trackings.infrastructure.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.app.stack.trackings.domain.entities.GeoPoint;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.domain.entities.PositionSource;
import com.app.stack.trackings.infrastructure.persistence.entities.PositionEntity;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

public class PositionPersistenceMapperTest {
    @Test
    public void mapsEntityToDomainAndBack() {
        PositionPersistenceMapper mapper = Mappers.getMapper(PositionPersistenceMapper.class);
        PositionEntity entity = new PositionEntity();
        entity.setId("pos_1");
        entity.setUserId(1L);
        entity.setLatitude(40.0);
        entity.setLongitude(-3.0);
        entity.setAltitudeMeters(500.0);
        entity.setAccuracyMeters(5.0);
        entity.setHeadingDegrees(90.0);
        entity.setSpeedMps(1.2);
        entity.setSource(PositionSource.GPS);
        entity.setRecordedAt(OffsetDateTime.now());
        entity.setReceivedAt(OffsetDateTime.now());
        entity.setMetadata(Collections.singletonMap("key", "value"));

        Position domain = mapper.toDomain(entity);
        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getUserId(), domain.getUserId());
        assertEquals(PositionSource.GPS, domain.getSource());

        GeoPoint point = domain.getPoint();
        assertNotNull(point);
        assertEquals(entity.getLatitude(), point.getLatitude());
        assertEquals(entity.getLongitude(), point.getLongitude());

        PositionEntity roundTrip = mapper.toEntity(domain);
        assertNotNull(roundTrip);
        assertEquals(domain.getId(), roundTrip.getId());
        assertEquals(domain.getUserId(), roundTrip.getUserId());
        assertEquals(domain.getSource(), roundTrip.getSource());
        assertEquals(point.getLatitude(), roundTrip.getLatitude());
        assertEquals(point.getLongitude(), roundTrip.getLongitude());
    }
}
