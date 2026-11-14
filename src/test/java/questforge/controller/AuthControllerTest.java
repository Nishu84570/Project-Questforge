
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.LoginRequestDTO;
import questforge.dto.LoginResponseDTO;
import questforge.service.AuthService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_ShouldReturnLoginResponse() {

        LoginRequestDTO request = mock(LoginRequestDTO.class);
        LoginResponseDTO expected = mock(LoginResponseDTO.class);

        when(authService.login(request)).thenReturn(expected);

        ResponseEntity<LoginResponseDTO> response =
                authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(authService).login(request);
    }

    @Test
    void login_ShouldCallAuthServiceOnlyOnce() {

        LoginRequestDTO request = mock(LoginRequestDTO.class);

        when(authService.login(request))
                .thenReturn(mock(LoginResponseDTO.class));

        authController.login(request);

        verify(authService, times(1)).login(request);
    }

    @Test
    void login_ShouldPropagateException_WhenAuthenticationFails() {

        LoginRequestDTO request = mock(LoginRequestDTO.class);

        when(authService.login(request))
                .thenThrow(new RuntimeException("Invalid credentials"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.login(request)
        );

        assertEquals("Invalid credentials", exception.getMessage());

        verify(authService).login(request);
    }

    @Test
    void login_ShouldReturnSameResponseFromService() {

        LoginRequestDTO request = mock(LoginRequestDTO.class);
        LoginResponseDTO expected = mock(LoginResponseDTO.class);

        when(authService.login(request)).thenReturn(expected);

        ResponseEntity<LoginResponseDTO> response =
                authController.login(request);

        assertSame(expected, response.getBody());
    }
}
