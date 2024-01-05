package org.jenjetsu.com.restapi.security.filter;

import java.io.IOException;
import java.util.function.Function;

import org.jenjetsu.com.restapi.security.model.BearerTokenAuthentication;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    
    @Setter
    private Function<HttpServletRequest, String> bearerTokenResolver;
    @Setter
    private SecurityContextHolderStrategy contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    @Setter 
    private SecurityContextRepository contextRepository = new NullSecurityContextRepository();
    @Setter
    private AuthenticationFailureHandler failureHandler = (req, resp, exception) -> {
		if (exception instanceof AuthenticationServiceException) {
            throw exception;
        }
        this.authenticationEntryPoint.commence(req, resp, exception);
    };
    @Setter
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Setter
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        try {
            token = this.bearerTokenResolver.apply(request);
        } catch (AuthenticationException invalid) {
            this.logger.trace("Sending to authentication entry point since failed to resolve bearer token", 
                              invalid);
            this.authenticationEntryPoint.commence(request, response, invalid);
            return;
        }
        if (token == null) {
            this.logger.trace("Did not process request since did not find bearer token");
            filterChain.doFilter(request, response);
            return;
        }
        BearerTokenAuthentication tokenAuth = new BearerTokenAuthentication(token);
        tokenAuth.setDetails(this.authenticationDetailsSource.buildDetails(request));
        try {
            Authentication auth = authenticationManager.authenticate(tokenAuth);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            this.contextRepository.saveContext(context, request, response);
            filterChain.doFilter(request, response);
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            this.logger.trace("Failed to process authentication request", failed);
            this.failureHandler.onAuthenticationFailure(request, response, failed);
        }
    }
    
}
