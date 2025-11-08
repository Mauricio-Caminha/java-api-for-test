package br.dev.mauriciocaminha.todolist.service;

import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {

    // Simple placeholder notification service that logs to stdout.
    public void notifyTaskCreated(UUID userId, TaskModel task) {
        System.out.println("[Notification] User: " + userId + " created task: " + task.getId());
    }

    // New: notify task deleted
    public void notifyTaskDeleted(UUID userId, UUID taskId) {
        System.out.println("[Notification] User: " + userId + " deleted task: " + taskId);
    }

    // New: send generic message
    public void sendMessage(UUID userId, String message) {
        System.out.println("[Notification] User: " + userId + " message: " + message);
    }
}
