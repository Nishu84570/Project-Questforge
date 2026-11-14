
package questforge.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import questforge.dto.LoginRequestDTO;
import questforge.dto.LoginResponseDTO;
import questforge.model.User;
import questforge.repository.UserRepository;
import questforge.service.AuthService;
import questforge.service.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDTO request;
    private User user;

    @BeforeEach
    void setUp() {

        request = mock(LoginRequestDTO.class);
        user = mock(User.class);
    }


    private void stubValidRequest() {

        when(request.getEmail())
                .thenReturn("user@example.com");

        when(request.getPassword())
                .thenReturn("password123");
    }

    private void stubValidUser() {

        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn("Test User");
        when(user.getEmail()).thenReturn("user@example.com");
        when(user.getPassword()).thenReturn("encodedPassword");
    }


    @Test
    void login_Success() {

        stubValidRequest();
        stubValidUser();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(
                eq("user@example.com"), isNull()))
                .thenReturn("mock-jwt-token");

        LoginResponseDTO result = authService.login(request);

        assertNotNull(result);

        verify(userRepository).findByEmail("user@example.com");

        verify(passwordEncoder).matches(
                "password123", "encodedPassword");

        verify(jwtService).generateToken(
                "user@example.com", null);
    }


    @Test
    void login_NullRequest() {

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(null)
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }


    @Test
    void login_NullEmail() {

        when(request.getEmail()).thenReturn(null);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }


    @Test
    void login_NullPassword() {

        when(request.getEmail())
                .thenReturn("user@example.com");

        when(request.getPassword())
                .thenReturn(null);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }


    @Test
    void login_UserNotFound() {

        stubValidRequest();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail("user@example.com");

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void login_IncorrectPassword() {

        stubValidRequest();

        when(user.getPassword())
                .thenReturn("encodedPassword");

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123", "encodedPassword"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail("user@example.com");

        verify(passwordEncoder).matches(
                "password123", "encodedPassword");

        verifyNoInteractions(jwtService);
    }

    @Test
    void login_StoredPasswordNull() {

        stubValidRequest();

        when(user.getPassword()).thenReturn(null);

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(userRepository).findByEmail("user@example.com");

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void login_EmailWithSpaces() {

        when(request.getEmail())
                .thenReturn("  user@example.com  ");

        when(request.getPassword())
                .thenReturn("password123");

        stubValidUser();

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(
                eq("user@example.com"), isNull()))
                .thenReturn("mock-jwt-token");

        LoginResponseDTO result = authService.login(request);

        assertNotNull(result);

        verify(userRepository).findByEmail("user@example.com");

        verify(passwordEncoder).matches(
                "password123", "encodedPassword");

        verify(jwtService).generateToken(
                "user@example.com", null);
    }
}
