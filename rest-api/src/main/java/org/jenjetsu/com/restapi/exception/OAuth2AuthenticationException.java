package org.jenjetsu.com.restapi.exception;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationException extends AuthenticationException {

    public OAuth2AuthenticationException() {
        super("Some exception while oauth2 authentication");
    }

    public OAuth2AuthenticationException(String msg) {
        super(msg);
    }
    
    public OAuth2AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
