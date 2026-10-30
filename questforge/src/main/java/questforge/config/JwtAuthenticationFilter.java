package questforge.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY =
            "QuestForgeSecretKeyForJWTAuthentication2026Secure";

    private final SecretKey key;

    public JwtAuthenticationFilter() {
        this.key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        String authorizationHeader =
                request.getHeader("Authorization");

        System.out.println(
                "========== JWT FILTER START =========="
        );

        System.out.println(
                "JWT DEBUG -> Method: "
                        + request.getMethod()
                        + " | URI: "
                        + requestUri
        );

        System.out.println(
                "JWT DEBUG -> Authorization present: "
                        + (authorizationHeader != null)
        );

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            System.out.println(
                    "JWT DEBUG -> No Bearer token found"
            );

            filterChain.doFilter(request, response);

            System.out.println(
                    "JWT DEBUG -> Response status: "
                            + response.getStatus()
            );

            System.out.println(
                    "========== JWT FILTER END =========="
            );

            return;
        }

        String token = authorizationHeader.substring(7);

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();

            String role = claims.get(
                    "role",
                    String.class
            );

            System.out.println(
                    "JWT DEBUG -> email: "
                            + email
                            + " | role: "
                            + role
            );

            if (email != null
                    && role != null
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                List<SimpleGrantedAuthority> authorities =
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role
                                )
                        );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authorities
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println(
                        "JWT DEBUG -> Authentication set"
                );

                System.out.println(
                        "JWT DEBUG -> Authorities: "
                                + authentication.getAuthorities()
                );
            }

        } catch (Exception ex) {

            System.out.println(
                    "JWT DEBUG -> JWT ERROR: "
                            + ex.getClass().getSimpleName()
                            + " | "
                            + ex.getMessage()
            );

            SecurityContextHolder.clearContext();
        }

        System.out.println(
                "JWT DEBUG -> Before filterChain:"
                        + " authenticated="
                        + (
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication() != null
                )
        );

        filterChain.doFilter(request, response);

        System.out.println(
                "JWT DEBUG -> After filterChain:"
                        + " status="
                        + response.getStatus()
                        + " | authenticated="
                        + (
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication() != null
                )
        );

        System.out.println(
                "========== JWT FILTER END =========="
        );
    }
}