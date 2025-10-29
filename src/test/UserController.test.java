import br.dev.mauriciocaminha.todolist.controller.UserController;
import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnBadRequestWhenUserAlreadyExists() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("existingUser");
        when(userRepository.findByUsername("existingUser")).thenReturn(userModel);

        // Act
        ResponseEntity<?> response = userController.create(userModel);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists.", response.getBody());
    }

    @Test
    void shouldCreateUserWhenUserDoesNotExist() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("newUser");
        userModel.setPassword("password123");
        when(userRepository.findByUsername("newUser")).thenReturn(null);
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        // Act
        ResponseEntity<?> response = userController.create(userModel);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userModel, response.getBody());
        verify(userRepository).save(any(UserModel.class));
    }
}