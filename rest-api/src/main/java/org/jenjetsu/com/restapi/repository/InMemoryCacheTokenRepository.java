package org.jenjetsu.com.restapi.repository;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jenjetsu.com.restapi.exception.JwtExpiredException;

import com.nimbusds.jose.util.StandardCharset;
import com.nimbusds.jwt.JWT;

import lombok.Setter;
import lombok.SneakyThrows;

public class InMemoryCacheTokenRepository implements OAuth2TokenRepository {

    private final Map<String, JWT> cache = new ConcurrentHashMap<>();
    @Setter
    private MessageDigest messageDigest;

    @SneakyThrows
    public InMemoryCacheTokenRepository() {
        this.messageDigest = MessageDigest.getInstance("SHA-256");
    }

    @Override
    public void saveToken(JWT token) {
        try {
            String payload = token.getParsedParts()[1].toString();
            String key = new String(messageDigest.digest(payload.getBytes(StandardCharset.UTF_8)));
            this.cache.put(key, token);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override @SneakyThrows
    public JWT getToken(String payload) {
        String key = new String(this.messageDigest.digest(payload.getBytes(StandardCharset.UTF_8)));
        if (!this.cache.containsKey(key)) {
            throw new RuntimeException("Token wasn't found");
        }
        JWT jwt = this.cache.get(key);
        if (!jwt.getJWTClaimsSet().getExpirationTime().toInstant().isAfter(Instant.now())) {
            this.cache.remove(key);
            throw new JwtExpiredException("Token is expired");
        }
        return jwt;
    }
    
}
