package com.app.stack.users.infrastructure.web;

import com.app.stack.generated.model.Pagination;
import com.app.stack.generated.model.UserCreate;
import com.app.stack.generated.model.UserListResponse;
import com.app.stack.generated.model.UserUpdate;
import com.app.stack.users.domain.entities.UserPage;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserApiMapper {
    com.app.stack.generated.model.User toApi(com.app.stack.users.domain.entities.User user);

    @Mapping(target = "status", source = "status")
    com.app.stack.users.domain.entities.User toDomain(com.app.stack.generated.model.User user);

    com.app.stack.users.domain.entities.User toDomain(UserCreate create);

    com.app.stack.users.domain.entities.User toDomain(UserUpdate update);

    com.app.stack.users.domain.entities.UserStatus toDomain(com.app.stack.generated.model.UserStatus status);

    com.app.stack.generated.model.UserStatus toApi(com.app.stack.users.domain.entities.UserStatus status);

    default UserListResponse toApi(UserPage page) {
        UserListResponse response = new UserListResponse();
        response.setItems(page.getItems().stream().map(this::toApi).collect(Collectors.toList()));
        response.setPagination(toPagination(page));
        return response;
    }

    default Pagination toPagination(UserPage page) {
        return new Pagination(page.getPage(), page.getPageSize(), page.getTotalItems(), page.getTotalPages());
    }
}
