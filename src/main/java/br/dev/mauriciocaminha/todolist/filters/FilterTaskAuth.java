package br.dev.mauriciocaminha.todolist.filters;

import br.dev.mauriciocaminha.todolist.repository.UserRepository;
import br.dev.mauriciocaminha.todolist.service.AuthenticationService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var serveletPath = request.getServletPath();

        if (serveletPath.startsWith("/tasks/")) {
            // Pegar auth
            var authorization = request.getHeader("Authorization");
            var encodedAuth = authorization.substring("Basic".length()).trim();
            byte[] decodedAuth = Base64.getDecoder().decode(encodedAuth);

            var authString = new String(decodedAuth);
            var credentials = authString.split(":");

            // Validar
            var user = this.userRepository.findByUsername(credentials[0]);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                var passwordVerify = this.authenticationService.verifyPassword(user, credentials[1]);
                if (passwordVerify) {
                    request.setAttribute("userId", user.getId());
                    // Segue
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
