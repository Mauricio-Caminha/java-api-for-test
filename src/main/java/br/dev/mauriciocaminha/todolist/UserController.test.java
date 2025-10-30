import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import br.dev.mauriciocaminha.todolist.controller.UserController;
import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.repository.UserRepository;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("User already exists.");
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(userModel);
    }
}