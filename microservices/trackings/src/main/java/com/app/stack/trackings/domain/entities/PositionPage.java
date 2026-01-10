package com.app.stack.trackings.domain.entities;

import java.util.List;

public class PositionPage {
    private List<Position> items;
    private int page;
    private int pageSize;
    private int totalItems;
    private int totalPages;

    public List<Position> getItems() {
        return items;
    }

    public void setItems(List<Position> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
