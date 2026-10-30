package questforge.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import questforge.dto.LoginRequestDTO;
import questforge.dto.LoginResponseDTO;
import questforge.model.User;
import questforge.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(
            LoginRequestDTO request) {

        // ==========================================
        // 1. FIND USER BY EMAIL
        // ==========================================

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        // ==========================================
        // 2. CHECK PASSWORD
        // ==========================================

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        // ==========================================
        // 3. GENERATE JWT
        // ==========================================

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        // ==========================================
        // 4. RETURN LOGIN RESPONSE
        // ==========================================

        return new LoginResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}