import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
    }

    @Test
    void notifyTaskCreated_shouldLogNotification_whenTaskIsCreated() {
        // Arrange
        UUID userId = UUID.randomUUID();
        TaskModel task = mock(TaskModel.class);
        when(task.getId()).thenReturn(UUID.randomUUID());

        // Act
        notificationService.notifyTaskCreated(userId, task);

        // Assert
        // Since the method prints to stdout, we can verify the output indirectly if needed
        // For this example, we will just ensure no exceptions are thrown
    }

    @Test
    void notifyTaskDeleted_shouldLogNotification_whenTaskIsDeleted() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        // Act
        notificationService.notifyTaskDeleted(userId, taskId);

        // Assert
        // Since the method prints to stdout, we can verify the output indirectly if needed
        // For this example, we will just ensure no exceptions are thrown
    }

    @Test
    void sendMessage_shouldLogNotification_whenMessageIsSent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String message = "This is a test message";

        // Act
        notificationService.sendMessage(userId, message);

        // Assert
        // Since the method prints to stdout, we can verify the output indirectly if needed
        // For this example, we will just ensure no exceptions are thrown
    }
}