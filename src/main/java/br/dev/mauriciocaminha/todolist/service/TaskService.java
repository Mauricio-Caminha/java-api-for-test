package br.dev.mauriciocaminha.todolist.service;

import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.repository.TaskRepository;
import br.dev.mauriciocaminha.todolist.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationService notificationService;

    public TaskModel create(TaskModel taskModel, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        taskModel.setUserId(userId);

        var currentData = LocalDateTime.now();
        if (currentData.isAfter(taskModel.getStartAt()) || currentData.isAfter(taskModel.getEndAt())) {
            throw new IllegalArgumentException("Start date and End date must be in the future.");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            throw new IllegalArgumentException("Start date must be before End date.");
        }

        var task = this.taskRepository.save(taskModel);

        // Notify (simple in-memory/console notification)
        try {
            this.notificationService.notifyTaskCreated(userId, task);
        } catch (Exception ignored) { }

        return task;
    }

    public List<TaskModel> listAllByUser(HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        return this.taskRepository.findByUserId(userId);
    }

    public TaskModel updateTask(UUID taskId, TaskModel taskModel, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");

        var task = this.taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new NoSuchElementException("Task not found.");
        }
        if (!task.getUserId().equals(userId)) {
            throw new SecurityException("You do not have permission to update this task.");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var updatedTask = this.taskRepository.save(task);

        return updatedTask;
    }

    // New helper: find task by id and ensure ownership
    public TaskModel findById(UUID taskId, UUID userId) {
        var task = this.taskRepository.findById(taskId).orElse(null);
        if (task == null) throw new NoSuchElementException("Task not found.");
        if (!task.getUserId().equals(userId)) throw new SecurityException("Forbidden");
        return task;
    }

    // New helper: delete a task by id (only owner)
    public void deleteTask(UUID taskId, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        var task = this.taskRepository.findById(taskId).orElse(null);
        if (task == null) throw new NoSuchElementException("Task not found.");
        if (!task.getUserId().equals(userId)) throw new SecurityException("Forbidden");
        this.taskRepository.deleteById(taskId);
    }

    // New: list tasks by priority for current user
    public List<TaskModel> listByPriority(String priority, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        return this.taskRepository.findByUserIdAndPriority(userId, priority);
    }

    // New: mark task as completed (set endAt to now)
    public TaskModel markAsCompleted(UUID taskId, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        var task = this.taskRepository.findById(taskId).orElse(null);
        if (task == null) throw new NoSuchElementException("Task not found.");
        if (!task.getUserId().equals(userId)) throw new SecurityException("Forbidden");

        task.setEndAt(LocalDateTime.now());
        var updated = this.taskRepository.save(task);
        return updated;
    }
}
