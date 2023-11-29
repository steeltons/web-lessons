package org.jenjetsu.com.todo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final Converter<Jwt, AbstractAuthenticationToken> tokenConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests((request) ->
                            request
                                .requestMatchers("/api-docs/**").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll()
                                .requestMatchers("/swagger-config.*").permitAll()
                                .anyRequest().authenticated()
                        )
                .sessionManagement((session) ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                .oauth2ResourceServer((oauth2) ->
                            oauth2.jwt(Customizer.withDefaults())
                                    .jwt((jwtConfigurer) ->
                                            jwtConfigurer.jwtAuthenticationConverter(tokenConverter)
                                    )
                        );
        return http.build();
    }
}
