package com.healthcare.service;

import com.healthcare.dto.LoginDTO;
import com.healthcare.dto.RegisterDTO;
import com.healthcare.entity.User;
import com.healthcare.enums.Role;
import com.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    // Dky Tk benh nhan
    public void register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(hashPassword(dto.getPassword()))
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .role(Role.PATIENT)
                .build();

        userRepository.save(user);
    }

    // Dnhap
    public User login(LoginDTO dto) {
        Optional<User> optionalUser = userRepository.findByEmail(dto.getEmail());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(hashPassword(dto.getPassword()))) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        return user;
    }

    // Hash đơn giản để đúng yêu cầu không lưu mật khẩu thô
    private String hashPassword(String password) {
        return String.valueOf(password.hashCode());
    }
}