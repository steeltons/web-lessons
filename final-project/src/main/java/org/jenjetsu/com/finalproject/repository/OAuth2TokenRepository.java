package org.jenjetsu.com.finalproject.repository;

import com.nimbusds.jwt.JWT;

public interface OAuth2TokenRepository {
    
    public void saveToken(JWT token);
    public JWT getToken(String payload);
}
