package com.example.racketmanager.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.racketmanager.model.User;
import com.example.racketmanager.repository.UserRepository;
import com.example.racketmanager.security.EncryptionUtil;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String inputUsername) throws UsernameNotFoundException {
        // 🔐 入力されたユーザー名をAES暗号化して検索
        String encryptedUsername = EncryptionUtil.encrypt(inputUsername);

        User user = userRepo.findByUsername(encryptedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + inputUsername));

        return org.springframework.security.core.userdetails.User
                .withUsername(inputUsername)  // 表示用は平文
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", "")) // ROLE_を除去して付与
                .build();
    }
}
