
package questforge.repositary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import questforge.model.User;
import questforge.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("encoded-test-password");
        user.setRole(User.Role.USER);
        user.setXp(100L);

        return user;
    }

    @Test
    void shouldSaveAndFindUserById() {
        User saved = userRepository.save(
                createUser("Rahul", "rahul@example.com")
        );

        User found = userRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("Rahul");
        assertThat(found.getEmail()).isEqualTo("rahul@example.com");
        assertThat(found.getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    void shouldFindUserByEmail() {
        userRepository.save(
                createUser("Rahul", "rahul@example.com")
        );

        var result = userRepository.findByEmail("rahul@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Rahul");
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        assertThat(userRepository.findByEmail("missing@example.com"))
                .isEmpty();
    }

    @Test
    void shouldCheckWhetherEmailExists() {
        userRepository.save(
                createUser("Rahul", "rahul@example.com")
        );

        assertThat(userRepository.existsByEmail("rahul@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("missing@example.com")).isFalse();
    }
}
