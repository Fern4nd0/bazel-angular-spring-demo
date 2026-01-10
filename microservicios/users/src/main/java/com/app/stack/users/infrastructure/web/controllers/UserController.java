package com.app.stack.users.infrastructure.web.controllers;

import com.app.stack.generated.api.UsersApi;
import com.app.stack.generated.model.User;
import com.app.stack.generated.model.UserCreate;
import com.app.stack.generated.model.UserListResponse;
import com.app.stack.generated.model.UserStatus;
import com.app.stack.generated.model.UserUpdate;
import com.app.stack.users.application.port.UserUseCasePort;
import com.app.stack.users.domain.entities.PageRequest;
import com.app.stack.users.domain.entities.SortDirection;
import com.app.stack.users.domain.entities.UserPage;
import com.app.stack.users.infrastructure.web.mappers.UserApiMapper;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UsersApi {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 200;

    private final UserUseCasePort service;
    private final UserApiMapper mapper;

    public UserController(UserUseCasePort service, UserApiMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public UserListResponse usersGet(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role) {
        UserPage result = service.listUsers(buildPageRequest(page, pageSize, sort), mapper.toDomain(status), role);
        return mapper.toApi(result);
    }

    @Override
    public User usersPost(UserCreate userCreate) {
        return mapper.toApi(service.createUser(mapper.toDomain(userCreate)));
    }

    @Override
    public UserListResponse usersSearchGet(
            String q,
            Integer page,
            Integer pageSize,
            String sort) {
        UserPage result = service.searchUsers(q, buildPageRequest(page, pageSize, sort));
        return mapper.toApi(result);
    }

    @Override
    public User usersUserIdGet(Long userId) {
        return mapper.toApi(service.getUser(userId));
    }

    @Override
    public User usersUserIdPatch(Long userId, UserUpdate userUpdate) {
        return mapper.toApi(service.updateUser(userId, mapper.toDomain(userUpdate)));
    }

    @Override
    public void usersUserIdDelete(Long userId) {
        service.deleteUser(userId);
    }

    private PageRequest buildPageRequest(Integer page, Integer pageSize, String sort) {
        PageRequest request = new PageRequest();
        request.setPage(normalizePage(page));
        request.setPageSize(normalizePageSize(pageSize));
        applySort(request, sort);
        return request;
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
