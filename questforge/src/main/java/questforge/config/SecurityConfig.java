package questforge.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =========================================================
    // AUTHENTICATION MANAGER
    // =========================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // =========================================================
    // CORS
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf ->
                        csrf.disable()
                )

                // =================================================
                // CORS
                // =================================================

                .cors(cors -> {
                })

                // =================================================
                // SESSION
                // =================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // =================================================
                // EXCEPTION HANDLING
                // =================================================

                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(
                                        (request,
                                         response,
                                         authException) -> {

                                            response.setStatus(
                                                    HttpServletResponse
                                                            .SC_UNAUTHORIZED
                                            );

                                            response.setContentType(
                                                    "application/json"
                                            );

                                            response.getWriter().write(
                                                    "{\"status\":401,\"message\":\"Authentication required\"}"
                                            );
                                        }
                                )
                )

                // =================================================
                // AUTHORIZATION RULES
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // OPTIONS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        )
                        .permitAll()

                        // =================================================
                        // AUTH
                        // =================================================

                        .requestMatchers(
                                "/auth/login"
                        )
                        .permitAll()

                        // =================================================
                        // USER REGISTRATION
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/users"
                        )
                        .permitAll()

                        // =================================================
                        // SWAGGER / OPENAPI
                        // =================================================

                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()

                        // =================================================
                        // USER MANAGEMENT - ADMIN
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/users"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/users/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/users/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/users/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // ADMIN USER MANAGEMENT
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/users/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/admin/users/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/admin/users/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // CHALLENGE MANAGEMENT - ADMIN
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/challenges"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/challenges/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/challenges/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // QUESTIONS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/questions/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/questions"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/questions/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/questions/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // OPTIONS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/options/**"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/options"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/options/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/options/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // SUBMISSIONS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/submissions"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/submissions/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // ADMIN SUBMISSIONS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/submissions/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // NOTIFICATIONS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/notifications/broadcast"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // ADMIN DASHBOARD
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/dashboard"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // ADMIN CHALLENGE ANALYTICS
                        // =================================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/admin/challenges/**"
                        )
                        .hasRole("ADMIN")

                        // =================================================
                        // EVERYTHING ELSE
                        // =================================================

                        .anyRequest()
                        .authenticated()
                )

                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}