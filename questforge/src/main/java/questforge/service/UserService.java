package questforge.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import questforge.dto.UserRequestDTO;
import questforge.dto.UserResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.User;
import questforge.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // =========================
    // CONSTRUCTOR
    // =========================

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;

        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // CREATE USER
    // =========================

    public UserResponseDTO createUser(
            UserRequestDTO request) {

        User user = new User();

        user.setName(
                request.getName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        // Every newly registered user
        // is always a USER.
        user.setRole("USER");

        // Every new user starts with
        // 0 XP.
        user.setXp(0);

        User savedUser =
                userRepository.save(user);

        return convertToResponseDTO(
                savedUser
        );
    }

    // =========================
    // GET ALL USERS
    // =========================

    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // =========================
    // GET USER BY ID
    // =========================

    public UserResponseDTO getUserById(
            Integer id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + id
                                )
                        );

        return convertToResponseDTO(
                user
        );
    }

    // =========================
    // UPDATE USER
    // =========================

    public UserResponseDTO updateUser(
            Integer id,
            UserRequestDTO request) {

        User existingUser =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + id
                                )
                        );

        existingUser.setName(
                request.getName()
        );

        existingUser.setEmail(
                request.getEmail()
        );

        existingUser.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        existingUser.setRole(
                request.getRole()
        );

        /*
         * XP is intentionally NOT changed here.
         *
         * XP should only be controlled by
         * the gamification system.
         */

        User updatedUser =
                userRepository.save(
                        existingUser
                );

        return convertToResponseDTO(
                updatedUser
        );
    }

    // =========================
    // DELETE USER
    // =========================

    public void deleteUser(
            Integer id) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + id
                                )
                        );

        userRepository.delete(user);
    }

    // =========================
    // ADD XP
    // =========================

    public void addXp(
            Integer userId,
            int xpAmount) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + userId
                                )
                        );

        int currentXp =
                user.getXp() == null
                        ? 0
                        : user.getXp();

        user.setXp(
                currentXp + xpAmount
        );

        userRepository.save(user);
    }

    // =========================
    // CALCULATE LEVEL
    // =========================

    private int calculateLevel(
            Integer xp) {

        if (xp == null || xp < 100) {
            return 1;
        }

        if (xp < 250) {
            return 2;
        }

        if (xp < 450) {
            return 3;
        }

        if (xp < 700) {
            return 4;
        }

        if (xp < 1000) {
            return 5;
        }

        return 6 + ((xp - 1000) / 500);
    }

    // =========================
    // ENTITY → RESPONSE DTO
    // =========================

    private UserResponseDTO convertToResponseDTO(
            User user) {

        int level =
                calculateLevel(
                        user.getXp()
                );

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getXp(),
                level
        );
    }
}