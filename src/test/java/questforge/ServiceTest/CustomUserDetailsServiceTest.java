
package questforge.ServiceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import questforge.model.User;
import questforge.model.User.Role;
import questforge.repository.UserRepository;
import questforge.service.CustomUserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @Mock
    private Role role;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "encodedPassword123";

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getRole()).thenReturn(role);
        when(role.matches("(?i)USER|EMPLOYEE|ADMIN"))
                .thenReturn(true);
        when(role.toUpperCase()).thenReturn("USER");

        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getPassword()).thenReturn(PASSWORD);

        UserDetails result =
                customUserDetailsService.loadUserByUsername(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userRepository).findByEmail(EMAIL);
    }


    @Test
    void loadUserByUsername_ShouldTrimEmail_WhenEmailContainsSpaces() {

        String emailWithSpaces = "  test@example.com  ";

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getRole()).thenReturn(role);
        when(role.matches("(?i)USER|EMPLOYEE|ADMIN"))
                .thenReturn(true);
        when(role.toUpperCase()).thenReturn("USER");

        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getPassword()).thenReturn(PASSWORD);

        UserDetails result =
                customUserDetailsService.loadUserByUsername(emailWithSpaces);

        assertNotNull(result);
        assertEquals(EMAIL, result.getUsername());

        verify(userRepository).findByEmail(EMAIL);
    }

 
    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService
                                .loadUserByUsername(EMAIL)
                );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        verify(userRepository).findByEmail(EMAIL);
    }


    @Test
    void loadUserByUsername_ShouldThrowException_WhenRoleIsNull() {

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getRole()).thenReturn(null);

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService
                                .loadUserByUsername(EMAIL)
                );

        assertEquals(
                "User account has an invalid role",
                exception.getMessage()
        );
    }


    @Test
    void loadUserByUsername_ShouldThrowException_WhenRoleIsInvalid() {

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getRole()).thenReturn(role);
        when(role.matches("(?i)USER|EMPLOYEE|ADMIN"))
                .thenReturn(false);

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService
                                .loadUserByUsername(EMAIL)
                );

        assertEquals(
                "User account has an invalid role",
                exception.getMessage()
        );

        verify(userRepository).findByEmail(EMAIL);
    }


    @Test
    void loadUserByUsername_ShouldAssignEmployeeAuthority() {

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getRole()).thenReturn(role);
        when(role.matches("(?i)USER|EMPLOYEE|ADMIN"))
                .thenReturn(true);
        when(role.toUpperCase()).thenReturn("EMPLOYEE");

        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getPassword()).thenReturn(PASSWORD);

        UserDetails result =
                customUserDetailsService.loadUserByUsername(EMAIL);

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_EMPLOYEE")));
    }

    @Test
    void loadUserByUsername_ShouldAssignAdminAuthority() {

        when(userRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(user));

        when(user.getRole()).thenReturn(role);
        when(role.matches("(?i)USER|EMPLOYEE|ADMIN"))
                .thenReturn(true);
        when(role.toUpperCase()).thenReturn("ADMIN");

        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getPassword()).thenReturn(PASSWORD);

        UserDetails result =
                customUserDetailsService.loadUserByUsername(EMAIL);

        assertTrue(result.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN")));
    }
}
