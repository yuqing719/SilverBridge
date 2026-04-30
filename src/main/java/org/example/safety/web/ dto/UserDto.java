package org.example.safety.web.dto;

import org.example.safety.domain.User;
import org.example.safety.web.SessionUtil;

/** 各 Controller 统一使用的用户响应 DTO */
public record UserDto(
        long id,
        String username,
        String name,
        String role,
        String phone       // 已脱敏
) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getRole().name(),
                maskPhone(user.getPhone())
        );
    }

    /** 从 Session LoginUser 构建（Session 中的 name 可能已过时） */
    public static UserDto fromSession(SessionUtil.LoginUser lu, String phone) {
        return new UserDto(
                lu.id(),
                lu.username(),
                lu.name(),
                lu.role().name(),
                phone
        );
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
