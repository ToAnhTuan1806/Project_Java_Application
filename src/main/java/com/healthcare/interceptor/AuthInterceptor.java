package com.healthcare.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("/login");
            return false;
        }

        String role = (String) session.getAttribute("role");

        System.out.println("Interceptor chạy: " + uri + " - role: " + role);

        if (uri.startsWith("/admin") && !"ADMIN".equals(role)) {
            response.sendRedirect("/access-denied");
            return false;
        }

        if (uri.startsWith("/doctor") && !"DOCTOR".equals(role)) {
            response.sendRedirect("/access-denied");
            return false;
        }

        if (uri.startsWith("/patient") && !"PATIENT".equals(role)) {
            response.sendRedirect("/access-denied");
            return false;
        }

        return true;
    }
}