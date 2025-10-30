import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.dev.mauriciocaminha.todolist.controller.UserController;
import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    void create_shouldReturnBadRequest_whenUserAlreadyExists() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("existingUser");
        userModel.setPassword("password");

        when(userRepository.findByUsername("existingUser")).thenReturn(userModel);

        // Act
        ResponseEntity<?> response = userController.create(userModel);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists.", response.getBody());
    }

    @Test
    void create_shouldReturnCreated_whenUserIsNew() {
        // Arrange
        UserModel userModel = new UserModel();
        userModel.setUsername("newUser");
        userModel.setPassword("password");

        when(userRepository.findByUsername("newUser")).thenReturn(null);
        when(userRepository.save(userModel)).thenReturn(userModel);

        // Act
        ResponseEntity<?> response = userController.create(userModel);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userModel, response.getBody());
    }
}