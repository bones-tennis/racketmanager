package com.example.racketmanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.racketmanager.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 🔹 ログイン用（usernameで1件取得）
    Optional<User> findByUsername(String username);

    // 🔹 ROLEで絞り込み（顧客・スタッフのリスト表示用）
    List<User> findByRole(String role);
}
