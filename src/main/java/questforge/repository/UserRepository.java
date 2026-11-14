
package questforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import questforge.dto.AdminUserResponseDTO;
import questforge.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Iterable<AdminUserResponseDTO> findByNameContainingIgnoreCase(String normalizedQuery);
}
