package com.example.demo.users;

import com.example.demo.generated.api.UsersApi;
import com.example.demo.generated.model.Pagination;
import com.example.demo.generated.model.User;
import com.example.demo.generated.model.UserCreate;
import com.example.demo.generated.model.UserListResponse;
import com.example.demo.generated.model.UserStatus;
import com.example.demo.generated.model.UserUpdate;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController implements UsersApi {
    private final UserStore store;

    public UserController(UserStore store) {
        this.store = store;
    }

    @Override
    public UserListResponse usersGet(
            Integer page,
            Integer pageSize,
            String sort,
            UserStatus status,
            String role) {
        int pageValue = normalizePage(page);
        int pageSizeValue = normalizePageSize(pageSize);

        List<User> filtered = store.list(role, status);
        store.sort(filtered, sort);
        return paginate(filtered, pageValue, pageSizeValue);
    }

    @Override
    public User usersPost(UserCreate userCreate) {
        return store.create(userCreate);
    }

    @Override
    public UserListResponse usersSearchGet(
            String q,
            Integer page,
            Integer pageSize,
            String sort) {
        if (q == null || q.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing search text.");
        }
        int pageValue = normalizePage(page);
        int pageSizeValue = normalizePageSize(pageSize);

        List<User> filtered = store.search(q.trim());
        store.sort(filtered, sort);
        return paginate(filtered, pageValue, pageSizeValue);
    }

    @Override
    public User usersUserIdGet(String userId) {
        return store.findById(userId);
    }

    @Override
    public User usersUserIdPatch(String userId, UserUpdate userUpdate) {
        return store.update(userId, userUpdate);
    }

    @Override
    public void usersUserIdDelete(String userId) {
        store.delete(userId);
    }

    private UserListResponse paginate(List<User> users, int page, int pageSize) {
        int totalItems = users.size();
        int totalPages = totalItems == 0 ? 1 : (int) Math.ceil(totalItems / (double) pageSize);
        int fromIndex = Math.min((page - 1) * pageSize, totalItems);
        int toIndex = Math.min(fromIndex + pageSize, totalItems);
        List<User> items = fromIndex >= toIndex
                ? Collections.emptyList()
                : users.subList(fromIndex, toIndex);
        Pagination pagination = new Pagination(page, pageSize, totalItems, totalPages);
        return new UserListResponse(items, pagination);
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 20;
        }
        return Math.min(pageSize, 200);
    }
}
