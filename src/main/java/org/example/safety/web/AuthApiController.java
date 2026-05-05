package org.example.safety.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.safety.domain.User;
import org.example.safety.domain.UserRole;
import org.example.safety.service.UserService;
import org.example.safety.web.dto.UserDto;
import org.springframework.web.bind.annotation.*;

/**
 * 注册 / 登录 / 登出 / 当前用户
 * 白名单（无需 Session）：/api/auth/register、/api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final UserService userService;

    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody RegisterRequest req, HttpSession session) {
        UserRole role = parseRole(req.role());
        User user = userService.register(req.username(), req.password(), req.name(), role);
        SessionUtil.put(session, user);
        return UserDto.from(user);
    }

    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginRequest req, HttpSession session) {
        User user = userService.login(req.username(), req.password());
        SessionUtil.put(session, user);
        return UserDto.from(user);
    }

    /** 从 Session 直接返回，不查库 */
    @GetMapping("/me")
    public UserDto me(HttpSession session) {
        SessionUtil.LoginUser lu = SessionUtil.get(session);
        if (lu == null) {
            throw new org.example.safety.service.UnauthorizedException("未登录");
        }
        return UserDto.fromSession(lu, null);
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        SessionUtil.remove(session);
    }

    private UserRole parseRole(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("无效角色，应为 ELDER、CHILD 或 VOLUNTEER: " + role);
        }
    }

    public record RegisterRequest(
            @NotBlank(message = "用户名不能为空")
            @Size(min = 4, max = 20, message = "用户名 4-20 位")
            @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字、下划线")
            String username,

            @NotBlank(message = "密码不能为空")
            @Size(min = 6, max = 32, message = "密码 6-32 位")
            String password,

            @NotBlank(message = "昵称不能为空")
            @Size(max = 32, message = "昵称最多 32 位")
            String name,

            @NotBlank(message = "角色不能为空")
            String role
    ) {}

    public record LoginRequest(
            @NotBlank(message = "用户名不能为空")
            String username,

            @NotBlank(message = "密码不能为空")
            String password
    ) {}
}
