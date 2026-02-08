package com.example.Project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 다른 사용자의 세션에 접근하려고 할 때 발생하는 예외
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedSessionAccessException extends RuntimeException {
    
    public UnauthorizedSessionAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedSessionAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
