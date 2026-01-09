package com.app.stack.users.infrastructure.web;

import com.app.stack.generated.model.Error;
import com.app.stack.users.domain.errors.DomainErrorCode;
import com.app.stack.users.domain.errors.DomainException;
import com.app.stack.users.infrastructure.persistence.UserPersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserErrorHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Error> handleDomain(DomainException ex) {
        DomainErrorCode code = ex.getCode();
        HttpStatus status = DomainErrorHttpMapping.resolve(code);
        String message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = DomainErrorHttpMapping.resolveMessage(code);
        }
        return build(status, code.name(), message);
    }

    @ExceptionHandler(UserPersistenceException.class)
    public ResponseEntity<Error> handlePersistence(UserPersistenceException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "PERSISTENCE_ERROR", "Unexpected persistence error.");
    }

    private ResponseEntity<Error> build(HttpStatus status, String code, String message) {
        Error error = new Error();
        error.setCode(code);
        error.setMessage(message);
        return ResponseEntity.status(status).body(error);
    }
}
