package com.app.stack.users.domain.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest {
    private int page;
    private int pageSize;
    private String sortField;
    private SortDirection sortDirection;
}
