package com.healthcare.repository;

import com.healthcare.entity.Prescription;
import com.healthcare.enums.PrescriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // Lấy đơn thuốc theo trạng thái
    List<Prescription> findByStatus(PrescriptionStatus status);
    // Tìm đơn thuốc theo bệnh án
    Prescription findByMedicalRecordId(Long medicalRecordId);
}