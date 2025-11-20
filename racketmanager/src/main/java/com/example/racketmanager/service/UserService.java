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
    public void registerUser(String username, String password, String role, String email) {
        // 🔐 AES暗号化
        String encryptedUsername = EncryptionUtil.encrypt(username);
        String encryptedEmail = EncryptionUtil.encrypt(email);

        // 🔑 パスワードはハッシュ化
        String encodedPassword = passwordEncoder.encode(password);

        // 🪪 表示用
        String displayName = username;

        User user = new User(
                encryptedUsername,
                encodedPassword,
                "ROLE_" + role,
                encryptedEmail,
                displayName
        );

        user.setProvider("local");
        userRepo.save(user);

        System.out.println("✅ 新規ユーザー登録: " + displayName + "（" + role + "）");
    }
}
