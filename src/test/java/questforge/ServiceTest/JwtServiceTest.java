
package questforge.ServiceTest;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import questforge.model.User.Role;
import questforge.service.JwtService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            "01234567890123456789012345678901";

    private static final long EXPIRATION = 86400000L;

    private static final String EMAIL =
            "test@example.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION);
    }


    @Test
    void generateToken_ShouldReturnValidToken() {

        Role role = Role.values()[0];

        String token = jwtService.generateToken(EMAIL, role);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_ShouldContainCorrectEmail() {

        Role role = Role.values()[0];

        String token = jwtService.generateToken(EMAIL, role);

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(EMAIL, claims.getSubject());
    }

    @Test
    void generateToken_ShouldContainCorrectRole() {

        Role role = Role.values()[0];

        String token = jwtService.generateToken(EMAIL, role);

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(role.name(), claims.get("role", String.class));
    }

   
    @Test
    void generateToken_ShouldContainIssuedAtDate() {

        Role role = Role.values()[0];

        String token = jwtService.generateToken(EMAIL, role);

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getIssuedAt().getTime() <=
                System.currentTimeMillis());
    }

  
    @Test
    void generateToken_ShouldHaveCorrectExpirationTime() {

        Role role = Role.values()[0];

        String token = jwtService.generateToken(EMAIL, role);

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertNotNull(expiration);
        assertEquals(EXPIRATION,
                expiration.getTime() - issuedAt.getTime());
    }


    @Test
    void constructor_ShouldThrowException_WhenSecretIsNull() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new JwtService(null, EXPIRATION)
                );

        assertEquals(
                "JWT secret must be configured",
                exception.getMessage()
        );
    }

    @Test
    void constructor_ShouldThrowException_WhenSecretIsBlank() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new JwtService("   ", EXPIRATION)
                );

        assertEquals(
                "JWT secret must be configured",
                exception.getMessage()
        );
    }


    @Test
    void constructor_ShouldThrowException_WhenSecretIsTooShort() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new JwtService("shortsecret", EXPIRATION)
                );

        assertEquals(
                "JWT secret must be at least 32 bytes",
                exception.getMessage()
        );
    }


    @Test
    void constructor_ShouldThrowException_WhenExpirationIsZero() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new JwtService(SECRET, 0)
                );

        assertEquals(
                "JWT expiration must be positive",
                exception.getMessage()
        );
    }


    @Test
    void constructor_ShouldThrowException_WhenExpirationIsNegative() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> new JwtService(SECRET, -1000)
                );

        assertEquals(
                "JWT expiration must be positive",
                exception.getMessage()
        );
    }


    @Test
    void generateToken_ShouldHaveValidSignature() {

        Role role = Role.values()[0];

        String token = jwtService.generateToken(EMAIL, role);

        SecretKey correctKey = Keys.hmacShaKeyFor(
                SECRET.getBytes(StandardCharsets.UTF_8));

        assertDoesNotThrow(() ->
                Jwts.parser()
                        .verifyWith(correctKey)
                        .build()
                        .parseSignedClaims(token)
        );
    }
}
