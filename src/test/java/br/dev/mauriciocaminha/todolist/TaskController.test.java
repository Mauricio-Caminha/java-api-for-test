import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.repository.TaskRepository;
import br.dev.mauriciocaminha.todolist.controller.TaskController;
import br.dev.mauriciocaminha.todolist.utils.Utils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldReturnBadRequest_whenStartAtIsInThePast() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().minusDays(1));
        taskModel.setEndAt(LocalDateTime.now().plusDays(1));
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());

        // Act
        ResponseEntity<?> response = taskController.create(taskModel, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Start date and End date must be in the future.", response.getBody());
    }

    @Test
    void create_shouldReturnBadRequest_whenStartAtIsAfterEndAt() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(1));
        taskModel.setEndAt(LocalDateTime.now());
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());

        // Act
        ResponseEntity<?> response = taskController.create(taskModel, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Start date must be before End date.", response.getBody());
    }

    @Test
    void create_shouldReturnCreated_whenTaskIsValid() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(1));
        taskModel.setEndAt(LocalDateTime.now().plusDays(2));
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());
        when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);

        // Act
        ResponseEntity<?> response = taskController.create(taskModel, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(taskModel, response.getBody());
    }

    @Test
    void listAllByUser_shouldReturnTasks_whenUserHasTasks() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findByUserId(userId)).thenReturn(Collections.singletonList(new TaskModel()));

        // Act
        ResponseEntity<List<TaskModel>> response = taskController.listAllByUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateTask_shouldReturnNotFound_whenTaskDoesNotExist() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = taskController.updateTask(taskModel, request, taskId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found.", response.getBody());
    }

    @Test
    void updateTask_shouldReturnForbidden_whenUserDoesNotOwnTask() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        TaskModel existingTask = new TaskModel();
        existingTask.setUserId(UUID.randomUUID());
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // Act
        ResponseEntity<?> response = taskController.updateTask(taskModel, request, taskId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You do not have permission to update this task.", response.getBody());
    }

    @Test
    void updateTask_shouldReturnOk_whenTaskIsUpdated() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        TaskModel existingTask = new TaskModel();
        existingTask.setUserId(UUID.randomUUID());
        when(request.getAttribute("userId")).thenReturn(existingTask.getUserId());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(TaskModel.class))).thenReturn(existingTask);

        // Act
        ResponseEntity<?> response = taskController.updateTask(taskModel, request, taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingTask, response.getBody());
    }
}