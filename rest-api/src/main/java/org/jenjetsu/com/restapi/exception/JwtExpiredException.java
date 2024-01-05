package org.jenjetsu.com.restapi.exception;

public class JwtExpiredException extends RuntimeException{
    
    public JwtExpiredException() {
        super();
    }

    public JwtExpiredException(String msg) {
        super(msg);
    }

    public JwtExpiredException(Throwable cause) {
        super(cause);
    }

    public JwtExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
