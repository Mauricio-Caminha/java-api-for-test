import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.repository.UserRepository;
import br.dev.mauriciocaminha.todolist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldThrowException_whenUserAlreadyExists() {
        // Arrange
        UserModel existingUser = new UserModel();
        existingUser.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(existingUser);

        UserModel newUser = new UserModel();
        newUser.setUsername("john");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.create(newUser));
        assertEquals("User already exists.", exception.getMessage());
    }

    @Test
    void create_shouldSaveUser_whenUserDoesNotExist() {
        // Arrange
        UserModel newUser = new UserModel();
        newUser.setUsername("john");
        newUser.setPassword("password");

        when(userRepository.findByUsername("john")).thenReturn(null);
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserModel createdUser = userService.create(newUser);

        // Assert
        assertNotNull(createdUser);
        assertEquals("john", createdUser.getUsername());
        verify(userRepository).save(any(UserModel.class));
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        // Arrange
        UserModel user = new UserModel();
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(user);

        // Act
        UserModel foundUser = userService.findByUsername("john");

        // Assert
        assertNotNull(foundUser);
        assertEquals("john", foundUser.getUsername());
    }

    @Test
    void findByUsername_shouldReturnNull_whenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("john")).thenReturn(null);

        // Act
        UserModel foundUser = userService.findByUsername("john");

        // Assert
        assertNull(foundUser);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserModel user = new UserModel();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserModel foundUser = userService.findById(userId);

        // Assert
        assertNotNull(foundUser);
    }

    @Test
    void findById_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userService.findById(userId));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserModel user = new UserModel();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userService.deleteUser(userId));
        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void updateUser_shouldUpdateUser_whenUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserModel existingUser = new UserModel();
        existingUser.setPassword("oldPassword");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserModel incomingUser = new UserModel();
        incomingUser.setUsername("john");
        incomingUser.setPassword("newPassword");

        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserModel updatedUser = userService.updateUser(userId, incomingUser);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("john", updatedUser.getUsername());
        assertNotEquals("oldPassword", updatedUser.getPassword());
        verify(userRepository).save(any(UserModel.class));
    }

    @Test
    void updateUser_shouldThrowException_whenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserModel incomingUser = new UserModel();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> userService.updateUser(userId, incomingUser));
        assertEquals("User not found.", exception.getMessage());
    }
}