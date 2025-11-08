package br.dev.mauriciocaminha.todolist.service;

import br.dev.mauriciocaminha.todolist.entities.UserModel;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    // Simple wrapper for authentication logic used by the filter
    public boolean verifyPassword(UserModel user, String rawPassword) {
        var verifier = at.favre.lib.crypto.bcrypt.BCrypt.verifyer();
        return verifier.verify(rawPassword.toCharArray(), user.getPassword()).verified;
    }

    // New helper: build Basic auth header from username/password
    public String generateBasicAuthHeader(String username, String password) {
        var token = java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + token;
    }
}
