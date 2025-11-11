import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.repository.TaskRepository;
import br.dev.mauriciocaminha.todolist.service.NotificationService;
import br.dev.mauriciocaminha.todolist.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {
    
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService();
        taskService.taskRepository = taskRepository;
        taskService.notificationService = notificationService;
    }

    @Test
    public void create_shouldSaveTask_whenValidTask() {
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(1));
        taskModel.setEndAt(LocalDateTime.now().plusDays(2));
        
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.save(any(TaskModel.class))).thenReturn(taskModel);

        TaskModel result = taskService.create(taskModel, request);

        assertNotNull(result);
        assertEquals(taskModel, result);
        verify(notificationService).notifyTaskCreated(userId, taskModel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_shouldThrowException_whenStartAtIsInThePast() {
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().minusDays(1));
        taskModel.setEndAt(LocalDateTime.now().plusDays(1));

        taskService.create(taskModel, request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_shouldThrowException_whenStartAtIsAfterEndAt() {
        TaskModel taskModel = new TaskModel();
        taskModel.setStartAt(LocalDateTime.now().plusDays(2));
        taskModel.setEndAt(LocalDateTime.now().plusDays(1));

        taskService.create(taskModel, request);
    }

    @Test
    public void listAllByUser_shouldReturnTasks_whenUserHasTasks() {
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        List<TaskModel> tasks = new ArrayList<>();
        tasks.add(new TaskModel());
        when(taskRepository.findByUserId(userId)).thenReturn(tasks);

        List<TaskModel> result = taskService.listAllByUser(request);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test(expected = NoSuchElementException.class)
    public void updateTask_shouldThrowException_whenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        taskService.updateTask(taskId, taskModel, request);
    }

    @Test(expected = SecurityException.class)
    public void updateTask_shouldThrowException_whenUserDoesNotOwnTask() {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        UUID userId = UUID.randomUUID();
        UUID taskUserId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(new TaskModel() {{
            setUserId(taskUserId);
        }}));

        taskService.updateTask(taskId, taskModel, request);
    }

    @Test
    public void deleteTask_shouldDeleteTask_whenUserIsOwner() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(new TaskModel() {{
            setUserId(userId);
        }}));

        taskService.deleteTask(taskId, request);

        verify(taskRepository).deleteById(taskId);
    }

    @Test(expected = NoSuchElementException.class)
    public void deleteTask_shouldThrowException_whenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        taskService.deleteTask(taskId, request);
    }

    @Test(expected = SecurityException.class)
    public void deleteTask_shouldThrowException_whenUserDoesNotOwnTask() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID taskUserId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(new TaskModel() {{
            setUserId(taskUserId);
        }}));

        taskService.deleteTask(taskId, request);
    }

    @Test
    public void markAsCompleted_shouldMarkTaskAsCompleted_whenUserIsOwner() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TaskModel task = new TaskModel();
        task.setUserId(userId);
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(task));

        TaskModel result = taskService.markAsCompleted(taskId, request);

        assertNotNull(result);
        assertNotNull(result.getEndAt());
        verify(taskRepository).save(task);
    }

    @Test(expected = NoSuchElementException.class)
    public void markAsCompleted_shouldThrowException_whenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(UUID.randomUUID());
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.empty());

        taskService.markAsCompleted(taskId, request);
    }

    @Test(expected = SecurityException.class)
    public void markAsCompleted_shouldThrowException_whenUserDoesNotOwnTask() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID taskUserId = UUID.randomUUID();
        when(request.getAttribute("userId")).thenReturn(userId);
        when(taskRepository.findById(taskId)).thenReturn(java.util.Optional.of(new TaskModel() {{
            setUserId(taskUserId);
        }}));

        taskService.markAsCompleted(taskId, request);
    }
}