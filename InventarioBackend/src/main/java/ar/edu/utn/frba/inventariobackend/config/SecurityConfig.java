package ar.edu.utn.frba.inventariobackend.config;

import ar.edu.utn.frba.inventariobackend.auth.CustomAuthenticationEntryPoint;
import ar.edu.utn.frba.inventariobackend.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configures security settings for the application, including route protection and JWT authentication.
 */
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    /**
     * Authentication filter used to retrieve token data during request.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * An authentication entry point used to throw 401 when unauthenticated.
     */
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Configures the HTTP security filter chain.
     *
     * @param http the {@link HttpSecurity} object used to configure web-based security.
     * @return the {@link SecurityFilterChain} to be applied by Spring Security.
     * @throws Exception if any configuration error occurs.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(
                    auth -> auth.requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/api/v1/products/",
                            "/auth/login",
                            "/auth/refresh",
                            "/api/v1/user",
                            "/api/v1/location",
                            "/api/v1/shipments",
                            "/api/v1/orders",
                            "/api/v1/products",
                            "/api/v1/products/*/stock"
                        ).permitAll().anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}