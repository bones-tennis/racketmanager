package com.example.racketmanager.model;

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

    @Column(nullable = false)
    private String role;

    // 🔐 暗号化してDB保存（email）
    @Column(nullable = false)
    private String email;

    // 🪪 表示用の名前（平文）
    @Column(nullable = false)
    private String displayName;

    // local or google
    @Column(nullable = false)
    private String provider;

    // ==========================
    // 🧱 コンストラクタ
    // ==========================
    public User() {}

    public User(String username, String password, String role, String email, String displayName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.displayName = displayName;
        this.provider = "local"; // デフォルト
    }

    // ==========================
    // 🧭 Getter / Setter
    // ==========================
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    // ==========================
    // 🪄 復号用 Getter（Thymeleaf表示などで利用）
    // ==========================
    @Transient
    public String getUsernameDecrypted() {
        try {
            return EncryptionUtil.decrypt(this.username);
        } catch (Exception e) {
            return "(復号エラー)";
        }
    }

    @Transient
    public String getEmailDecrypted() {
        try {
            return EncryptionUtil.decrypt(this.email);
        } catch (Exception e) {
            return "(復号エラー)";
        }
    }
}
