package com.app.stack.trackings.domain.errors;

public enum DomainErrorCode {
    INVALID_POSITION_DATA("Invalid position data."),
    POSITION_NOT_FOUND("Position not found."),
    INVALID_SEARCH_QUERY("Invalid search query."),
    PERSISTENCE_ERROR("Persistence error.");

    private final String defaultMessage;

    DomainErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
