import br.dev.mauriciocaminha.todolist.controller.TaskController;
import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.repository.TaskRepository;
import br.dev.mauriciocaminha.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    void create_shouldReturnBadRequest_whenStartDateIsInThePast() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().minusDays(1));
        taskModel.setEndAt(LocalDateTime.now().plusDays(1));
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);

        // Act
        var response = taskController.create(taskModel, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Start date and End date must be in the future.");
    }

    @Test
    void create_shouldReturnBadRequest_whenStartDateIsAfterEndDate() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(1));
        taskModel.setEndAt(LocalDateTime.now());
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);

        // Act
        var response = taskController.create(taskModel, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Start date must be before End date.");
    }

    @Test
    void create_shouldReturnCreated_whenTaskIsValid() {
        // Arrange
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(1));
        taskModel.setEndAt(LocalDateTime.now().plusDays(2));
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);

        // Act
        var response = taskController.create(taskModel, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(taskModel);
    }

    @Test
    void listAllByUser_shouldReturnOk_whenTasksExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findByUserId(userId)).thenReturn(Collections.singletonList(new TaskModel()));

        // Act
        var response = taskController.listAllByUser(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void updateTask_shouldReturnNotFound_whenTaskDoesNotExist() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act
        var response = taskController.updateTask(taskModel, request, taskId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Task not found.");
    }

    @Test
    void updateTask_shouldReturnForbidden_whenUserDoesNotOwnTask() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        TaskModel existingTask = new TaskModel();
        existingTask.setUserId(UUID.randomUUID());
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        // Act
        var response = taskController.updateTask(taskModel, request, taskId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isEqualTo("You do not have permission to update this task.");
    }

    @Test
    void updateTask_shouldReturnOk_whenTaskIsUpdatedSuccessfully() {
        // Arrange
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        TaskModel existingTask = new TaskModel();
        existingTask.setUserId(UUID.randomUUID());
        UUID userId = existingTask.getUserId();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(TaskModel.class))).thenReturn(existingTask);

        // Act
        var response = taskController.updateTask(taskModel, request, taskId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(existingTask);
    }
}