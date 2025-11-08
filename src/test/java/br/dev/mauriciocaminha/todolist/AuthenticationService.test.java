package br.dev.mauriciocaminha.todolist;

import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserModel userModel;

    public AuthenticationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testVerifyPassword_Success() {
        String rawPassword = "password123";
        String hashedPassword = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());

        when(userModel.getPassword()).thenReturn(hashedPassword);

        boolean result = authenticationService.verifyPassword(userModel, rawPassword);

        assertTrue(result);
    }

    @Test
    public void testVerifyPassword_Failure() {
        String rawPassword = "wrongPassword";
        String hashedPassword = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, "password123".toCharArray());

        when(userModel.getPassword()).thenReturn(hashedPassword);

        boolean result = authenticationService.verifyPassword(userModel, rawPassword);

        assertFalse(result);
    }

    @Test
    public void testGenerateBasicAuthHeader() {
        String username = "user";
        String password = "pass";
        String expectedHeader = "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        String result = authenticationService.generateBasicAuthHeader(username, password);

        assertTrue(result.equals(expectedHeader));
    }
}