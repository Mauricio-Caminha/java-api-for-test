package br.dev.mauriciocaminha.todolist.service;

import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.verify;

public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private TaskModel taskModel;

    public NotificationServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNotifyTaskCreated() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        taskModel.setId(taskId);

        notificationService.notifyTaskCreated(userId, taskModel);

        verify(taskModel).getId();
    }

    @Test
    public void testNotifyTaskDeleted() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        notificationService.notifyTaskDeleted(userId, taskId);

        // Verify that the notification was sent (you may need to capture output if necessary)
    }

    @Test
    public void testSendMessage() {
        UUID userId = UUID.randomUUID();
        String message = "Test message";

        notificationService.sendMessage(userId, message);

        // Verify that the message was sent (you may need to capture output if necessary)
    }
}