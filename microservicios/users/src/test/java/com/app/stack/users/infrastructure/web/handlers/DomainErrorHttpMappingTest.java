package com.app.stack.users.infrastructure.web.handlers;

import static org.junit.Assert.assertEquals;

import com.app.stack.users.domain.errors.DomainErrorCode;
import org.junit.Test;
import org.springframework.http.HttpStatus;

public class DomainErrorHttpMappingTest {
    @Test
    public void resolvesPersistenceErrorToInternalServerError() {
        HttpStatus status = DomainErrorHttpMapping.resolve(DomainErrorCode.PERSISTENCE_ERROR);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status);
    }
}
