package org.jenjetsu.com.finalproject.security.jwt;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jenjetsu.com.finalproject.model.User;
import org.jenjetsu.com.finalproject.security.model.BearerTokenAuthentication;
import org.jenjetsu.com.finalproject.service.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class JwtAuthenticationConverter implements Converter<JWT, AbstractAuthenticationToken> {

    private final UserService userSerice;

    @Override
    @SneakyThrows
    public AbstractAuthenticationToken convert(JWT jwt) {
        String username = jwt.getJWTClaimsSet().getStringClaim("preferred_username");;
        String email = jwt.getJWTClaimsSet().getStringClaim("email");
        UUID userId = null;
        if(this.userSerice.existsByUsername(username)) {
            userId = this.userSerice.readUserIdByUsername(username);
        } else if (this.userSerice.existsByEmail(email)) {
            userId = this.userSerice.readUserIdByEmail(email);
        }
        if(userId == null) {
            userId = this.addUserToDatabase(jwt);
        }
        BearerTokenAuthentication authentication = new BearerTokenAuthentication(jwt.getParsedString(), parseAuthorities(jwt));
        authentication.setUserId(userId);
        return authentication;
    }

    @SneakyThrows
    private UUID addUserToDatabase(JWT token) {
        JWTClaimsSet set = token.getJWTClaimsSet();
        String username = set.getStringClaim("preferred_username");
        String email = set.getStringClaim("email");
        User raw = User.builder()
                       .username(username)
                       .email(email)
                       .build();
        raw = this.userSerice.create(raw);
        return raw.getUserId();
    }

    @SneakyThrows
    private List<GrantedAuthority> parseAuthorities(JWT jwt) {
        Map<String, Object> claims = jwt.getJWTClaimsSet().toJSONObject();
        Map<String, Object> newHashMap = new HashMap<>(claims);
        List<String> plainAuthorities;
        if (!claims.containsKey("realm_access")) {
            plainAuthorities = Collections.EMPTY_LIST;
        } else {
            plainAuthorities = (List<String>) claims.get("spring_sec_roles");
        }
        return plainAuthorities.stream()
                               .filter((authority) -> authority.startsWith("ROLE_"))
                               .map(SimpleGrantedAuthority::new)
                               .map(GrantedAuthority.class::cast)
                               .toList();
    }
}
