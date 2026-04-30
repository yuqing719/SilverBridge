package org.example.safety.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false, length = 64)
    private String name;

    /** BCrypt 哈希后的密码 */
    @Column(nullable = false, length = 128)
    private String passwordHash;

    /** 手机号（可选） */
    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private UserRole role;

    protected User() {
    }

    /** 旧构造器：兼容 DemoDataInitializer（用 name 作 username） */
    public User(String name, UserRole role) {
        this.username = name;
        this.name = name;
        this.passwordHash = "";
        this.role = role;
    }

    public User(String username, String name, String passwordHash, UserRole role) {
        this.username = username;
        this.name = name;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
