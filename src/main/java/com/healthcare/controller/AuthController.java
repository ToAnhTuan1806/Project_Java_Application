package com.healthcare.controller;

import com.healthcare.dto.LoginDTO;
import com.healthcare.dto.RegisterDTO;
import com.healthcare.entity.User;
import com.healthcare.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO registerDTO,
                           BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(registerDTO);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "auth/login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            User user = authService.login(loginDTO);

            session.setAttribute("currentUser", user);
            session.setAttribute("role", user.getRole().name());

            if (user.getRole().name().equals("ADMIN")) {
                return "redirect:/admin/dashboard";
            }

            if (user.getRole().name().equals("DOCTOR")) {
                return "redirect:/doctor/dashboard";
            }

            return "redirect:/patient/dashboard";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    // Đăng xuất
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }

    // Điều hướng theo vai trò sau khi vào trang chủ
    @GetMapping("/")
    public String home(HttpSession session) {
        if (session.getAttribute("currentUser") == null) {
            return "redirect:/login";
        }

        String role = (String) session.getAttribute("role");

        if ("ADMIN".equals(role)) {
            return "redirect:/admin/dashboard";
        }

        if ("DOCTOR".equals(role)) {
            return "redirect:/doctor/dashboard";
        }

        return "redirect:/patient/dashboard";
    }
}