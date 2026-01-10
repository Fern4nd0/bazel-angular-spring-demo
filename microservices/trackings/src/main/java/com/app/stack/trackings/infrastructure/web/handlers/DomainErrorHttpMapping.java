package com.app.stack.trackings.infrastructure.web.handlers;

import com.app.stack.trackings.domain.errors.DomainErrorCode;
import org.springframework.http.HttpStatus;

public enum DomainErrorHttpMapping {
    POSITION_NOT_FOUND(DomainErrorCode.POSITION_NOT_FOUND, HttpStatus.NOT_FOUND),
    INVALID_POSITION_DATA(DomainErrorCode.INVALID_POSITION_DATA, HttpStatus.BAD_REQUEST),
    INVALID_SEARCH_QUERY(DomainErrorCode.INVALID_SEARCH_QUERY, HttpStatus.BAD_REQUEST),
    PERSISTENCE_ERROR(DomainErrorCode.PERSISTENCE_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);

    private final DomainErrorCode code;
    private final HttpStatus status;

    DomainErrorHttpMapping(DomainErrorCode code, HttpStatus status) {
        this.code = code;
        this.status = status;
    }

    public DomainErrorCode getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static HttpStatus resolve(DomainErrorCode code) {
        for (DomainErrorHttpMapping mapping : values()) {
            if (mapping.code == code) {
                return mapping.status;
            }
        }
        return HttpStatus.BAD_REQUEST;
    }

    public static String resolveMessage(DomainErrorCode code) {
        if (code == null) {
            return "Invalid request.";
        }
        return code.getDefaultMessage();
    }
}
