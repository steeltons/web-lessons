package org.jenjetsu.com.restapi.security.configurer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
@Getter @Setter
@NoArgsConstructor
public class OAuth2JwtResourceServerProperties {

    private final String OPEN_ID_ENDPOINT_CONFIG_PATH = ".well-known/openid-configuration";
    
    private String issuerUri;
    private List<String> audiences;
    private String jwkSetUri;
    private List<String> jwsAlgorithms;
    private String publicKeyLocation;
    private Map<String, Object> openIDEndpointConfiguration; 

    @ConstructorBinding
    public OAuth2JwtResourceServerProperties(String issuerUri, List<String> audiences, 
                                             String jwkSetUri, List<String> jwsAlgorithms, 
                                             String publicKeyLocation) {
        Assert.hasText(issuerUri, "Issuer uri cannot be null or empty or blank");
        this.issuerUri = issuerUri;
        this.audiences = audiences;
        this.jwkSetUri = jwkSetUri;
        this.jwsAlgorithms = jwsAlgorithms;
        this.publicKeyLocation = publicKeyLocation;
    }

    public <T> Optional<T> getOpenIDProperty(String key, Class<? extends T> returnClass) {
        return this.openIDEndpointConfiguration.containsKey(key)
               ? Optional.of(returnClass.cast(this.openIDEndpointConfiguration.get(key)))
               : Optional.empty();
    } 

    public Optional<Object> getOpenIDProperty(String key) {
        return this.openIDEndpointConfiguration.containsKey(key)
               ? Optional.of(this.openIDEndpointConfiguration.get(key))
               : Optional.empty();
    }

    public void updateConfiguration() {
        RestTemplate restTemplate = new RestTemplate();
        String openIDUri = UriComponentsBuilder.fromUriString(issuerUri)
            .path("/"+this.OPEN_ID_ENDPOINT_CONFIG_PATH)
            .toUriString();
        ResponseEntity<Map> response = restTemplate.getForEntity(openIDUri, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(response.getStatusCode(), "Response status is not 2xx");
        }
        Map<String, Object> newConfig = (Map<String, Object>) response.getBody();
        String metaIssuer = newConfig.get("issuer").toString();
        Assert.state(this.issuerUri.equals(metaIssuer), "Current issuer and remote issuer are different");
        this.openIDEndpointConfiguration = newConfig;
        this.jwkSetUri = newConfig.get("jwks_uri").toString();
    }
}
