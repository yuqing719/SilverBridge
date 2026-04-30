package org.example.safety.service;

import org.example.safety.domain.User;
import org.example.safety.domain.UserRole;
import org.example.safety.repo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ────────── 注册 ──────────

    @Transactional
    public User register(String username, String rawPassword, String name, UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在: " + username);
        }
        String hash = encoder.encode(rawPassword);
        User user = new User(username, name, hash, role);
        return userRepository.save(user);
    }

    // ────────── 登录 ──────────

    @Transactional(readOnly = true)
    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("用户名或密码错误"));
        if (!encoder.matches(rawPassword, user.getPasswordHash())) {
            throw new UnauthorizedException("用户名或密码错误");
        }
        return user;
    }

    // ────────── 查询个人资料 ──────────

    @Transactional(readOnly = true)
    public User getProfile(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("用户不存在: " + userId));
    }

    // ────────── 修改资料 ──────────

    @Transactional
    public User updateProfile(long userId, String newName, String newPhone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("用户不存在: " + userId));
        if (newName != null && !newName.isBlank()) {
            user.setName(newName);
        }
        if (newPhone != null) {
            user.setPhone(newPhone.isBlank() ? null : newPhone);
        }
        return user; // JPA dirty-check 自动持久化
    }

    // ────────── 修改密码 ──────────

    @Transactional
    public void changePassword(long userId, String oldRaw, String newRaw) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("用户不存在: " + userId));
        if (!encoder.matches(oldRaw, user.getPasswordHash())) {
            throw new UnauthorizedException("原密码错误");
        }
        if (newRaw == null || newRaw.length() < 6) {
            throw new IllegalArgumentException("新密码不能少于 6 位");
        }
        user.setPasswordHash(encoder.encode(newRaw));
    }
}
