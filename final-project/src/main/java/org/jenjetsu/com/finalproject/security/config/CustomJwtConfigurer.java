package org.jenjetsu.com.finalproject.security.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jenjetsu.com.finalproject.repository.InMemoryCacheTokenRepository;
import org.jenjetsu.com.finalproject.repository.OAuth2TokenRepository;
import org.jenjetsu.com.finalproject.security.filter.BearerTokenAuthenticationFilter;
import org.jenjetsu.com.finalproject.security.filter.StandardBearerTokenResolver;
import org.jenjetsu.com.finalproject.security.jwt.DefaultJWTProcessorSupplier;
import org.jenjetsu.com.finalproject.security.jwt.JwtAccessTokenDecoder;
import org.jenjetsu.com.finalproject.security.jwt.JwtAuthenticationConverter;
import org.jenjetsu.com.finalproject.security.jwt.JwtDecoder;
import org.jenjetsu.com.finalproject.security.provider.JWtTokenCacheAuthenticationProvider;
import org.jenjetsu.com.finalproject.security.provider.JwtTokenAuthenticationProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_XML;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.DelegatingAccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.proc.JWTProcessor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;

public class CustomJwtConfigurer extends AbstractHttpConfigurer<CustomJwtConfigurer, HttpSecurity> {

    private static final RequestHeaderRequestMatcher X_REQUESTED_WITH = new RequestHeaderRequestMatcher(
        "X-Requested-With", "XMLHttpRequest");
        
    private final ApplicationContext context;
    @Setter
    private AuthenticationManager authenticationManager;
    @Setter
    private Function<HttpServletRequest, String> bearerTokenResolver;
    @Setter
    private JwtDecoder jwtDecoder;
    @Setter
    private Converter<JWT, ? extends AbstractAuthenticationToken> authenticationTokenConverter;
    private AccessDeniedHandler accessDeniedHandler = new DelegatingAccessDeniedHandler(
        new LinkedHashMap<>(Map.of(CsrfException.class, new AccessDeniedHandlerImpl())), 
        this.getDefaultAccessDeniedHandler());
    @Setter
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Setter
    private RequestMatcher requestMatcher;
    @Setter
    private OAuth2TokenRepository tokenRepository;

    public CustomJwtConfigurer(ApplicationContext context) {
        Assert.notNull(context, "ApplicationContext cannot be null");
        this.context = context;
    }

    @Override
    public void init(HttpSecurity http) {
        getRequestMatcher();
        getAuthenticationTokenConverter();
        getBearerTokenResolver();
        getJwtDecoder();
        getAuthenticationManager();
        getAuthenticationEntryPoint();
        registerDefaultCsrfOverride(http);
        registerDefaultAccessDeniedHandler(http);
        registerDefaultEntryPoint(http);
    }

    @Override
    public void configure(HttpSecurity http) {
        BearerTokenAuthenticationFilter filter = new BearerTokenAuthenticationFilter(this.authenticationManager);
        filter.setBearerTokenResolver(this.bearerTokenResolver);
        filter.setAuthenticationEntryPoint(this.authenticationEntryPoint);
        filter.setContextHolderStrategy(getSecurityContextHolderStrategy());
        filter = postProcess(filter);
        http.addFilterAfter(filter, CsrfFilter.class);
    }

    private void registerDefaultCsrfOverride(HttpSecurity http) {
        CsrfConfigurer<HttpSecurity> csrf = http.getConfigurer(CsrfConfigurer.class);
        if (csrf != null) {
            csrf.ignoringRequestMatchers(this.requestMatcher);
        }
    }

