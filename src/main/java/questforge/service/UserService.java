package questforge.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import questforge.dto.UserRequestDTO;
import questforge.dto.UserResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.User;
import questforge.repository.UserRepository;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("User request is required");
        }

        String name = requireName(request.getName());
        String email = normalizeEmail(request.getEmail());
        String password = requirePassword(request.getPassword());

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists"
            );
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setXp(0L);

        User savedUser = userRepository.save(user);
        return convertToResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        return convertToResponseDTO(findUserById(id));
    }

    @Transactional
    public UserResponseDTO updateUser(
            Long id,
            UserRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException("User request is required");
        }

        User existingUser = findUserById(id);

        String name = requireName(request.getName());
        String email = normalizeEmail(request.getEmail());

        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "An account with this email already exists"
                    );
                });

        existingUser.setName(name);
        existingUser.setEmail(email);

        
        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            String password = requirePassword(request.getPassword());
            existingUser.setPassword(passwordEncoder.encode(password));
        }

       
        User updatedUser = userRepository.save(existingUser);
        return convertToResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    @Transactional
    public void addXp(Long userId, long xpAmount) {
        if (xpAmount <= 0L) {
            throw new IllegalArgumentException(
                    "XP amount must be greater than 0"
            );
        }

        User user = findUserById(userId);

        long currentXp = user.getXp();

        if (Long.MAX_VALUE - currentXp < xpAmount) {
            throw new IllegalArgumentException("XP total exceeds the allowed range");
        }

        user.setXp(currentXp + xpAmount);
        userRepository.save(user);
    }

    private User findUserById(Long id) {
        if (id == null || id <= 0L) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        String trimmed = name.trim();

        if (trimmed.length() > 100) {
            throw new IllegalArgumentException(
                    "Name cannot exceed 100 characters"
            );
        }

        return trimmed;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String normalized = email.trim().toLowerCase(Locale.ROOT);

        if (normalized.length() > 254 || !normalized.matches(
                "^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$"
        )) {
            throw new IllegalArgumentException("Invalid email address");
        }

        return normalized;
    }
    private String requirePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() < 8 || password.length() > 255) {
            throw new IllegalArgumentException(
                    "Password must be between 8 and 255 characters"
            );
        }

        return password;
    }

    private int calculateLevel(long xp) {
        if (xp < 100L) return 1;
        if (xp < 250L) return 2;
        if (xp < 450L) return 3;
        if (xp < 700L) return 4;
        if (xp < 1000L) return 5;

        long level = 6L + ((xp - 1000L) / 500L);
        return (int) Math.min(level, Integer.MAX_VALUE);
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getXp(),
                calculateLevel(user.getXp())
        );
    }
}