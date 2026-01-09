package com.app.stack.users.domain.entities;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPage {
    private List<User> items;
    private int page;
    private int pageSize;
    private int totalItems;
    private int totalPages;
}
