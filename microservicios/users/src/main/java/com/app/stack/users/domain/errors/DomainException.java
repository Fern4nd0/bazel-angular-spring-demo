package com.app.stack.users.domain.errors;

public class DomainException extends RuntimeException {
    private final DomainErrorCode code;

    public DomainException(DomainErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public DomainErrorCode getCode() {
        return code;
    }
}
