package com.example.webSpring.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] ADMIN_URLs = {
            "/user/all",
            "api/v1/book/add",
            "api/v1/book/delete/**"
    };
    private static final String[] USER_URLs = {
            "/api/v1/book/all",
            "/books/all",
            "/books/book/{id}"
    };
    private static final String[] PERMIT_URLs = {
            "/",
            "/api/v1/auth/register",
            "/api/v1/auth/login",
//            "api/v1/auth/logout",
            "/api/v1/file/**"
    };
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LogoutHandler logoutHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(PERMIT_URLs).permitAll()
            .and()
            .authorizeHttpRequests()
            .requestMatchers(USER_URLs).hasAnyAuthority("USER","ADMIN")
            .and()
            .authorizeHttpRequests()
            .requestMatchers(ADMIN_URLs).hasAuthority("ADMIN")
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}