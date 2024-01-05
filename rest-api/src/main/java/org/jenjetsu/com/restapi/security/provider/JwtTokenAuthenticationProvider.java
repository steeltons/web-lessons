package org.jenjetsu.com.restapi.security.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jenjetsu.com.restapi.exception.OAuth2AuthenticationException;
import org.jenjetsu.com.restapi.repository.OAuth2TokenRepository;
import org.jenjetsu.com.restapi.security.jwt.JwtDecoder;
import org.jenjetsu.com.restapi.security.model.BearerTokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public final class JwtTokenAuthenticationProvider implements AuthenticationProvider {

    private final Log logger = LogFactory.getLog(getClass());
    private final JwtDecoder jwtDecoder;
    @Setter
    private OAuth2TokenRepository tokenRepository;

    @Setter @Autowired
    private Converter<JWT, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        BearerTokenAuthentication bearerAuth = (BearerTokenAuthentication) authentication;
        JWT jwt = this.getJwt(bearerAuth);
        AbstractAuthenticationToken token = this.jwtAuthenticationConverter.convert(jwt);
        token.setDetails(bearerAuth.getDetails());
        this.logger.debug("Authenticated token");
        token.setAuthenticated(true);
        tokenRepository.saveToken(jwt);
        return token;
    }

    private JWT getJwt(BearerTokenAuthentication bearer) {
        try {
            return this.jwtDecoder.decode(bearer.getToken());
        } catch (BadJOSEException e) {
            this.logger.trace("Imbossible to authenticate by token");
            throw new OAuth2AuthenticationException(e.getMessage(), e);
        } catch (JOSEException e) {
            this.logger.trace("Some error while parsing token");
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthentication.class.isAssignableFrom(authentication);
    }
    
}
