
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import questforge.dto.ProfileUpdateRequestDTO;
import questforge.dto.UserProfileDTO;
import questforge.model.User;
import questforge.service.UserProfileService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserProfileController userProfileController;

    @Test
    void getMyProfile_ShouldReturnProfile() {

        String email = "test@gmail.com";
        UserProfileDTO expected = mock(UserProfileDTO.class);

        when(authentication.getName()).thenReturn(email);
        when(userProfileService.getMyProfile(email)).thenReturn(expected);

        ResponseEntity<UserProfileDTO> response =
                userProfileController.getMyProfile(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userProfileService).getMyProfile(email);
    }

    @Test
    void updateMyProfile_ShouldReturnUpdatedProfile() {

        String email = "test@gmail.com";

        ProfileUpdateRequestDTO request =
                mock(ProfileUpdateRequestDTO.class);

        UserProfileDTO expected = mock(UserProfileDTO.class);

        when(authentication.getName()).thenReturn(email);

        when(userProfileService.updateMyProfile(email, request))
                .thenReturn(expected);

        ResponseEntity<UserProfileDTO> response =
                userProfileController.updateMyProfile(
                        authentication, request
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userProfileService).updateMyProfile(email, request);
    }

    @Test
    void uploadProfileImage_ShouldReturnUpdatedProfile() throws Exception {

        String email = "test@gmail.com";

        MultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                "image/png",
                "test-image".getBytes()
        );

        UserProfileDTO expected = mock(UserProfileDTO.class);

        when(authentication.getName()).thenReturn(email);

        when(userProfileService.uploadProfileImage(email, file))
                .thenReturn(expected);

        ResponseEntity<UserProfileDTO> response =
                userProfileController.uploadProfileImage(
                        authentication, file
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userProfileService).uploadProfileImage(email, file);
    }

    @Test
    void getProfileImage_ShouldReturnJpegImage() {

        String email = "test@gmail.com";
        byte[] image = {1, 2, 3, 4};

        User user = mock(User.class);

        when(authentication.getName()).thenReturn(email);
        when(userProfileService.getUserWithProfileImage(email))
                .thenReturn(user);

        when(user.getProfileImage()).thenReturn(image);
        when(user.getProfileImageContentType())
                .thenReturn(MediaType.IMAGE_JPEG_VALUE);

        ResponseEntity<byte[]> response =
                userProfileController.getProfileImage(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(image, response.getBody());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());

        assertNotNull(response.getHeaders().getCacheControl());
    }

    @Test
    void getProfileImage_ShouldReturnPngImage() {

        String email = "test@gmail.com";
        byte[] image = {5, 6, 7};

        User user = mock(User.class);

        when(authentication.getName()).thenReturn(email);
        when(userProfileService.getUserWithProfileImage(email))
                .thenReturn(user);

        when(user.getProfileImage()).thenReturn(image);
        when(user.getProfileImageContentType())
                .thenReturn(MediaType.IMAGE_PNG_VALUE);

        ResponseEntity<byte[]> response =
                userProfileController.getProfileImage(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(image, response.getBody());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }

    @Test
    void getProfileImage_ShouldReturnWebpImage() {

        String email = "test@gmail.com";
        byte[] image = {8, 9, 10};

        User user = mock(User.class);

        when(authentication.getName()).thenReturn(email);
        when(userProfileService.getUserWithProfileImage(email))
                .thenReturn(user);

        when(user.getProfileImage()).thenReturn(image);
        when(user.getProfileImageContentType()).thenReturn("image/webp");

        ResponseEntity<byte[]> response =
                userProfileController.getProfileImage(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(image, response.getBody());
        assertEquals(
                MediaType.parseMediaType("image/webp"),
                response.getHeaders().getContentType()
        );
    }

    @Test
    void getProfileImage_ShouldReturnNotFound_WhenImageIsNull() {

        String email = "test@gmail.com";

        User user = mock(User.class);

        when(authentication.getName()).thenReturn(email);
        when(userProfileService.getUserWithProfileImage(email))
                .thenReturn(user);

        when(user.getProfileImage()).thenReturn(null);

        ResponseEntity<byte[]> response =
                userProfileController.getProfileImage(authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(user, never()).getProfileImageContentType();
    }

    @Test
    void getProfileImage_ShouldReturnNotFound_WhenImageIsEmpty() {

        String email = "test@gmail.com";

        User user = mock(User.class);

        when(authentication.getName()).thenReturn(email);
        when(userProfileService.getUserWithProfileImage(email))
                .thenReturn(user);

        when(user.getProfileImage()).thenReturn(new byte[0]);

        ResponseEntity<byte[]> response =
                userProfileController.getProfileImage(authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getProfileImage_ShouldThrowException_WhenFormatIsUnsupported() {

        String email = "test@gmail.com";

        User user = mock(User.class);

        when(authentication.getName()).thenReturn(email);
        when(userProfileService.getUserWithProfileImage(email))
                .thenReturn(user);

        when(user.getProfileImage()).thenReturn(new byte[]{1, 2, 3});
        when(user.getProfileImageContentType())
                .thenReturn("image/gif");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userProfileController.getProfileImage(authentication)
        );

        assertEquals(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                exception.getStatusCode()
        );
    }

    @Test
    void deleteProfileImage_ShouldReturnUpdatedProfile() {

        String email = "test@gmail.com";

        UserProfileDTO expected = mock(UserProfileDTO.class);

        when(authentication.getName()).thenReturn(email);

        when(userProfileService.deleteProfileImage(email))
                .thenReturn(expected);

        ResponseEntity<UserProfileDTO> response =
                userProfileController.deleteProfileImage(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(userProfileService).deleteProfileImage(email);
    }
}
