package org.jenjetsu.com.finalproject.security.model;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class BearerTokenAuthentication extends AbstractAuthenticationToken {

    private final JWT token;
    @Setter @Getter
    private UUID userId;

    @SneakyThrows
    public BearerTokenAuthentication(String token, 
                                     Collection<GrantedAuthority> authorities) {
        super(authorities);
        Assert.hasText(token, "token cannot be null");
        this.token = SignedJWT.parse(token);
    }

    public BearerTokenAuthentication(JWT token, 
                                     Collection<GrantedAuthority> authorities) {
        super(authorities);
        Assert.notNull(token, "token cannot be null");
        this.token = token;
    }

    public BearerTokenAuthentication(String token) {
        this(token, Collections.EMPTY_LIST);
    }

    public BearerTokenAuthentication(JWT token) {
        this(token, Collections.EMPTY_LIST);
    }

    public String getToken() {
        return this.token.getParsedString();
    }

    @Override
    public Object getCredentials() {
        return getToken();
    }

    @Override
    public Object getPrincipal() {
        return getToken();
    }
    
}
