package questforge.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import questforge.dto.AdminSubmissionResponseDTO;
import questforge.dto.AdminUserResponseDTO;
import questforge.dto.AdminUserRoleRequestDTO;
import questforge.dto.AdminUserXpRequestDTO;
import questforge.service.AdminUserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService) {

        this.adminUserService =
                adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponseDTO>>
    getAllUsers() {

        return ResponseEntity.ok(
                adminUserService.getAllUsers()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<AdminUserResponseDTO>>
    searchUsers(
            @RequestParam String query) {

        return ResponseEntity.ok(
                adminUserService.searchUsers(query)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserResponseDTO>
    getUserById(
            @PathVariable Integer userId) {

        return ResponseEntity.ok(
                adminUserService.getUserById(userId)
        );
    }

    @GetMapping("/{userId}/submissions")
    public ResponseEntity<List<AdminSubmissionResponseDTO>>
    getUserSubmissions(
            @PathVariable Integer userId) {

        return ResponseEntity.ok(
                adminUserService.getUserSubmissions(userId)
        );
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<AdminUserResponseDTO>
    updateUserRole(
            @PathVariable Integer userId,
            @Valid @RequestBody AdminUserRoleRequestDTO request) {

        return ResponseEntity.ok(
                adminUserService.updateUserRole(
                        userId,
                        request.getRole()
                )
        );
    }

    @PostMapping("/{userId}/xp")
    public ResponseEntity<AdminUserResponseDTO>
    addUserXp(
            @PathVariable Integer userId,
            @Valid @RequestBody AdminUserXpRequestDTO request) {

        return ResponseEntity.ok(
                adminUserService.addUserXp(
                        userId,
                        request.getXp()
                )
        );
    }
}