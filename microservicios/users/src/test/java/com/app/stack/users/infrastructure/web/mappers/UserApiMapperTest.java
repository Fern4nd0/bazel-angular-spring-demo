package com.app.stack.users.infrastructure.web.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.app.stack.generated.model.Pagination;
import com.app.stack.generated.model.UserListResponse;
import com.app.stack.generated.model.UserStatus;
import com.app.stack.users.domain.entities.User;
import com.app.stack.users.domain.entities.UserPage;
import java.util.Collections;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

public class UserApiMapperTest {
    @Test
    public void mapsUserPageToResponse() {
        UserApiMapper mapper = Mappers.getMapper(UserApiMapper.class);
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setStatus(com.app.stack.users.domain.entities.UserStatus.ACTIVE);

        UserPage page = new UserPage();
        page.setItems(Collections.singletonList(user));
        page.setPage(2);
        page.setPageSize(5);
        page.setTotalItems(1);
        page.setTotalPages(1);

        UserListResponse response = mapper.toApi(page);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(UserStatus.ACTIVE, response.getItems().get(0).getStatus());
        Pagination pagination = response.getPagination();
        assertNotNull(pagination);
        assertEquals(Integer.valueOf(2), pagination.getPage());
        assertEquals(Integer.valueOf(5), pagination.getPageSize());
        assertEquals(Integer.valueOf(1), pagination.getTotalItems());
        assertEquals(Integer.valueOf(1), pagination.getTotalPages());
    }
}
