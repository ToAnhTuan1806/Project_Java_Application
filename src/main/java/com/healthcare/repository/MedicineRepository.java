package com.healthcare.repository;

import com.healthcare.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // Lấy thuốc chưa bị xóa mềm
    List<Medicine> findByDeletedFalse();

    // Tìm thuốc theo tên và chưa bị xóa
    List<Medicine> findByNameContainingIgnoreCaseAndDeletedFalse(String name);
}