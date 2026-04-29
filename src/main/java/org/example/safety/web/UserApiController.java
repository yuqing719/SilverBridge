package org.example.safety.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.example.safety.domain.User;
import org.example.safety.service.UserService;
import org.example.safety.web.dto.UserDto;
import org.springframework.web.bind.annotation.*;

/**
 * 用户个人资料管理（需登录）
 * GET  /api/users/me              查资料
 * PUT  /api/users/me              改昵称 / 手机号
 * POST /api/users/me/password     改密码（改后强制重新登录）
 */
@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDto getProfile(HttpSession session) {
        SessionUtil.LoginUser lu = requireLogin(session);
        User user = userService.getProfile(lu.id());
        return UserDto.from(user);
    }

    @PutMapping("/me")
    public UserDto updateProfile(@Valid @RequestBody UpdateProfileRequest req,
                                 HttpSession session) {
        SessionUtil.LoginUser lu = requireLogin(session);
        User user = userService.updateProfile(lu.id(), req.name(), req.phone());
        // 同步 Session 中的 name
        SessionUtil.refreshName(session, user.getName());
        return UserDto.from(user);
    }

    @PostMapping("/me/password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest req,
                               HttpSession session) {
        SessionUtil.LoginUser lu = requireLogin(session);
        userService.changePassword(lu.id(), req.oldPassword(), req.newPassword());
        // 改密码后强制重新登录，防止旧 session 被滥用
        SessionUtil.invalidate(session);
    }

    private SessionUtil.LoginUser requireLogin(HttpSession session) {
        SessionUtil.LoginUser lu = SessionUtil.get(session);
        if (lu == null) {
            throw new org.example.safety.service.UnauthorizedException("请先登录");
        }
        return lu;
    }

    public record UpdateProfileRequest(
            @Size(max = 32, message = "昵称最多 32 位")
            String name,

            @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
            String phone
    ) {}

    public record ChangePasswordRequest(
            @Size(min = 1, message = "原密码不能为空")
            String oldPassword,

            @Size(min = 6, max = 32, message = "新密码 6-32 位")
            String newPassword
    ) {}
}
