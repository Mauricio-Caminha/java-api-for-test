package br.dev.mauriciocaminha.todolist.repository;

import br.dev.mauriciocaminha.todolist.entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    UserModel findByUsername(String username);
}
