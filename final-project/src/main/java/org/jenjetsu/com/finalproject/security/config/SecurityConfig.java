package org.jenjetsu.com.finalproject.security.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ApplicationContext context) throws Exception{
        http.authorizeHttpRequests((req) -> 
            req.anyRequest().authenticated()
        )
        .sessionManagement((session) ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ).apply(new CustomJwtConfigurer(context));
        return http.build();
    }
}
