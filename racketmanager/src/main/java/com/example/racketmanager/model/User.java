package com.example.racketmanager.model;

import com.example.racketmanager.security.EncryptionUtil;
import jakarta.persistence.*;

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

    @Column(nullable = false)
    private String role;

    // 🪪 表示用の名前（平文）
    @Column(nullable = false)
    private String displayName;

    // local or google
    @Column(nullable = false)
    private String provider;

    // ✅ LINEのユーザーID（紐付け用）
    @Column(name = "line_user_id", unique = true)
    private String lineUserId;

    public User() {}

    public User(String username, String password, String role, String displayName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
        this.provider = "local";
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getLineUserId() { return lineUserId; }
    public void setLineUserId(String lineUserId) { this.lineUserId = lineUserId; }

    @Transient
    public String getUsernameDecrypted() {
        try {
            return EncryptionUtil.decrypt(this.username);
        } catch (Exception e) {
            return "(復号エラー)";
        }
    }
}
