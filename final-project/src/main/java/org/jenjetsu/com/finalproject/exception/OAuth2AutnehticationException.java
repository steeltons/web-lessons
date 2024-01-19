package org.jenjetsu.com.finalproject.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AutnehticationException extends AuthenticationException {

    public OAuth2AutnehticationException() {
        super("Some exception while oauth2 authentication");
    }

    public OAuth2AutnehticationException(String msg) {
        super(msg);
    }
    
    public OAuth2AutnehticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
