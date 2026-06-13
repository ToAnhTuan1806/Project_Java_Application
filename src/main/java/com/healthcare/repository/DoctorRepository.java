package com.healthcare.repository;

import com.healthcare.entity.Doctor;
import com.healthcare.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Lấy bác sĩ theo chuyên khoa
    List<Doctor> findBySpecialty(Specialty specialty);

    // Tìm thông tin bác sĩ theo tài khoản user
    Optional<Doctor> findByUserId(Long userId);
}