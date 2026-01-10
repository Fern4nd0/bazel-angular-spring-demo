package com.app.stack.trackings.domain.errors;

public class DomainException extends RuntimeException {
    private final DomainErrorCode code;

    public DomainException(DomainErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public DomainException(DomainErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public DomainErrorCode getCode() {
        return code;
    }
}
