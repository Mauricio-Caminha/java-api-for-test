package br.dev.mauriciocaminha.todolist.service;

import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.repository.TaskRepository;
import br.dev.mauriciocaminha.todolist.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.any(UUID.class);
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private HttpServletRequest request;

    private UUID userId;
    private TaskModel taskModel;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(1));
        taskModel.setEndAt(LocalDateTime.now().plusDays(2));
        when(request.getAttribute("userId")).thenReturn(userId);
    }

    @Test
    void create_ShouldSaveTask_WhenValid() {
        when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);

        TaskModel createdTask = taskService.create(taskModel, request);

        assertNotNull(createdTask);
        assertEquals(userId, createdTask.getUserId());
        Mockito.verify(notificationService).notifyTaskCreated(userId, createdTask);
    }

    @Test
    void create_ShouldThrowException_WhenStartAtIsInThePast() {
        taskModel.setStartAt(LocalDateTime.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> taskService.create(taskModel, request));
    }

    @Test
    void create_ShouldThrowException_WhenStartAtIsAfterEndAt() {
        taskModel.setEndAt(LocalDateTime.now().plusDays(1));
        taskModel.setStartAt(LocalDateTime.now().plusDays(2));
        assertThrows(IllegalArgumentException.class, () -> taskService.create(taskModel, request));
    }

    @Test
    void listAllByUser_ShouldReturnTasks_WhenUserHasTasks() {
        when(taskRepository.findByUserId(userId)).thenReturn(Collections.singletonList(taskModel));

        var tasks = taskService.listAllByUser(request);

        assertEquals(1, tasks.size());
        assertEquals(taskModel, tasks.get(0));
    }

    @Test
    void updateTask_ShouldUpdateTask_WhenUserIsOwner() {
        UUID taskId = UUID.randomUUID();
        taskModel.setUserId(userId);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(taskModel));
        when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);

        TaskModel updatedTask = taskService.updateTask(taskId, taskModel, request);

        assertNotNull(updatedTask);
        assertEquals(taskModel, updatedTask);
    }

    @Test
    void updateTask_ShouldThrowException_WhenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () -> taskService.updateTask(taskId, taskModel, request));
    }

    @Test
    void updateTask_ShouldThrowException_WhenUserIsNotOwner() {
        UUID taskId = UUID.randomUUID();
        TaskModel anotherTask = new TaskModel();
        anotherTask.setUserId(UUID.randomUUID());
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(anotherTask));

        assertThrows(SecurityException.class, () -> taskService.updateTask(taskId, taskModel, request));
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenUserIsOwner() {
        UUID taskId = UUID.randomUUID();
        taskModel.setUserId(userId);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(taskModel));

        assertDoesNotThrow(() -> taskService.deleteTask(taskId, request));
        Mockito.verify(taskRepository).deleteById(taskId);
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () -> taskService.deleteTask(taskId, request));
    }

    @Test
    void deleteTask_ShouldThrowException_WhenUserIsNotOwner() {
        UUID taskId = UUID.randomUUID();
        TaskModel anotherTask = new TaskModel();
        anotherTask.setUserId(UUID.randomUUID());
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(anotherTask));

        assertThrows(SecurityException.class, () -> taskService.deleteTask(taskId, request));
    }

    @Test
    void markAsCompleted_ShouldMarkTaskAsCompleted_WhenUserIsOwner() {
        UUID taskId = UUID.randomUUID();
        taskModel.setUserId(userId);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(taskModel));
        when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);

        TaskModel completedTask = taskService.markAsCompleted(taskId, request);

        assertNotNull(completedTask);
        assertNotNull(completedTask.getEndAt());
    }

    @Test
    void markAsCompleted_ShouldThrowException_WhenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        assertThrows(NoSuchElementException.class, () -> taskService.markAsCompleted(taskId, request));
    }

    @Test
    void markAsCompleted_ShouldThrowException_WhenUserIsNotOwner() {
        UUID taskId = UUID.randomUUID();
        TaskModel anotherTask = new TaskModel();
        anotherTask.setUserId(UUID.randomUUID());
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(anotherTask));

        assertThrows(SecurityException.class, () -> taskService.markAsCompleted(taskId, request));
    }
}