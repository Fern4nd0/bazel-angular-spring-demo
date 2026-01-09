package com.app.stack.users.domain.errors;

public enum DomainErrorCode {
    USER_NOT_FOUND("User not found."),
    EMAIL_ALREADY_USED("Email is already registered."),
    INVALID_USER_DATA("Invalid user data."),
    INVALID_SEARCH_QUERY("Invalid search query.");

    private final String defaultMessage;

    DomainErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
