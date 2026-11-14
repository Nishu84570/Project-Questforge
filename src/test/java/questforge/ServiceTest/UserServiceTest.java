
package questforge.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import questforge.dto.UserRequestDTO;
import questforge.dto.UserResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.User;
import questforge.model.User.Role;
import questforge.repository.UserRepository;
import questforge.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO validRequest;

    @BeforeEach
    void setUp() {

        validRequest = new UserRequestDTO();

        validRequest.setName("Nishu Gupta");
        validRequest.setEmail("nishu@gmail.com");
        validRequest.setPassword("Password123");
    }


    @Test
    void createUser_shouldCreateUserSuccessfully() {

        when(userRepository.existsByEmail("nishu@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password123"))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response =
                userService.createUser(validRequest);

        assertEquals("Nishu Gupta", response.getName());
        assertEquals("nishu@gmail.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());
        assertEquals(0L, response.getXp());
        assertEquals(1, response.getLevel());

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("hashedPassword", savedUser.getPassword());

        verify(passwordEncoder).encode("Password123");
    }

    @Test
    void createUser_shouldTrimNameAndNormalizeEmail() {

        validRequest.setName("  Nishu Gupta  ");
        validRequest.setEmail("  NISHU@GMAIL.COM  ");

        when(userRepository.existsByEmail("nishu@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("hashedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response =
                userService.createUser(validRequest);

        assertEquals("Nishu Gupta", response.getName());
        assertEquals("nishu@gmail.com", response.getEmail());
    }

    @Test
    void createUser_shouldThrowExceptionWhenRequestIsNull() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.createUser(null)
                );

        assertEquals("User request is required", exception.getMessage());

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectEmptyName() {

        validRequest.setName("   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRequest)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectNameExceeding100Characters() {

        validRequest.setName("A".repeat(101));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRequest)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectEmptyEmail() {

        validRequest.setEmail("");

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRequest)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectInvalidEmail() {

        validRequest.setEmail("invalid-email");

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRequest)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectEmailExceeding254Characters() {

        validRequest.setEmail("a".repeat(250) + "@example.com");

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRequest)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectEmptyPassword() {

        validRequest.setPassword(" ");

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRequest)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectShortPassword() {

        validRequest.setPassword("1234567");

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRequest)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectPasswordExceeding255Characters() {

        validRequest.setPassword("A".repeat(256));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(validRequest)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void createUser_shouldRejectDuplicateEmail() {

        when(userRepository.existsByEmail("nishu@gmail.com"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.createUser(validRequest)
                );

        assertEquals(
                "An account with this email already exists",
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }



    @Test
    void getAllUsers_shouldReturnAllUsers() {

        User user1 = createMockUser(
                1L, "Nishu", "nishu@gmail.com", 100L
        );

        User user2 = createMockUser(
                2L, "Rahul", "rahul@gmail.com", 250L
        );

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserResponseDTO> response =
                userService.getAllUsers();

        assertEquals(2, response.size());

        assertEquals("Nishu", response.get(0).getName());
        assertEquals(2, response.get(0).getLevel());

        assertEquals("Rahul", response.get(1).getName());
        assertEquals(3, response.get(1).getLevel());

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_shouldReturnEmptyList() {

        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponseDTO> response =
                userService.getAllUsers();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(userRepository).findAll();
    }


    @Test
    void getUserById_shouldReturnUserSuccessfully() {

        User user = createMockUser(
                1L, "Nishu", "nishu@gmail.com", 100L
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponseDTO response =
                userService.getUserById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Nishu", response.getName());
        assertEquals("nishu@gmail.com", response.getEmail());
        assertEquals(100L, response.getXp());
        assertEquals(2, response.getLevel());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(99L)
        );

        verify(userRepository).findById(99L);
    }

    @Test
    void getUserById_shouldRejectNullId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(null)
        );

        verify(userRepository, never()).findById(any());
    }

    @Test
    void getUserById_shouldRejectInvalidId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(0L)
        );

        verify(userRepository, never()).findById(any());
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {

        User existingUser = new User();

        existingUser.setName("Old Name");
        existingUser.setEmail("old@gmail.com");
        existingUser.setPassword("oldHashedPassword");
        existingUser.setXp(100L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByEmail("nishu@gmail.com"))
                .thenReturn(Optional.empty());

        validRequest.setName("Nishu Gupta");
        validRequest.setEmail("nishu@gmail.com");
        validRequest.setPassword(null);

        UserResponseDTO response =
                userService.updateUser(1L, validRequest);

        assertEquals("Nishu Gupta", response.getName());
        assertEquals("nishu@gmail.com", response.getEmail());
        assertEquals(100L, response.getXp());

        assertEquals("oldHashedPassword", existingUser.getPassword());

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("nishu@gmail.com");

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUser_shouldUpdatePasswordWhenProvided() {

        User existingUser = new User();

        existingUser.setName("Old Name");
        existingUser.setEmail("old@gmail.com");
        existingUser.setPassword("oldPassword");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByEmail("nishu@gmail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("NewPassword123"))
                .thenReturn("newHashedPassword");

        validRequest.setPassword("NewPassword123");

        userService.updateUser(1L, validRequest);

        assertEquals(
                "newHashedPassword",
                existingUser.getPassword()
        );

        verify(passwordEncoder).encode("NewPassword123");
    }

    @Test
    void updateUser_shouldRejectNullRequest() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(1L, null)
        );

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    @Test
    void updateUser_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(99L, validRequest)
        );

        verify(userRepository, never()).findByEmail(anyString());
    }

    
@Test
void updateUser_shouldAllowSameUserEmail1() {

    User existingUser = mock(User.class);

    when(existingUser.getId()).thenReturn(1L);
    when(existingUser.getName()).thenReturn("Nishu Gupta");
    when(existingUser.getEmail()).thenReturn("nishu@gmail.com");
    when(existingUser.getXp()).thenReturn(100L);
    when(existingUser.getRole()).thenReturn(Role.USER);

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(existingUser));

    when(userRepository.findByEmail("nishu@gmail.com"))
            .thenReturn(Optional.of(existingUser));

    UserResponseDTO response =
            userService.updateUser(1L, validRequest);

    assertEquals("Nishu Gupta", response.getName());
    assertEquals("nishu@gmail.com", response.getEmail());

    verify(existingUser).setName("Nishu Gupta");
    verify(existingUser).setEmail("nishu@gmail.com");
}


    
@Test
void updateUser_shouldAllowSameUserEmail() {
    User existingUser = spy(new User());

    doReturn(1L).when(existingUser).getId();

    existingUser.setName("Old Name");
    existingUser.setEmail("old@gmail.com");
    existingUser.setXp(100L);

    when(userRepository.findById(1L))
            .thenReturn(Optional.of(existingUser));

    when(userRepository.findByEmail("nishu@gmail.com"))
            .thenReturn(Optional.of(existingUser));

    UserResponseDTO response =
            userService.updateUser(1L, validRequest);

    assertEquals("Nishu Gupta", response.getName());
    assertEquals("nishu@gmail.com", response.getEmail());
    assertEquals(100L, response.getXp());

    verify(userRepository).findById(1L);
    verify(userRepository).findByEmail("nishu@gmail.com");
}


    @Test
    void updateUser_shouldRejectShortPasswordWhenProvided() {

        User existingUser = new User();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByEmail("nishu@gmail.com"))
                .thenReturn(Optional.empty());

        validRequest.setPassword("123");

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(1L, validRequest)
        );

        verifyNoInteractions(passwordEncoder);
    }


    @Test
    void deleteUser_shouldDeleteUserSuccessfully() {

        User user = new User();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(99L)
        );

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void deleteUser_shouldRejectInvalidId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser(-1L)
        );

        verify(userRepository, never()).findById(any());
    }


    @Test
    void addXp_shouldIncreaseUserXpSuccessfully() {

        User user = new User();
        user.setXp(100L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.addXp(1L, 50L);

        assertEquals(150L, user.getXp());

        verify(userRepository).save(user);
    }

    @Test
    void addXp_shouldRejectZeroXp() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.addXp(1L, 0L)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void addXp_shouldRejectNegativeXp() {

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.addXp(1L, -10L)
        );

        verifyNoInteractions(userRepository);
    }

    @Test
    void addXp_shouldRejectOverflow() {

        User user = new User();

        user.setXp(Long.MAX_VALUE - 5);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.addXp(1L, 10L)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addXp_shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.addXp(99L, 100L)
        );

        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void getUserById_shouldCalculateLevelCorrectly() {

        long[] xpValues = {
                0L, 99L, 100L, 249L,
                250L, 449L, 450L, 699L,
                700L, 999L, 1000L, 1499L, 1500L
        };

        int[] expectedLevels = {
                1, 1, 2, 2,
                3, 3, 4, 4,
                5, 5, 6, 6, 7
        };

        for (int i = 0; i < xpValues.length; i++) {

            User user = createMockUser(
                    1L,
                    "Nishu",
                    "nishu@gmail.com",
                    xpValues[i]
            );

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(user));

            UserResponseDTO response =
                    userService.getUserById(1L);

            assertEquals(
                    expectedLevels[i],
                    response.getLevel(),
                    "Incorrect level for XP: " + xpValues[i]
            );
        }

        verify(userRepository, times(xpValues.length))
                .findById(1L);
    }


    private User createMockUser(
            Long id,
            String name,
            String email,
            long xp) {

        User user = mock(User.class);

        when(user.getId()).thenReturn(id);
        when(user.getName()).thenReturn(name);
        when(user.getEmail()).thenReturn(email);
        when(user.getRole()).thenReturn(Role.USER);
        when(user.getXp()).thenReturn(xp);

        return user;
    }
}
