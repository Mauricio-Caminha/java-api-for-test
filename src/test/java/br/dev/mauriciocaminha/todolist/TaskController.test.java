package br.dev.mauriciocaminha.todolist;

import br.dev.mauriciocaminha.todolist.controller.TaskController;
import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setName("Test Task");

        when(taskService.create(any(TaskModel.class), any(HttpServletRequest.class))).thenReturn(taskModel);

        ResponseEntity<Object> response = taskController.create(taskModel, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(taskModel, response.getBody());
    }

    @Test
    void testCreateTaskBadRequest() {
        TaskModel taskModel = new TaskModel();

        when(taskService.create(any(TaskModel.class), any(HttpServletRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid task data"));

        ResponseEntity<Object> response = taskController.create(taskModel, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid task data", response.getBody());
    }

    @Test
    void testListAllByUser() {
        List<TaskModel> tasks = new ArrayList<>();
        TaskModel taskModel = new TaskModel();
        taskModel.setId(UUID.randomUUID());
        taskModel.setName("Test Task");
        tasks.add(taskModel);

        when(taskService.listAllByUser(any(HttpServletRequest.class))).thenReturn(tasks);

        ResponseEntity<List<TaskModel>> response = taskController.listAllByUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }

    @Test
    void testUpdateTask() {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();
        taskModel.setName("Updated Task");

        when(taskService.updateTask(any(UUID.class), any(TaskModel.class), any(HttpServletRequest.class)))
                .thenReturn(taskModel);

        ResponseEntity<Object> response = taskController.updateTask(taskModel, request, taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(taskModel, response.getBody());
    }

    @Test
    void testUpdateTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();

        when(taskService.updateTask(any(UUID.class), any(TaskModel.class), any(HttpServletRequest.class)))
                .thenThrow(new NoSuchElementException("Task not found"));

        ResponseEntity<Object> response = taskController.updateTask(taskModel, request, taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found", response.getBody());
    }

    @Test
    void testUpdateTaskForbidden() {
        UUID taskId = UUID.randomUUID();
        TaskModel taskModel = new TaskModel();

        when(taskService.updateTask(any(UUID.class), any(TaskModel.class), any(HttpServletRequest.class)))
                .thenThrow(new SecurityException("Access denied"));

        ResponseEntity<Object> response = taskController.updateTask(taskModel, request, taskId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody());
    }
}