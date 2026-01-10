package com.app.stack.trackings.infrastructure.persistence.mappers;

import com.app.stack.trackings.domain.entities.GeoPoint;
import com.app.stack.trackings.domain.entities.Position;
import com.app.stack.trackings.infrastructure.persistence.entities.PositionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PositionPersistenceMapper {
    @Mapping(target = "point", expression = "java(toPoint(entity))")
    Position toDomain(PositionEntity entity);

    @Mapping(target = "latitude", source = "position.point.latitude")
    @Mapping(target = "longitude", source = "position.point.longitude")
    @Mapping(target = "altitudeMeters", source = "position.point.altitudeMeters")
    @Mapping(target = "accuracyMeters", source = "position.point.accuracyMeters")
    @Mapping(target = "headingDegrees", source = "position.point.headingDegrees")
    @Mapping(target = "speedMps", source = "position.point.speedMps")
    PositionEntity toEntity(Position position);

    default GeoPoint toPoint(PositionEntity entity) {
        if (entity == null) {
            return null;
        }
        GeoPoint point = new GeoPoint();
        point.setLatitude(entity.getLatitude());
        point.setLongitude(entity.getLongitude());
        point.setAltitudeMeters(entity.getAltitudeMeters());
        point.setAccuracyMeters(entity.getAccuracyMeters());
        point.setHeadingDegrees(entity.getHeadingDegrees());
        point.setSpeedMps(entity.getSpeedMps());
        return point;
    }
}
