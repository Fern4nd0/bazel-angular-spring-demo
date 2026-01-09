package com.app.stack;

import com.app.stack.users.ApplicationTest;
import com.app.stack.users.application.UserUseCaseTest;
import com.app.stack.users.domain.services.UserServiceTest;
import com.app.stack.users.infrastructure.persistence.mappers.UserPersistenceMapperTest;
import com.app.stack.users.infrastructure.web.handlers.DomainErrorHttpMappingTest;
import com.app.stack.users.infrastructure.web.mappers.UserApiMapperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ApplicationTest.class,
        UserServiceTest.class,
        UserUseCaseTest.class,
        UserPersistenceMapperTest.class,
        UserApiMapperTest.class,
        DomainErrorHttpMappingTest.class
})
public class AllTests {
}
