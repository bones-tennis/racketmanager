package com.example.racketmanager.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.racketmanager.model.User;
import com.example.racketmanager.repository.UserRepository;
import com.example.racketmanager.security.EncryptionUtil;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * ユーザー登録処理
     */
    public void registerUser(String username, String password, String role, String lineUserId) {

        String encryptedUsername = EncryptionUtil.encrypt(username);
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User();
        user.setUsername(encryptedUsername);
        user.setPassword(encodedPassword);
        user.setRole("ROLE_" + role);
        user.setDisplayName(username);
        user.setProvider("local");
        user.setLineUserId(lineUserId);

        userRepo.save(user);
    }

}