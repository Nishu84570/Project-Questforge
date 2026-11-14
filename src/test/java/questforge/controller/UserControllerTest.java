
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.UserRequestDTO;
import questforge.dto.UserResponseDTO;
import questforge.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_ShouldReturnCreatedUser() {

        UserRequestDTO request = mock(UserRequestDTO.class);
        UserResponseDTO expected = mock(UserResponseDTO.class);

        when(userService.createUser(request)).thenReturn(expected);

        ResponseEntity<UserResponseDTO> response =
                userController.createUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userService).createUser(request);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {

        List<UserResponseDTO> expected =
                List.of(
                        mock(UserResponseDTO.class),
                        mock(UserResponseDTO.class)
                );

        when(userService.getAllUsers()).thenReturn(expected);

        ResponseEntity<List<UserResponseDTO>> response =
                userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() {

        Long id = 1L;
        UserResponseDTO expected = mock(UserResponseDTO.class);

        when(userService.getUserById(id)).thenReturn(expected);

        ResponseEntity<UserResponseDTO> response =
                userController.getUserById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userService).getUserById(id);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {

        Long id = 1L;

        UserRequestDTO request = mock(UserRequestDTO.class);
        UserResponseDTO expected = mock(UserResponseDTO.class);

        when(userService.updateUser(id, request)).thenReturn(expected);

        ResponseEntity<UserResponseDTO> response =
                userController.updateUser(id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userService).updateUser(id, request);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {

        Long id = 1L;

        ResponseEntity<Void> response =
                userController.deleteUser(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(userService).deleteUser(id);
    }
}
