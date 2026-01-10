package com.app.stack.users.application;

import com.app.stack.users.application.port.UserUseCasePort;
import com.app.stack.users.domain.entities.PageRequest;
import com.app.stack.users.domain.entities.SortDirection;
import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.domain.entities.UserStatus;
import com.app.stack.users.domain.services.UserService;

public class UserUseCase implements UserUseCasePort {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;

    private final UserService service;

    public UserUseCase(UserService service) {
        this.service = service;
    }

    @Override
    public UserPage listUsers(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role) {
        PageRequest pageRequest = buildPageRequest(page, pageSize, sort);
        return service.listUsers(pageRequest, status, role);
    }

    @Override
    public UserPage searchUsers(
            String query,
            Integer page,
            Integer pageSize,
            String sort) {
        PageRequest pageRequest = buildPageRequest(page, pageSize, sort);
        return service.searchUsers(query, pageRequest);
    }

    @Override
    public User createUser(User create) {
        return service.createUser(create);
    }

    @Override
    public User getUser(Long id) {
        return service.getUser(id);
    }

    @Override
    public User updateUser(Long id, User update) {
        return service.updateUser(id, update);
    }

    @Override
    public void deleteUser(Long id) {
        service.deleteUser(id);
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private PageRequest buildPageRequest(Integer page, Integer pageSize, String sort) {
        PageRequest request = new PageRequest();
        request.setPage(normalizePage(page));
        request.setPageSize(normalizePageSize(pageSize));
        applySort(request, sort);
        return request;
    }

    private void applySort(PageRequest request, String sort) {
        if (sort == null || sort.isBlank()) {
            return;
        }
        String[] parts = sort.split(":", 2);
        String field = parts[0].trim();
        if (!isSortableField(field)) {
            return;
        }
        SortDirection direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                ? SortDirection.DESC
                : SortDirection.ASC;
        request.setSortField(field);
        request.setSortDirection(direction);
    }

    private boolean isSortableField(String field) {
        switch (field) {
            case "createdAt":
            case "email":
            case "firstName":
            case "lastName":
                return true;
            default:
                return false;
        }
    }
}
