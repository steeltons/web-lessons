package org.jenjetsu.com.finalproject.security.config;

import org.jenjetsu.com.finalproject.security.filter.ProjectCreatorFilter;
import org.jenjetsu.com.finalproject.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, 
                                                   ApplicationContext context,
                                                   ProjectCreatorFilter projectFilter) throws Exception{
        http.authorizeHttpRequests((req) -> 
            req
                .requestMatchers(HttpMethod.GET, "/api/v1/subtask-statuses").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/subtask-statuses").hasRole("MODERATOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/subtask-statuses").hasRole("MODERATOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/subtask-statuses").hasRole("MODERATOR")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/subtask-statuses").hasRole("MODERATOR")
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
        ).csrf((csrf) ->
            csrf.disable()
        ).sessionManagement((session) ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ).addFilterAfter(projectFilter, LogoutFilter.class)
         .apply(new CustomJwtConfigurer(context, userService));
        return http.build();
    }

}
