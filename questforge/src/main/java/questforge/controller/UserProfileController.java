package questforge.controller;

import jakarta.validation.Valid;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import questforge.dto.ProfileUpdateRequestDTO;
import questforge.dto.UserProfileDTO;
import questforge.model.User;
import questforge.service.UserProfileService;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(
            UserProfileService userProfileService) {

        this.userProfileService =
                userProfileService;
    }

    // ==========================================
    // GET MY PROFILE
    // ==========================================

    @GetMapping
    public ResponseEntity<UserProfileDTO> getMyProfile(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                userProfileService.getMyProfile(
                        email
                )
        );
    }

    // ==========================================
    // UPDATE MY PROFILE
    // ==========================================

    @PutMapping
    public ResponseEntity<UserProfileDTO> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequestDTO request) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                userProfileService.updateMyProfile(
                        email,
                        request
                )
        );
    }

    // ==========================================
    // UPLOAD PROFILE IMAGE
    // ==========================================

    @PostMapping(
            value = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserProfileDTO> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file)
            throws Exception {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                userProfileService.uploadProfileImage(
                        email,
                        file
                )
        );
    }

    // ==========================================
    // GET PROFILE IMAGE
    // ==========================================

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getProfileImage(
            Authentication authentication) {

        String email =
                authentication.getName();

        User user =
                userProfileService
                        .getUserWithProfileImage(email);

        byte[] image =
                user.getProfileImage();

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound()
                    .build();
        }

        String contentType =
                user.getProfileImageContentType();

        MediaType mediaType =
                MediaType.APPLICATION_OCTET_STREAM;

        if (contentType != null) {
            try {
                mediaType =
                        MediaType.parseMediaType(
                                contentType
                        );
            } catch (Exception ignored) {
                mediaType =
                        MediaType.APPLICATION_OCTET_STREAM;
            }
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(
                        CacheControl.maxAge(
                                5,
                                TimeUnit.MINUTES
                        )
                )
                .body(image);
    }

    // ==========================================
    // DELETE PROFILE IMAGE
    // ==========================================

    @DeleteMapping("/avatar")
    public ResponseEntity<UserProfileDTO> deleteProfileImage(
            Authentication authentication) {

        String email =
                authentication.getName();

        return ResponseEntity.ok(
                userProfileService.deleteProfileImage(
                        email
                )
        );
    }
}