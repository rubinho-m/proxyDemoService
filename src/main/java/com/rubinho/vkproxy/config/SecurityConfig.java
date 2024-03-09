package com.rubinho.vkproxy.config;

import com.rubinho.vkproxy.jwt.JwtAccessDeniedHandler;
import com.rubinho.vkproxy.jwt.JwtAuthFilter;
import com.rubinho.vkproxy.jwt.JwtAuthenticationEntryPoint;
import com.rubinho.vkproxy.jwt.UserAuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserAuthProvider userAuthProvider;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          UserAuthProvider userAuthProvider,
                          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userAuthProvider = userAuthProvider;
        this.resolver = resolver;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthFilter(userAuthProvider, resolver), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").hasAnyRole("ADMIN", "POSTS", "POSTS_VIEWER", "POSTS_EDITOR")
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").hasAnyRole("ADMIN", "POSTS", "POSTS_EDITOR")
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasAnyRole("ADMIN", "POSTS", "POSTS_EDITOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("ADMIN", "POSTS")

                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "USERS", "USERS_VIEWER", "USERS_EDITOR")
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("ADMIN", "USERS", "USERS_EDITOR")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN", "USERS", "USERS_EDITOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("ADMIN", "USERS")


                        .requestMatchers(HttpMethod.GET, "/api/albums/**").hasAnyRole("ADMIN", "ALBUMS", "ALBUMS_VIEWER", "ALBUMS_EDITOR")
                        .requestMatchers(HttpMethod.POST, "/api/albums/**").hasAnyRole("ADMIN", "ALBUMS", "ALBUMS_EDITOR")
                        .requestMatchers(HttpMethod.PUT, "/api/albums/**").hasAnyRole("ADMIN", "ALBUMS", "ALBUMS_EDITOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/albums/**").hasAnyRole("ADMIN", "ALBUMS")


                        .anyRequest().permitAll()
                )
                .build();

    }

//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return new JwtAccessDeniedHandler();
//    }
}