package org.jenjetsu.com.restapi.security.jwt;

import static java.lang.String.format;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jenjetsu.com.restapi.security.configurer.OAuth2JwtResourceServerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.jwt.proc.JWTProcessor;

import io.micrometer.common.util.StringUtils;

/**
 * <h2>DefaultJWTProcessorSupplier</h2>
 * <p>Supplier that create JWTProcessor by requesting jwks_uri when it realy needed</p>
 */
public class DefaultJWTProcessorSupplier implements Supplier<JWTProcessor<? extends SecurityContext>> {

    private final Log logger = LogFactory.getLog(getClass());
    private final OAuth2JwtResourceServerProperties properties;

    public DefaultJWTProcessorSupplier(OAuth2JwtResourceServerProperties properties) {
        Assert.notNull(properties, "Properties cannot be null");
        this.properties = properties;
    }

    @Override
    public JWTProcessor<SecurityContext> get() {
        if (StringUtils.isBlank(this.properties.getJwkSetUri())) {
            this.properties.updateConfiguration();
        }
        JWKSet jwkSet = this.getJWKSetFromUri();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);
        JWTClaimsSetVerifier<SecurityContext> claimsVerifier = this.getClaimsSetVerifier();
        try {
            JWSKeySelector<SecurityContext> keySelector = JWSAlgorithmFamilyJWSKeySelector.fromJWKSource(jwkSource);
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(keySelector);
            jwtProcessor.setJWTClaimsSetVerifier(claimsVerifier);
            return jwtProcessor;
        } catch (KeySourceException e) {
            this.logger.trace("Impossible to create JWTProcessor");
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private JWKSet getJWKSetFromUri() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(this.properties.getJwkSetUri(), String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            String errorMessage = format("Response status from \"%s\" is %d",
                                         this.properties.getJwkSetUri(),
                                         response.getStatusCode().value());
            throw new ResponseStatusException(response.getStatusCode(), errorMessage);
        }
        try {
            return JWKSet.parse(response.getBody());
        } catch (ParseException e) {
            this.logger.trace(format("Impossible to parse response from \"%s\"",
                                     this.properties.getIssuerUri()));
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    
    private JWTClaimsSetVerifier<SecurityContext> getClaimsSetVerifier() {
        Set<String> requiredClaims = new HashSet<>(Arrays.asList(
                        "email",
                        "preferred_username",
                        JWTClaimNames.SUBJECT,
                        JWTClaimNames.ISSUED_AT,
                        JWTClaimNames.EXPIRATION_TIME));
        String issuerUri = this.properties.getIssuerUri();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .issuer(issuerUri)
            .build();
        return new DefaultJWTClaimsVerifier<>(claimsSet, requiredClaims);
    }
}
