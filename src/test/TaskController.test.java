import br.dev.mauriciocaminha.todolist.controller.TaskController;
import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.repository.TaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    private TaskController taskController;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskController = new TaskController();
        taskController.taskRepository = taskRepository;
    }

    @Test
    void create_ShouldReturnCreated_WhenTaskIsValid() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(1));
        taskModel.setEndAt(LocalDateTime.now().plusDays(2));
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        taskModel.setUserId(userId);
        when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);

        // Act
        ResponseEntity<?> response = taskController.create(taskModel, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(taskModel, response.getBody());
    }

    @Test
    void create_ShouldReturnBadRequest_WhenStartDateIsInThePast() {
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
    void create_ShouldReturnBadRequest_WhenStartDateIsAfterEndDate() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(2));
        taskModel.setEndAt(LocalDateTime.now().plusDays(1));
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());

        // Act
        ResponseEntity<?> response = taskController.create(taskModel, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Start date must be before End date.", response.getBody());
    }

    @Test
    void listAllByUser_ShouldReturnTasks_WhenUserHasTasks() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        TaskModel taskModel = new TaskModel();
        taskModel.setUserId(userId);
        when(taskRepository.findByUserId(userId)).thenReturn(Collections.singletonList(taskModel));

        // Act
        ResponseEntity<List<TaskModel>> response = taskController.listAllByUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(taskModel, response.getBody().get(0));
    }

    @Test
    void updateTask_ShouldReturnOk_WhenTaskIsUpdated() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        TaskModel existingTask = new TaskModel();
        existingTask.setUserId(userId);
        TaskModel updatedTaskModel = new TaskModel();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(TaskModel.class))).thenReturn(existingTask);

        // Act
        ResponseEntity<?> response = taskController.updateTask(updatedTaskModel, request, taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingTask, response.getBody());
    }

    @Test
    void updateTask_ShouldReturnNotFound_WhenTaskDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = taskController.updateTask(new TaskModel(), request, taskId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found.", response.getBody());
    }

    @Test
    void updateTask_ShouldReturnForbidden_WhenUserDoesNotOwnTask() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        TaskModel existingTask = new TaskModel();
        existingTask.setUserId(UUID.randomUUID()); // Different user
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // Act
        ResponseEntity<?> response = taskController.updateTask(new TaskModel(), request, taskId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You do not have permission to update this task.", response.getBody());
    }
}