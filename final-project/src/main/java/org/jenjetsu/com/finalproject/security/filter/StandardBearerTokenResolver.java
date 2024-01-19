package org.jenjetsu.com.finalproject.security.filter;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jenjetsu.com.finalproject.exception.OAuth2AutnehticationException;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;

public class StandardBearerTokenResolver implements Function<HttpServletRequest, String>{

    private static final Pattern authorizationPattern = Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$",
			Pattern.CASE_INSENSITIVE);

    @Override
    public String apply(HttpServletRequest req) {
        String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.toLowerCase().startsWith("bearer")) {
            return null;
        }
        Matcher matcher = authorizationPattern.matcher(authHeader);
        if (!matcher.matches()) {
            throw new OAuth2AutnehticationException("Bearer token is malformed");
        }
        if (matcher.results().count() > 1) {
            throw new OAuth2AutnehticationException("Found multiple bearer tokens in the request");
        }
        return authHeader.substring(authHeader.indexOf('\s') + 1);
    }
    
}
