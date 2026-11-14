
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.*;
import questforge.service.AdminUserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @Mock
    private AdminUserRoleRequestDTO roleRequest;

    @Mock
    private AdminUserXpRequestDTO xpRequest;

    @InjectMocks
    private AdminUserController adminUserController;

    private final Long USER_ID = 101L;


    @Test
    void getAllUsers_ShouldReturnAllUsers() {

        List<AdminUserResponseDTO> users = Arrays.asList(
                mock(AdminUserResponseDTO.class),
                mock(AdminUserResponseDTO.class)
        );

        when(adminUserService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<AdminUserResponseDTO>> response =
                adminUserController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(users, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(adminUserService).getAllUsers();
    }


    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {

        when(adminUserService.getAllUsers())
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<AdminUserResponseDTO>> response =
                adminUserController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(adminUserService).getAllUsers();
    }

    @Test
    void searchUsers_ShouldReturnMatchingUsers() {

        String query = "Nishu";

        List<AdminUserResponseDTO> users = Collections.singletonList(
                mock(AdminUserResponseDTO.class)
        );

        when(adminUserService.searchUsers(query)).thenReturn(users);

        ResponseEntity<List<AdminUserResponseDTO>> response =
                adminUserController.searchUsers(query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(users, response.getBody());

        verify(adminUserService).searchUsers(query);
    }


    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {

        AdminUserResponseDTO user = mock(AdminUserResponseDTO.class);

        when(adminUserService.getUserById(USER_ID)).thenReturn(user);

        ResponseEntity<AdminUserResponseDTO> response =
                adminUserController.getUserById(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(user, response.getBody());

        verify(adminUserService).getUserById(USER_ID);
    }


    @Test
    void getUserSubmissions_ShouldReturnSubmissions() {

        List<AdminSubmissionResponseDTO> submissions = Arrays.asList(
                mock(AdminSubmissionResponseDTO.class),
                mock(AdminSubmissionResponseDTO.class)
        );

        when(adminUserService.getUserSubmissions(USER_ID))
                .thenReturn(submissions);

        ResponseEntity<List<AdminSubmissionResponseDTO>> response =
                adminUserController.getUserSubmissions(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(submissions, response.getBody());
        assertEquals(2, response.getBody().size());

        verify(adminUserService).getUserSubmissions(USER_ID);
    }


    @Test
    void updateUserRole_ShouldReturnUpdatedUser() {

        AdminUserResponseDTO updatedUser =
                mock(AdminUserResponseDTO.class);

        when(adminUserService.updateUserRole(
                USER_ID, roleRequest.getRole()))
                .thenReturn(updatedUser);

        ResponseEntity<AdminUserResponseDTO> response =
                adminUserController.updateUserRole(USER_ID, roleRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedUser, response.getBody());

        verify(adminUserService).updateUserRole(
                USER_ID, roleRequest.getRole());
    }


    @Test
    void addUserXp_ShouldReturnUpdatedUser() {

        AdminUserResponseDTO updatedUser =
                mock(AdminUserResponseDTO.class);

        when(adminUserService.addUserXp(
                USER_ID, xpRequest.getXp()))
                .thenReturn(updatedUser);

        ResponseEntity<AdminUserResponseDTO> response =
                adminUserController.addUserXp(USER_ID, xpRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedUser, response.getBody());

        verify(adminUserService).addUserXp(
                USER_ID, xpRequest.getXp());
    }

  
    @Test
    void searchUsers_ShouldReturnEmptyList_WhenNoMatchFound() {

        when(adminUserService.searchUsers("unknown"))
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<AdminUserResponseDTO>> response =
                adminUserController.searchUsers("unknown");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        verify(adminUserService).searchUsers("unknown");
    }
}
