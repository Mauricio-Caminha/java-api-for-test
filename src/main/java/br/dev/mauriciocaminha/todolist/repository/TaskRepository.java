package br.dev.mauriciocaminha.todolist.repository;

import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findByUserId(UUID userId);
    List<TaskModel> findByUserIdAndPriority(UUID userId, String priority);
}
