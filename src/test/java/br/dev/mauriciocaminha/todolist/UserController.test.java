package br.dev.mauriciocaminha.todolist;

import br.dev.mauriciocaminha.todolist.controller.UserController;
import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUserSuccessfully() {
        UserModel userModel = new UserModel();
        userModel.setName("John Doe");
        userModel.setUsername("john.doe@example.com");

        when(userService.create(userModel)).thenReturn(userModel);

        ResponseEntity<Object> response = userController.create(userModel);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userModel, response.getBody());
    }

    @Test
    public void testCreateUserBadRequest() {
        UserModel userModel = new UserModel();
        userModel.setName(""); // Assuming name cannot be empty

        when(userService.create(userModel)).thenThrow(new IllegalArgumentException("Invalid user data"));

        ResponseEntity<Object> response = userController.create(userModel);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid user data", response.getBody());
    }
}