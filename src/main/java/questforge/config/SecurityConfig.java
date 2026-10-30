package questforge.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, ex) -> {
                        response.setStatus(
                                HttpServletResponse.SC_UNAUTHORIZED
                        );
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(
                                "{\"status\":401,\"message\":\"Authentication required\"}"
                        );
                    })
                    .accessDeniedHandler((request, response, ex) -> {
                        response.setStatus(
                                HttpServletResponse.SC_FORBIDDEN
                        );
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write(
                                "{\"status\":403,\"message\":\"Access denied\"}"
                        );
                    })
            )
            .authorizeHttpRequests(auth -> auth

                    .requestMatchers(
                            HttpMethod.OPTIONS, "/**"
                    ).permitAll()

                    .requestMatchers(
                            "/api/auth/**"
                    ).permitAll()
                    .requestMatchers(
                            HttpMethod.POST, "/users", "/api/users"
                    ).permitAll()

                    .requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    ).permitAll()

                    .requestMatchers(
                            "/api/admin/**",
                            "/admin/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.POST, "/notifications/broadcast"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.GET, "/attempts"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.DELETE, "/attempts/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.GET, "/api/answers",
                            "/api/answers/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.GET, "/options",
                            "/options/*"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.GET, "/users",
                            "/users/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.PUT, "/users/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            HttpMethod.DELETE, "/users/**"
                    ).hasRole("ADMIN")

                    .requestMatchers(
                            "/api/answers/**",
                            "/attempts/**",
                            "/challenges/**",
                            "/daily-quest/**",
                            "/leaderboard/**",
                            "/notifications/**",
                            "/options/**",
                            "/profile/**",
                            "/questions/**",
                            "/streak/**",
                    
                            "/submissions/**",
                            "/weekly-quest/**",
                            "/api/achievement/**",
                            "/api/achievements/**"
                    ).authenticated()

                    .anyRequest().authenticated()
            )
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}