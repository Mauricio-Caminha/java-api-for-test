package br.dev.mauriciocaminha.todolist.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.dev.mauriciocaminha.todolist.entities.UserModel;
import br.dev.mauriciocaminha.todolist.repository.UserRepository;
import br.dev.mauriciocaminha.todolist.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserModel create(UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if(user != null) {
            throw new IllegalArgumentException("User already exists.");
        }

        var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashed);

        var userCreated =  this.userRepository.save(userModel);
        return userCreated;
    }

    public UserModel findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    // New: find user by id
    public UserModel findById(UUID userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found."));
    }

    // New: delete user
    public void deleteUser(UUID userId) {
        var user = this.userRepository.findById(userId).orElse(null);
        if (user == null) throw new NoSuchElementException("User not found.");
        this.userRepository.deleteById(userId);
    }

    // New: update user (copy non-null properties except password unless provided)
    public UserModel updateUser(UUID userId, UserModel incoming) {
        var user = this.userRepository.findById(userId).orElse(null);
        if (user == null) throw new NoSuchElementException("User not found.");

        // If password present, hash it
        if (incoming.getPassword() != null && !incoming.getPassword().isBlank()) {
            var passwordHashed = BCrypt.withDefaults().hashToString(12, incoming.getPassword().toCharArray());
            incoming.setPassword(passwordHashed);
        } else {
            incoming.setPassword(user.getPassword());
        }

        // copy non-null (BeanUtils used only for remaining fields)
        BeanUtils.copyProperties(incoming, user, Utils.getNullPropertyNames(incoming));

        var updated = this.userRepository.save(user);
        return updated;
    }
}
