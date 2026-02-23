package com.example.racketmanager.security;

import com.example.racketmanager.model.User;
import com.example.racketmanager.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final int MAX_ATTEMPTS = 10;
    private final UserRepository userRepo;

    public LoginFailureHandler(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String username = request.getParameter("username");

        if (username != null && !username.isBlank()) {
            String encUsername = EncryptionUtil.encrypt(username);

            userRepo.findByUsername(encUsername).ifPresent(user -> {
                if (user.isAccountNonLocked()) {
                    int newFail = user.getFailedAttempts() + 1;
                    user.setFailedAttempts(newFail);

                    if (newFail >= MAX_ATTEMPTS) {
                        user.setAccountNonLocked(false);
                        user.setLockTime(LocalDateTime.now());
                    }

                    userRepo.save(user);
                }
            });
        }

        setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
