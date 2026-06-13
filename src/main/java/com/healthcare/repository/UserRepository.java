package com.healthcare.repository;

import com.healthcare.entity.User;
import com.healthcare.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Tìm user theo email để đăng nhập
    Optional<User> findByEmail(String email);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Lấy danh sách user theo vai trò
    List<User> findByRole(Role role);
}