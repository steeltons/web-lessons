package org.jenjetsu.com.restapi.security.jwt;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jenjetsu.com.restapi.security.model.BearerTokenAuthentication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.nimbusds.jwt.JWT;

import lombok.SneakyThrows;

public class JwtAuthenticationConverter implements Converter<JWT, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(JWT jwt) {
        return new BearerTokenAuthentication(jwt.getParsedString(), parseAuthorities(jwt));
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
