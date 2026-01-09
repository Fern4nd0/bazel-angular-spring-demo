package com.app.stack.users.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.domain.entities.UserStatus;
import com.app.stack.users.domain.port.UserRepository;
import com.app.stack.users.domain.services.UserService;
import java.util.Optional;
import org.junit.Test;

public class UserUseCaseTest {
    @Test
    public void listUsersDelegatesToService() {
        RecordingUserService service = new RecordingUserService();
        UserUseCase useCase = new UserUseCase(service);
        UserPage expected = new UserPage();
        service.listUsersResult = expected;

        UserPage result = useCase.listUsers(2, 30, "email:asc", UserStatus.ACTIVE, "admin");

        assertSame(expected, result);
        assertEquals(Integer.valueOf(2), service.page);
        assertEquals(Integer.valueOf(30), service.pageSize);
        assertEquals("email:asc", service.sort);
        assertEquals(UserStatus.ACTIVE, service.status);
        assertEquals("admin", service.role);
    }

    @Test
    public void searchUsersDelegatesToService() {
        RecordingUserService service = new RecordingUserService();
        UserUseCase useCase = new UserUseCase(service);
        UserPage expected = new UserPage();
        service.searchUsersResult = expected;

        UserPage result = useCase.searchUsers("john", 1, 10, "createdAt:desc");

        assertSame(expected, result);
        assertEquals("john", service.query);
        assertEquals(Integer.valueOf(1), service.page);
        assertEquals(Integer.valueOf(10), service.pageSize);
        assertEquals("createdAt:desc", service.sort);
    }

    @Test
    public void createUserDelegatesToService() {
        RecordingUserService service = new RecordingUserService();
        UserUseCase useCase = new UserUseCase(service);
        User expected = new User();
        service.createUserResult = expected;

        User result = useCase.createUser(new User());

        assertSame(expected, result);
        assertTrue(service.createCalled);
    }

    @Test
    public void getUserDelegatesToService() {
        RecordingUserService service = new RecordingUserService();
        UserUseCase useCase = new UserUseCase(service);
        User expected = new User();
        service.getUserResult = expected;

        User result = useCase.getUser(42L);

        assertSame(expected, result);
        assertEquals(Long.valueOf(42L), service.userId);
    }

    @Test
    public void updateUserDelegatesToService() {
        RecordingUserService service = new RecordingUserService();
        UserUseCase useCase = new UserUseCase(service);
        User expected = new User();
        service.updateUserResult = expected;

        User update = new User();
        User result = useCase.updateUser(7L, update);

        assertSame(expected, result);
        assertEquals(Long.valueOf(7L), service.userId);
        assertSame(update, service.updateUserInput);
    }

    @Test
    public void deleteUserDelegatesToService() {
        RecordingUserService service = new RecordingUserService();
        UserUseCase useCase = new UserUseCase(service);

        useCase.deleteUser(9L);

        assertEquals(Long.valueOf(9L), service.userId);
        assertTrue(service.deleteCalled);
    }

    private static class RecordingUserService extends UserService {
        private Integer page;
        private Integer pageSize;
        private String sort;
        private UserStatus status;
        private String role;
        private String query;
        private Long userId;
        private User updateUserInput;
        private boolean createCalled;
        private boolean deleteCalled;
        private UserPage listUsersResult;
        private UserPage searchUsersResult;
        private User createUserResult;
        private User getUserResult;
        private User updateUserResult;

        RecordingUserService() {
            super(new NoOpRepository());
        }

        @Override
        public UserPage listUsers(Integer page, Integer pageSize, String sort, UserStatus status, String role) {
            this.page = page;
            this.pageSize = pageSize;
            this.sort = sort;
            this.status = status;
            this.role = role;
            return listUsersResult;
        }

        @Override
        public UserPage searchUsers(String query, Integer page, Integer pageSize, String sort) {
            this.query = query;
            this.page = page;
            this.pageSize = pageSize;
            this.sort = sort;
            return searchUsersResult;
        }

        @Override
        public User createUser(User create) {
            createCalled = true;
            return createUserResult;
        }

        @Override
        public User getUser(Long id) {
            userId = id;
            return getUserResult;
        }

        @Override
        public User updateUser(Long id, User update) {
            userId = id;
            updateUserInput = update;
            return updateUserResult;
        }

        @Override
        public void deleteUser(Long id) {
            userId = id;
            deleteCalled = true;
        }
    }

    private static class NoOpRepository implements UserRepository {
        @Override
        public Optional<User> findById(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<User> findByEmail(String email) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserPage findUsers(com.app.stack.users.domain.entities.PageRequest pageRequest,
                                  UserStatus status,
                                  String role) {
            throw new UnsupportedOperationException();
        }

        @Override
        public UserPage searchUsers(String query, com.app.stack.users.domain.entities.PageRequest pageRequest) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User save(User user) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean deleteById(Long id) {
            throw new UnsupportedOperationException();
        }
    }
}
