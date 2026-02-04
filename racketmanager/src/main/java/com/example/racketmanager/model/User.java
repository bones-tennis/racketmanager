package com.example.racketmanager.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.example.racketmanager.security.EncryptionUtil;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔐 暗号化してDB保存（username）
    @Column(nullable = false, unique = true)
    private String username;

    // 🔐 ハッシュ化してDB保存（password）
    @Column(nullable = false)
    private String password;

    // ROLE_CUSTOMER / ROLE_STAFF
    @Column(nullable = false)
    private String role;

    // 🪪 表示用の名前（平文）
    @Column(nullable = false)
    private String displayName;

    // local / google / etc
    @Column(nullable = false)
    private String provider;

    // ==========================
    // 📱 LINE連携用
    // ==========================

    // LINEのユーザーID（Push通知に使用）
    @Column(name = "line_user_id")
    private String lineUserId;

    // LINE連携した日時
    @Column(name = "line_linked_at")
    private LocalDateTime lineLinkedAt;

    // ==========================
    // 🧱 コンストラクタ
    // ==========================
    public User() {}

    public User(String username, String password, String role, String displayName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
        this.provider = "local";
    }

    // ==========================
    // 🧭 Getter / Setter
    // ==========================
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }

    // ===== LINE =====
    public String getLineUserId() {
        return lineUserId;
    }

    /**
     * LINEユーザーIDをセットする際に、連携日時も自動で入れる
     */
    public void setLineUserId(String lineUserId) {
        this.lineUserId = lineUserId;
        if (lineUserId != null && !lineUserId.isBlank()) {
            this.lineLinkedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getLineLinkedAt() {
        return lineLinkedAt;
    }

    // 明示的にセットしたい場合用（基本は使わなくてOK）
    public void setLineLinkedAt(LocalDateTime lineLinkedAt) {
        this.lineLinkedAt = lineLinkedAt;
    }

    // ==========================
    // 🪄 復号用 Getter（表示専用）
    // ==========================
    @Transient
    public String getUsernameDecrypted() {
        try {
            return EncryptionUtil.decrypt(this.username);
        } catch (Exception e) {
            return "(復号エラー)";
        }
    }
}
