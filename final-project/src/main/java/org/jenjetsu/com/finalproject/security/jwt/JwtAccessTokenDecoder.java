package org.jenjetsu.com.finalproject.security.jwt;

import java.text.ParseException;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.JWTProcessor;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JwtAccessTokenDecoder implements JwtDecoder {
    
    private final Log logger = LogFactory.getLog(getClass());
    private final Supplier<JWTProcessor<? extends SecurityContext>> jwtProcessorSupplier;
    private volatile JWTProcessor<? extends SecurityContext> jwtProcessor;

    @Override
    public JWT decode(String token) throws BadJOSEException, JOSEException {
        if (this.jwtProcessor == null) {
            synchronized(this.jwtProcessorSupplier) {
                if (this.jwtProcessor == null) {
                    this.jwtProcessor = jwtProcessorSupplier.get();
                }
            }
        }
        return this.parseToken(token);
    }

    private JWT parseToken(String token) throws BadJOSEException, JOSEException {
        JWT uncheckedJwt;
        try {
            uncheckedJwt = SignedJWT.parse(token);
        } catch (ParseException e) {
            this.logger.trace("Impossible to parse token");
            throw new BadJOSEException(e.getMessage(), e);
        }
        this.jwtProcessor.process(uncheckedJwt, null);
        return uncheckedJwt;
    }
    
}
