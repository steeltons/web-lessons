package org.jenjetsu.com.finalproject.security.provider;

import org.jenjetsu.com.finalproject.exception.JwtExpiredException;
import org.jenjetsu.com.finalproject.exception.OAuth2AutnehticationException;
import org.jenjetsu.com.finalproject.repository.OAuth2TokenRepository;
import org.jenjetsu.com.finalproject.security.model.BearerTokenAuthentication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.nimbusds.jwt.JWT;

import lombok.Setter;

public class JWtTokenCacheAuthenticationProvider implements AuthenticationProvider {

    @Setter
    private OAuth2TokenRepository tokenRepository;
    @Setter
    private Converter<JWT, ? extends AbstractAuthenticationToken> JwtAuthenticationConverter;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        BearerTokenAuthentication bearer = (BearerTokenAuthentication) authentication;
        try {
            JWT jwt = this.tokenRepository.getToken(bearer.getToken().split("\\.")[1]);
            AbstractAuthenticationToken auth = this.JwtAuthenticationConverter.convert(jwt);
            auth.setAuthenticated(true);
            auth.setDetails(bearer.getDetails());
            return auth;
        } catch (JwtExpiredException e) {
            throw new OAuth2AutnehticationException("Local JWT token is expired");
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthentication.class.isAssignableFrom(authentication);
    }
    

}
