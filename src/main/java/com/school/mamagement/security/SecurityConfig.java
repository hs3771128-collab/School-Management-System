package com.school.mamagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder encoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(encoder);

        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider provider)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler))

                .authenticationProvider(provider)

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // PUBLIC
                        // =========================

                        .requestMatchers(
                                "/api/v1/auth/login"
                        ).permitAll()

                        // Bootstrap is additionally protected
                        // inside AuthService.
                        .requestMatchers(
                                "/api/v1/auth/bootstrap"
                        ).permitAll()

                        // =========================
                        // SWAGGER / OPENAPI
                        // =========================

                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // =========================
                        // HEALTH CHECK
                        // =========================

                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        // =========================
                        // AUTHENTICATED AUTH APIs
                        // =========================

                        .requestMatchers(
                                "/api/v1/auth/me",
                                "/api/v1/auth/change-password"
                        ).authenticated()

                        // Account provisioning:
                        // final role restriction is also enforced
                        // inside AuthService.
                        .requestMatchers(
                                "/api/v1/auth/accounts"
                        ).hasAnyRole(
                                "SUPER_ADMIN",
                                "ADMIN"
                        )

                        // =========================
                        // SUPER ADMIN
                        // =========================

                        .requestMatchers(
                                "/api/v1/super-admins/**"
                        ).hasRole("SUPER_ADMIN")

                        // =========================
                        // ADMIN
                        // =========================

                        .requestMatchers(
                                "/api/v1/admins/**"
                        ).hasRole("SUPER_ADMIN")

                        // =========================
                        // PEOPLE MANAGEMENT
                        // =========================

                        .requestMatchers(
                                "/api/v1/teachers/**",
                                "/api/v1/students/**",
                                "/api/v1/parent-guardians/**"
                        ).hasAnyRole(
                                "SUPER_ADMIN",
                                "ADMIN",
                                "TEACHER"
                        )

                        // =========================
                        // EVERYTHING ELSE
                        // =========================

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}