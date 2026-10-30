
package questforge.controller;

import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import questforge.dto.ProfileUpdateRequestDTO;
import questforge.dto.UserProfileDTO;
import questforge.model.User;
import questforge.service.UserProfileService;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getMyProfile(
            Authentication authentication) {
        return ResponseEntity.ok(
                userProfileService.getMyProfile(authentication.getName())
        );
    }

    @PutMapping
    public ResponseEntity<UserProfileDTO> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequestDTO request) {
        return ResponseEntity.ok(
                userProfileService.updateMyProfile(
                        authentication.getName(),
                        request
                )
        );
    }

    @PostMapping(
            value = "/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserProfileDTO> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(
                userProfileService.uploadProfileImage(
                        authentication.getName(),
                        file
                )
        );
    }

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getProfileImage(
            Authentication authentication) {

        User user = userProfileService.getUserWithProfileImage(
                authentication.getName()
        );

        byte[] image = user.getProfileImage();

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String contentType = user.getProfileImageContentType();

        MediaType mediaType;
        if (MediaType.IMAGE_JPEG_VALUE.equalsIgnoreCase(contentType)) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (MediaType.IMAGE_PNG_VALUE.equalsIgnoreCase(contentType)) {
            mediaType = MediaType.IMAGE_PNG;
        } else if ("image/webp".equalsIgnoreCase(contentType)) {
            mediaType = MediaType.parseMediaType("image/webp");
        } else {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Unsupported profile image type"
            );
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .body(image);
    }

    @DeleteMapping("/avatar")
    public ResponseEntity<UserProfileDTO> deleteProfileImage(
            Authentication authentication) {
        return ResponseEntity.ok(
                userProfileService.deleteProfileImage(
                        authentication.getName()
                )
        );
    }
}