    private void registerDefaultAccessDeniedHandler(HttpSecurity http) {
        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = http.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling != null) {
            exceptionHandling.defaultAccessDeniedHandlerFor(this.accessDeniedHandler, this.requestMatcher);
        }
    }

    private void registerDefaultEntryPoint(HttpSecurity http) {
        ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = http.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling != null) {
            ContentNegotiationStrategy contentNegotiationStrategy = http
                .getSharedObject(ContentNegotiationStrategy.class);
            if (contentNegotiationStrategy == null) {
                contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
            }
            MediaTypeRequestMatcher restMatcher = new MediaTypeRequestMatcher(contentNegotiationStrategy,
                APPLICATION_ATOM_XML, APPLICATION_FORM_URLENCODED,
                APPLICATION_OCTET_STREAM, APPLICATION_XML,
                MULTIPART_FORM_DATA, TEXT_XML
            );
            restMatcher.setIgnoredMediaTypes(Collections.singleton(ALL));
            MediaTypeRequestMatcher allMatcher = new MediaTypeRequestMatcher(contentNegotiationStrategy, ALL);
            allMatcher.setUseEquals(true);
            RequestMatcher nonHtmlMatcher = new NegatedRequestMatcher(
                new MediaTypeRequestMatcher(contentNegotiationStrategy, TEXT_HTML));
            RequestMatcher restNotHtmlMatcher = new AndRequestMatcher(
                Arrays.asList(nonHtmlMatcher, restMatcher));
            RequestMatcher prefferedMatcher = new OrRequestMatcher(
                Arrays.asList(this.requestMatcher, X_REQUESTED_WITH, restNotHtmlMatcher, allMatcher));
            exceptionHandling.defaultAuthenticationEntryPointFor(this.authenticationEntryPoint, prefferedMatcher);
        }
    }

    private AccessDeniedHandler getDefaultAccessDeniedHandler() {
        return (req, resp, exception) -> {
            String wwwHeader = "AuthenticationException: " + exception.getMessage();
            resp.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwHeader);
            resp.setStatus(HttpStatus.FORBIDDEN.value());
        };
    }

    JwtDecoder getJwtDecoder() {
        if (this.jwtDecoder == null) {
            OAuth2JwtResourceServerProperties properties 
                = this.context.getBean(OAuth2JwtResourceServerProperties.class);
            Supplier<JWTProcessor<? extends SecurityContext>> jwtProcessorSupplier 
                = new DefaultJWTProcessorSupplier(properties);
            this.jwtDecoder = new JwtAccessTokenDecoder(jwtProcessorSupplier);
        }
        return this.jwtDecoder;
    }

    Function<HttpServletRequest, String> getBearerTokenResolver() {
        if (this.bearerTokenResolver == null) {
            this.bearerTokenResolver = new StandardBearerTokenResolver();
        }
        return this.bearerTokenResolver;
    }

    Converter<JWT, ? extends AbstractAuthenticationToken> getAuthenticationTokenConverter() {
        if (this.authenticationTokenConverter == null) {
            authenticationTokenConverter = new JwtAuthenticationConverter();
        }
        return this.authenticationTokenConverter;
    }

    RequestMatcher getRequestMatcher() {
        if (this.requestMatcher == null) {
            this.requestMatcher = (request) -> {
                try {
                    return this.bearerTokenResolver.apply(request) != null;
                } catch (AuthenticationException e) {
                    return false;
                }
            };
        }
        return this.requestMatcher;
    }

    AuthenticationEntryPoint getAuthenticationEntryPoint() {
        if (this.authenticationEntryPoint == null) {
            this.authenticationEntryPoint = (req, resp, authException) -> {
                String wwwHeader = "AuthenticationException: " + authException.getMessage();
                resp.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwHeader);
                resp.setStatus(HttpStatus.UNAUTHORIZED.value());
            };
        }
        return this.authenticationEntryPoint;
    }
    
    AuthenticationManager getAuthenticationManager() {
        if (this.authenticationManager == null) {
            JwtTokenAuthenticationProvider jwtProvider = new JwtTokenAuthenticationProvider(jwtDecoder);
            JWtTokenCacheAuthenticationProvider cacheProvider = new JWtTokenCacheAuthenticationProvider();
            cacheProvider.setTokenRepository(this.getTokenRepository());
            cacheProvider.setJwtAuthenticationConverter(this.getAuthenticationTokenConverter());
            jwtProvider.setTokenRepository(this.getTokenRepository());
            jwtProvider.setJwtAuthenticationConverter(this.getAuthenticationTokenConverter());
            this.authenticationManager = new ProviderManager(Arrays.asList(cacheProvider, jwtProvider));
        }
        return this.authenticationManager;
    }

    OAuth2TokenRepository getTokenRepository() {
        if (this.tokenRepository == null) {
            this.tokenRepository = new InMemoryCacheTokenRepository();
        }
        return this.tokenRepository;
    }
}
