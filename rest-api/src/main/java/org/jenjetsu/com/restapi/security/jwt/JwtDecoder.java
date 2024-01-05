package org.jenjetsu.com.restapi.security.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;

@FunctionalInterface
public interface JwtDecoder {
    
    public JWT decode(String token) throws BadJOSEException, JOSEException;
}
