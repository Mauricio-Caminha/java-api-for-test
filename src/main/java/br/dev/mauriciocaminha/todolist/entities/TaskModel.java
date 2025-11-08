package br.dev.mauriciocaminha.todolist.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "priority", nullable = false)
    private String priority;

    @Column(name = "startAt")
    private LocalDateTime startAt;

    @Column(name = "endAt")
    private LocalDateTime endAt;

    @CreationTimestamp
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    public void setTitle(String title) throws Exception {
        if(title.length() > 50) {
            throw new Exception("Title mus be at most 50 characters long.");
        }
        this.title = title;
    }

    // Explicit getters/setters to avoid relying solely on Lombok during compile
    public UUID getId() { return this.id; }
    public UUID getUserId() { return this.userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public LocalDateTime getStartAt() { return this.startAt; }
    public LocalDateTime getEndAt() { return this.endAt; }
    public void setEndAt(LocalDateTime endAt) { this.endAt = endAt; }
}
