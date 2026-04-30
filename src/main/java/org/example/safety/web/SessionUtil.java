package org.example.safety.web;

import jakarta.servlet.http.HttpSession;
import org.example.safety.domain.User;
import org.example.safety.domain.UserRole;

/**
 * Session 中登录用户 VO。
 */
public final class SessionUtil {

    private SessionUtil() {}

    private static final String KEY = "currentUser";

    public static void put(HttpSession session, User user) {
        session.setAttribute(KEY, new LoginUser(user.getId(), user.getUsername(),
                user.getName(), user.getRole()));
    }

    public static LoginUser get(HttpSession session) {
        if (session == null) return null;
        return (LoginUser) session.getAttribute(KEY);
    }

    /** 替换 Session 中缓存的 name（修改资料后同步） */
    public static void refreshName(HttpSession session, String name) {
        LoginUser old = get(session);
        if (old != null) {
            session.setAttribute(KEY, new LoginUser(old.id(), old.username(),
                    name, old.role()));
        }
    }

    public static void remove(HttpSession session) {
        if (session != null) {
            session.removeAttribute(KEY);
        }
    }

    /** 使 Session 失效（改密码后强制重新登录） */
    public static void invalidate(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public record LoginUser(long id, String username, String name, UserRole role) {}
}
