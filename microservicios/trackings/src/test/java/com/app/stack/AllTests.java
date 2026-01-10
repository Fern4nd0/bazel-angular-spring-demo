package com.app.stack;

import com.app.stack.trackings.ApplicationTest;
import com.app.stack.trackings.application.PositionUseCaseTest;
import com.app.stack.trackings.domain.services.PositionServiceTest;
import com.app.stack.trackings.infrastructure.persistence.mappers.PositionPersistenceMapperTest;
import com.app.stack.trackings.infrastructure.web.handlers.DomainErrorHttpMappingTest;
import com.app.stack.trackings.infrastructure.web.mappers.PositionApiMapperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ApplicationTest.class,
        PositionServiceTest.class,
        PositionUseCaseTest.class,
        PositionPersistenceMapperTest.class,
        PositionApiMapperTest.class,
        DomainErrorHttpMappingTest.class
})
public class AllTests {
}
