package com.app.stack.users.infrastructure.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserStatus;
import com.app.stack.users.infrastructure.persistence.entities.UserEntity;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

public class UserPersistenceMapperTest {
    @Test
    public void mapsEntityToDomainAndBack() {
        UserPersistenceMapper mapper = Mappers.getMapper(UserPersistenceMapper.class);
        UserEntity entity = new UserEntity();
        entity.setId(10L);
        entity.setEmail("test@example.com");
        entity.setFirstName("Jane");
        entity.setLastName("Doe");
        entity.setRole("admin");
        entity.setStatus(UserStatus.ACTIVE);
        entity.setPhone("123");
        entity.setMetadata(Collections.singletonMap("key", "value"));
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());

        User domain = mapper.toDomain(entity);
        assertNotNull(domain);
        assertEquals(entity.getEmail(), domain.getEmail());
        assertEquals(entity.getFirstName(), domain.getFirstName());
        assertEquals(entity.getLastName(), domain.getLastName());
        assertEquals(entity.getRole(), domain.getRole());
        assertEquals(entity.getStatus(), domain.getStatus());

        UserEntity roundTrip = mapper.toEntity(domain);
        assertNotNull(roundTrip);
        assertEquals(domain.getEmail(), roundTrip.getEmail());
        assertEquals(domain.getFirstName(), roundTrip.getFirstName());
        assertEquals(domain.getLastName(), roundTrip.getLastName());
        assertEquals(domain.getRole(), roundTrip.getRole());
        assertEquals(domain.getStatus(), roundTrip.getStatus());
    }
}
