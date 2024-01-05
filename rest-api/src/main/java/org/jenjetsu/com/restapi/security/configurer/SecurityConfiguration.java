package org.jenjetsu.com.restapi.security.configurer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain config(HttpSecurity http, ApplicationContext context) throws Exception {
        http.authorizeHttpRequests((request) -> 
            request.requestMatchers(HttpMethod.GET, "/api-docs").permitAll()
                   .requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                   .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}").permitAll()
                   .requestMatchers(HttpMethod.GET, "/api/v1/users/{userId}/todo").permitAll()
                   .anyRequest().authenticated()
        ).sessionManagement((session) ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ).apply(new CustomJwtConfigurer(context));
        return http.build();
    }
}
