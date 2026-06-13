package com.healthcare.config;

import com.healthcare.entity.*;
import com.healthcare.enums.Role;
import com.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final MedicineRepository medicineRepository;

    @Override
    public void run(String... args) {

        // Nếu đã có dữ liệu thì không seed lại
        if (userRepository.count() > 0) {
            return;
        }

        // Tạo chuyên khoa mẫu
        Specialty general = specialtyRepository.save(
                Specialty.builder().name("Nội tổng quát").build()
        );

        Specialty children = specialtyRepository.save(
                Specialty.builder().name("Nhi khoa").build()
        );

        // Tạo admin mẫu
        User admin = userRepository.save(
                User.builder()
                        .fullName("Quản trị viên")
                        .email("admin@gmail.com")
                        .password(hashPassword("123456"))
                        .phone("0900000000")
                        .address("Hà Nội")
                        .role(Role.ADMIN)
                        .build()
        );

        // Tạo tài khoản bác sĩ mẫu
        User doctorUser = userRepository.save(
                User.builder()
                        .fullName("Bác sĩ Nguyễn Văn A")
                        .email("doctor@gmail.com")
                        .password(hashPassword("123456"))
                        .phone("0911111111")
                        .address("Hà Nội")
                        .role(Role.DOCTOR)
                        .build()
        );

        doctorRepository.save(
                Doctor.builder()
                        .user(doctorUser)
                        .specialty(general)
                        .experience("5 năm kinh nghiệm")
                        .build()
        );

        // Tạo bệnh nhân mẫu
        userRepository.save(
                User.builder()
                        .fullName("Bệnh nhân Trần Văn B")
                        .email("patient@gmail.com")
                        .password(hashPassword("123456"))
                        .phone("0922222222")
                        .address("Hà Nội")
                        .role(Role.PATIENT)
                        .build()
        );

        // Tạo thuốc mẫu
        medicineRepository.save(
                Medicine.builder()
                        .name("Paracetamol")
                        .manufacturer("DHG Pharma")
                        .price(15000.0)
                        .stockQuantity(100)
                        .deleted(false)
                        .build()
        );

        medicineRepository.save(
                Medicine.builder()
                        .name("Vitamin C")
                        .manufacturer("Traphaco")
                        .price(20000.0)
                        .stockQuantity(80)
                        .deleted(false)
                        .build()
        );
    }
    private String hashPassword(String password) {
        return String.valueOf(password.hashCode());
    }
}