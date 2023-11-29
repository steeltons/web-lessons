package org.jenjetsu.com.todo.exception;

public class EntityValidateException extends RuntimeException{

    public EntityValidateException() {
        super();
    }

    public EntityValidateException(String message) {
        super(message);
    }

    public EntityValidateException(Throwable cause) {
        super(cause);
    }

    public EntityValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}
