package br.dev.mauriciocaminha.todolist.service;

import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UserModel userModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userModel = new UserModel();
        userModel.setUsername("testUser");
        userModel.setPassword("testPassword");
    }

    @Test
    void create_shouldThrowException_whenUserAlreadyExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(userModel);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.create(userModel);
        });
    }

    @Test
    void create_shouldSaveUser_whenUserDoesNotExist() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        UserModel createdUser = userService.create(userModel);

        Assertions.assertNotNull(createdUser);
        verify(userRepository).save(any(UserModel.class));
    }

    @Test
    void findByUsername_shouldReturnUser_whenUserExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(userModel);

        UserModel foundUser = userService.findByUsername("testUser");

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void findById_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            userService.findById(userId);
        });
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        UserModel foundUser = userService.findById(userId);

        Assertions.assertNotNull(foundUser);
        Assertions.assertEquals(userModel, foundUser);
    }

    @Test
    void deleteUser_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            userService.deleteUser(userId);
        });
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void updateUser_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UserModel incoming = new UserModel();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchElementException.class, () -> {
            userService.updateUser(userId, incoming);
        });
    }

    @Test
    void updateUser_shouldUpdateUser_whenUserExists() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userModel));
        when(userRepository.save(any(UserModel.class))).thenReturn(userModel);

        UserModel updatedUser = userService.updateUser(userId, userModel);

        Assertions.assertNotNull(updatedUser);
        verify(userRepository).save(any(UserModel.class));
    }
}