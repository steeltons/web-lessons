package org.jenjetsu.com.todo.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.UUID;

public class JwtUserIdAuthenticationToken extends JwtAuthenticationToken {

    private final UUID userId;

    public JwtUserIdAuthenticationToken(Jwt jwt, UUID userId, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

}
