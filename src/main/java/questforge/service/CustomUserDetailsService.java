
package questforge.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import questforge.model.User;
import questforge.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Invalid email or password"));

        String role = user.getRole();

        if (role == null
                || !role.matches("(?i)USER|EMPLOYEE|ADMIN")) {
            throw new UsernameNotFoundException(
                    "User account has an invalid role");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(role.toUpperCase())
                .build();
    }
}
