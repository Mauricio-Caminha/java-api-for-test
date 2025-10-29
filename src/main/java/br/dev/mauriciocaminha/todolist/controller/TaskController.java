package br.dev.mauriciocaminha.todolist.controller;

import br.dev.mauriciocaminha.todolist.entities.TaskModel;
import br.dev.mauriciocaminha.todolist.repository.TaskRepository;
import br.dev.mauriciocaminha.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        taskModel.setUserId(userId);

        var currentData = LocalDateTime.now();
        if (currentData.isAfter(taskModel.getStartAt()) || currentData.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date and End date must be in the future.");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be before End date.");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);

    }

    @GetMapping("/")
    public ResponseEntity<List<TaskModel>> listAllByUser(HttpServletRequest request) {
        var userId = (UUID) request.getAttribute("userId");
        var tasks = this.taskRepository.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity updateTask(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID taskId) {
        var userId = (UUID) request.getAttribute("userId");

        var task = this.taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found.");
        }
        if (!task.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update this task.");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var updatedTask = this.taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }


}
