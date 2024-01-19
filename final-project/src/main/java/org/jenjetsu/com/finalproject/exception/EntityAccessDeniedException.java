package org.jenjetsu.com.finalproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EntityAccessDeniedException extends RuntimeException{

    public EntityAccessDeniedException() {
        super();
    }

    public EntityAccessDeniedException(String message) {
        super(message);
    }

    public EntityAccessDeniedException(Throwable cause) {
        super(cause);
    }

    public EntityAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
