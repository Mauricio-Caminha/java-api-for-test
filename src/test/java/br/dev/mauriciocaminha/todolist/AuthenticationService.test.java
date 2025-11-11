import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationServiceTest {

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService();
    }

    @Test
    void verifyPassword_shouldReturnTrue_whenPasswordMatches() {
        // Arrange
        UserModel user = new UserModel();
        user.setPassword("$2a$10$e0MYZ9QZ3E1Rz5OZ6LkD8e4Q0U5PqW4aD5W6h4G4t5Q4E3ZyR5R5i"); // Example hashed password
        String rawPassword = "password123"; // This should match the hashed password

        // Act
        boolean result = authenticationService.verifyPassword(user, rawPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void verifyPassword_shouldReturnFalse_whenPasswordDoesNotMatch() {
        // Arrange
        UserModel user = new UserModel();
        user.setPassword("$2a$10$e0MYZ9QZ3E1Rz5OZ6LkD8e4Q0U5PqW4aD5W6h4G4t5Q4E3ZyR5R5i"); // Example hashed password
        String rawPassword = "wrongpassword"; // This should not match the hashed password

        // Act
        boolean result = authenticationService.verifyPassword(user, rawPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void generateBasicAuthHeader_shouldReturnCorrectHeader() {
        // Arrange
        String username = "user";
        String password = "password";

        // Act
        String result = authenticationService.generateBasicAuthHeader(username, password);

        // Assert
        String expectedToken = "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        assertEquals(expectedToken, result);
    }
}