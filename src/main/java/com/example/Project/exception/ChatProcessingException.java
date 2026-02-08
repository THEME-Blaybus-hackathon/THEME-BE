package com.example.Project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AI 채팅 처리 중 오류 발생 시 예외
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ChatProcessingException extends RuntimeException {
    
    public ChatProcessingException(String message) {
        super(message);
    }
    
    public ChatProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
