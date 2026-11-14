
package questforge.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.multipart.MultipartFile;

import questforge.dto.ProfileUpdateRequestDTO;
import questforge.dto.UserProfileDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Submission;
import questforge.model.User;
import questforge.repository.SubmissionRepository;
import questforge.repository.UserRepository;
import questforge.service.UserProfileService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UserProfileService userProfileService;

    private User user;

    private final String email = "test@gmail.com";

    @BeforeEach
    void setUp() {
        user = mock(User.class);
    }

    private void stubUserLookup() {
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
    }

    private void stubProfileDependencies(List<Submission> submissions) {

        when(user.getId()).thenReturn(1L);

        stubUserLookup();

        when(submissionRepository.findByUserId(1L))
                .thenReturn(submissions);
    }

    private void stubEmptyProfile() {
        stubProfileDependencies(List.of());
    }


    @Test
    void getMyProfile_ShouldReturnUserProfileDTO() {

        stubEmptyProfile();

        UserProfileDTO result =
                userProfileService.getMyProfile(email);

        assertNotNull(result);

        verify(userRepository).findByEmail(email);
        verify(submissionRepository).findByUserId(1L);
    }


    @Test
    void getMyProfile_ShouldHandleEmptySubmissions() {

        stubEmptyProfile();

        UserProfileDTO result =
                userProfileService.getMyProfile(email);

        assertNotNull(result);

        verify(submissionRepository).findByUserId(1L);
    }

    @Test
    void getMyProfile_ShouldHandleExistingSubmissions() {

        Submission submission1 = mock(Submission.class);
        Submission submission2 = mock(Submission.class);

        when(submission1.getFirstSubmission()).thenReturn(true);
        when(submission2.getFirstSubmission()).thenReturn(false);

        when(submission1.getScore()).thenReturn(80L);
        when(submission2.getScore()).thenReturn(60L);

        when(submission1.getCorrectAnswers()).thenReturn(8L);
        when(submission2.getCorrectAnswers()).thenReturn(6L);

        when(submission1.getTotalQuestions()).thenReturn(10L);
        when(submission2.getTotalQuestions()).thenReturn(10L);

        stubProfileDependencies(List.of(submission1, submission2));

        UserProfileDTO result =
                userProfileService.getMyProfile(email);

        assertNotNull(result);

        verify(submissionRepository).findByUserId(1L);
    }

    @Test
    void getMyProfile_ShouldDetectProfileImage() {

        stubEmptyProfile();

        when(user.getProfileImage())
                .thenReturn(new byte[]{1, 2, 3});

        UserProfileDTO result =
                userProfileService.getMyProfile(email);

        assertNotNull(result);
    }


    @Test
    void updateMyProfile_ShouldUpdateNameSuccessfully() {

        stubEmptyProfile();

        ProfileUpdateRequestDTO request =
                mock(ProfileUpdateRequestDTO.class);

        when(request.getName()).thenReturn("Rahul");

        UserProfileDTO result =
                userProfileService.updateMyProfile(email, request);

        assertNotNull(result);

        verify(user).setName("Rahul");
        verify(userRepository).save(user);
    }


    @Test
    void updateMyProfile_ShouldTrimName() {

        stubEmptyProfile();

        ProfileUpdateRequestDTO request =
                mock(ProfileUpdateRequestDTO.class);

        when(request.getName()).thenReturn("  Rahul  ");

        userProfileService.updateMyProfile(email, request);

        verify(user).setName("Rahul");
    }


    @Test
    void updateMyProfile_ShouldThrowException_WhenRequestIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.updateMyProfile(email, null)
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }

    @Test
    void updateMyProfile_ShouldThrowException_WhenNameIsNull() {

        ProfileUpdateRequestDTO request =
                mock(ProfileUpdateRequestDTO.class);

        when(request.getName()).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.updateMyProfile(email, request)
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }


    @Test
    void updateMyProfile_ShouldThrowException_WhenNameIsEmpty() {

        ProfileUpdateRequestDTO request =
                mock(ProfileUpdateRequestDTO.class);

        when(request.getName()).thenReturn("   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.updateMyProfile(email, request)
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }

    @Test
    void updateMyProfile_ShouldThrowException_WhenNameExceeds100Characters() {

        ProfileUpdateRequestDTO request =
                mock(ProfileUpdateRequestDTO.class);

        when(request.getName()).thenReturn("A".repeat(101));

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.updateMyProfile(email, request)
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }

    @Test
    void uploadProfileImage_ShouldUploadImageSuccessfully()
            throws IOException {

        stubEmptyProfile();

        byte[] imageBytes = {1, 2, 3, 4};

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType())
                .thenReturn("image/png");
        when(multipartFile.getBytes()).thenReturn(imageBytes);

        UserProfileDTO result =
                userProfileService.uploadProfileImage(
                        email, multipartFile
                );

        assertNotNull(result);

        verify(user).setProfileImage(imageBytes);
        verify(user).setProfileImageContentType("image/png");
        verify(userRepository).save(user);
    }


    @Test
    void uploadProfileImage_ShouldThrowException_WhenFileIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.uploadProfileImage(email, null)
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }


    @Test
    void uploadProfileImage_ShouldThrowException_WhenFileIsEmpty() {

        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.uploadProfileImage(
                        email, multipartFile
                )
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }


    @Test
    void uploadProfileImage_ShouldThrowException_WhenImageExceeds5MB() {

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize())
                .thenReturn(5L * 1024L * 1024L + 1L);

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.uploadProfileImage(
                        email, multipartFile
                )
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }

    @Test
    void uploadProfileImage_ShouldRejectInvalidContentType() {

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType())
                .thenReturn("application/pdf");

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.uploadProfileImage(
                        email, multipartFile
                )
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }

    @Test
    void uploadProfileImage_ShouldRejectNullContentType() {

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.uploadProfileImage(
                        email, multipartFile
                )
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }


    @Test
    void getUserWithProfileImage_ShouldReturnUser() {

        stubUserLookup();

        User result =
                userProfileService.getUserWithProfileImage(email);

        assertSame(user, result);

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(submissionRepository);
    }


    @Test
    void deleteProfileImage_ShouldRemoveImageSuccessfully() {

        stubEmptyProfile();

        UserProfileDTO result =
                userProfileService.deleteProfileImage(email);

        assertNotNull(result);

        verify(user).setProfileImage((byte[]) isNull());
        verify(user).setProfileImageContentType(isNull());
        verify(userRepository).save(user);
    }


    @Test
    void getMyProfile_ShouldThrowException_WhenEmailIsNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.getMyProfile(null)
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }

    @Test
    void getMyProfile_ShouldThrowException_WhenEmailIsBlank() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.getMyProfile("   ")
        );

        verifyNoInteractions(userRepository, submissionRepository);
    }

    @Test
    void getMyProfile_ShouldThrowException_WhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userProfileService.getMyProfile(
                        "unknown@gmail.com"
                )
        );

        verify(userRepository).findByEmail("unknown@gmail.com");
        verifyNoInteractions(submissionRepository);
    }


    @Test
    void getUserWithProfileImage_ShouldTrimEmail() {

        stubUserLookup();

        userProfileService.getUserWithProfileImage(
                "  test@gmail.com  "
        );

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(submissionRepository);
    }
}
