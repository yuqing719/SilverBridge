package org.example.safety.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 对 /api/** 的请求校验 Session 中是否有登录用户。
 * 白名单（注册/登录）不拦截。
 */
public class AuthInterceptor implements HandlerInterceptor {

    /** 不需要登录即可访问的路径前缀 */
    private static final String[] WHITELIST = {
            "/api/auth/register",
            "/api/auth/login"
    };

    @Override
    public boolean preHandle(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler) throws Exception {

        String path = request.getRequestURI();

        for (String w : WHITELIST) {
            if (path.startsWith(w)) return true;
        }

        if (!path.startsWith("/api/")) return true;   // 静态资源放行

        SessionUtil.LoginUser user = SessionUtil.get(request.getSession(false));
        if (user == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"请先登录\"}");
            return false;
        }
        return true;
    }
}
