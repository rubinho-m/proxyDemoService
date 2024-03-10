package com.rubinho.vkproxy.config;


import com.rubinho.vkproxy.jwt.JwtAccessDeniedHandler;
import com.rubinho.vkproxy.jwt.JwtAuthFilter;
import com.rubinho.vkproxy.jwt.JwtAuthenticationEntryPoint;
import com.rubinho.vkproxy.jwt.UserAuthProvider;
import com.rubinho.vkproxy.mappers.UserMapper;
import com.rubinho.vkproxy.services.AuditService;
import com.rubinho.vkproxy.services.UserService;
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
    private final AuditService auditService;
    private final UserService userService;
    private final UserMapper userMapper;

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver resolver;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          UserAuthProvider userAuthProvider,
                          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                          AuditService auditService,
                          UserService userService,
                          UserMapper userMapper) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userAuthProvider = userAuthProvider;
        this.resolver = resolver;
        this.auditService = auditService;
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthFilter(userAuthProvider, resolver), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/activate").authenticated()
                        .requestMatchers("/api/password/**").authenticated()


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

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")


                        .anyRequest().permitAll()
                );

        return http.build();

    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler(auditService, userService, userAuthProvider, userMapper);
    }
}