package org.jenjetsu.com.todo.security;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

import org.jenjetsu.com.todo.service.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationTokenConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final UserService userService;
    JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        if(username == null && email == null) {
            throw new BadCredentialsException("No preferred_username or email in jwt token to load or save user");
        }
        UUID userId = this.loadUserId(username, email);
        if(userId == null) {
            userId = this.userService.createFromJwt(jwt).getUserId();
        }
        Collection<? extends GrantedAuthority> authorities = this.mapAuthorities(jwt);
        return new JwtUserIdAuthenticationToken(jwt, userId, authorities);
    }

    private UUID loadUserId(String username, String email) {
        if(this.userService.existsByUsername(username)) {
            return this.userService.readByUsername(username).getUserId();
        } else if (this.userService.existsByEmail(email)) {
            return this.userService.readUserByEmail(email).getUserId();
        } else {
            return null;
        }
    }

    private Collection<? extends GrantedAuthority> mapAuthorities(Jwt jwt) {
        Collection<? extends GrantedAuthority> authorities = authoritiesConverter.convert(jwt);
        Collection<String> roles = jwt.getClaimAsStringList("spring_sec_roles");
        return Stream.concat(authorities.stream().map(GrantedAuthority::getAuthority), roles.stream())
                .filter((role) -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
