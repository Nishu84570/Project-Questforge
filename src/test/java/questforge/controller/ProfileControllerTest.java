
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import questforge.dto.ProfileProgressDTO;
import questforge.service.ProfileService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProfileController profileController;

    @Test
    void getMyProfileProgress_ShouldReturnProfileProgress() {

        String email = "test@gmail.com";

        ProfileProgressDTO expectedResponse =
                mock(ProfileProgressDTO.class);

        when(authentication.getName()).thenReturn(email);

        when(profileService.getMyProfileProgress(email))
                .thenReturn(expectedResponse);

        ResponseEntity<ProfileProgressDTO> response =
                profileController.getMyProfileProgress(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(authentication).getName();
        verify(profileService).getMyProfileProgress(email);
    }

    @Test
    void getMyProfileProgress_ShouldCallServiceOnce() {

        when(authentication.getName())
                .thenReturn("test@gmail.com");

        when(profileService.getMyProfileProgress(anyString()))
                .thenReturn(mock(ProfileProgressDTO.class));

        profileController.getMyProfileProgress(authentication);

        verify(profileService, times(1))
                .getMyProfileProgress("test@gmail.com");
    }
}
