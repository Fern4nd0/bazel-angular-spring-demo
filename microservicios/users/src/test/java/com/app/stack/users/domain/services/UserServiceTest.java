package com.app.stack.users.domain.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.app.stack.users.domain.entities.PageRequest;
import com.app.stack.users.domain.entities.SortDirection;
import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.domain.entities.UserStatus;
import com.app.stack.users.domain.errors.DomainErrorCode;
import com.app.stack.users.domain.errors.DomainException;
import com.app.stack.users.domain.port.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;

public class UserServiceTest {
    @Test
    public void createUserTrimsAndDefaults() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = new UserService(repository);

        User create = new User();
        create.setEmail("  test@example.com  ");
        create.setFirstName("  John ");
        create.setLastName(" Doe  ");
        create.setRole("  ");
        create.setPhone("  ");

        User saved = service.createUser(create);

        assertNotNull(saved.getId());
        assertEquals("test@example.com", saved.getEmail());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertEquals("user", saved.getRole());
        assertEquals(UserStatus.ACTIVE, saved.getStatus());
        assertNull(saved.getPhone());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    public void createUserRejectsImmutableFields() {
        UserService service = new UserService(new InMemoryUserRepository());
        User create = new User();
        create.setId(10L);
        create.setEmail("test@example.com");
        create.setFirstName("John");
        create.setLastName("Doe");

        try {
            service.createUser(create);
            fail("Expected DomainException");
        } catch (DomainException ex) {
            assertEquals(DomainErrorCode.INVALID_USER_DATA, ex.getCode());
        }
    }

    @Test
    public void searchUsersRequiresQuery() {
        UserService service = new UserService(new InMemoryUserRepository());
        try {
            service.searchUsers("  ", pageRequest(1, 10, null, null));
            fail("Expected DomainException");
        } catch (DomainException ex) {
            assertEquals(DomainErrorCode.INVALID_SEARCH_QUERY, ex.getCode());
        }
    }

    @Test
    public void listUsersUsesProvidedPageRequest() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = new UserService(repository);

        PageRequest request = pageRequest(3, 50, "email", SortDirection.DESC);
        UserPage result = service.listUsers(request, null, null);

        assertSame(request, repository.lastPageRequest);
        assertSame(repository.lastPageRequest, repository.lastReturnedPageRequest);
        assertEquals(3, result.getPage());
        assertEquals(50, result.getPageSize());
    }

    @Test
    public void updateUserRejectsDuplicateEmail() {
        InMemoryUserRepository repository = new InMemoryUserRepository();
        UserService service = new UserService(repository);

        User first = new User();
        first.setId(1L);
        first.setEmail("first@example.com");
        repository.add(first);

        User second = new User();
        second.setId(2L);
        second.setEmail("second@example.com");
        repository.add(second);

        User update = new User();
        update.setEmail("second@example.com");

        try {
            service.updateUser(1L, update);
            fail("Expected DomainException");
        } catch (DomainException ex) {
            assertEquals(DomainErrorCode.EMAIL_ALREADY_USED, ex.getCode());
        }
    }

    @Test
    public void deleteUserNotFound() {
        UserService service = new UserService(new InMemoryUserRepository());
        try {
            service.deleteUser(99L);
            fail("Expected DomainException");
        } catch (DomainException ex) {
            assertEquals(DomainErrorCode.USER_NOT_FOUND, ex.getCode());
        }
    }

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<Long, User> users = new HashMap<>();
        private long nextId = 1;
        private PageRequest lastPageRequest;
        private PageRequest lastReturnedPageRequest;

        void add(User user) {
            users.put(user.getId(), user);
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public Optional<User> findByEmail(String email) {
            if (email == null) {
                return Optional.empty();
            }
            String normalized = email.toLowerCase(Locale.US);
            for (User user : users.values()) {
                if (user.getEmail() != null && user.getEmail().toLowerCase(Locale.US).equals(normalized)) {
                    return Optional.of(user);
                }
            }
            return Optional.empty();
        }

        @Override
        public UserPage findUsers(PageRequest pageRequest, UserStatus status, String role) {
            lastPageRequest = pageRequest;
            return buildPage(pageRequest, Collections.emptyList());
        }

        @Override
        public UserPage searchUsers(String query, PageRequest pageRequest) {
            lastPageRequest = pageRequest;
            return buildPage(pageRequest, Collections.emptyList());
        }

        @Override
        public User save(User user) {
            if (user.getId() == null) {
                user.setId(nextId++);
            }
            users.put(user.getId(), user);
            return user;
        }

        @Override
        public boolean deleteById(Long id) {
            return users.remove(id) != null;
        }

        private UserPage buildPage(PageRequest request, List<User> items) {
            UserPage page = new UserPage();
            page.setItems(new ArrayList<>(items));
            page.setPage(request.getPage());
            page.setPageSize(request.getPageSize());
            page.setTotalItems(items.size());
            page.setTotalPages(1);
            lastReturnedPageRequest = request;
            return page;
        }
    }

    private static PageRequest pageRequest(
            int page,
            int pageSize,
            String sortField,
            SortDirection sortDirection) {
        PageRequest request = new PageRequest();
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setSortField(sortField);
        request.setSortDirection(sortDirection);
        return request;
    }
}
