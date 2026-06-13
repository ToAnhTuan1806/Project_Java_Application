package com.healthcare.repository;

import com.healthcare.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // Lấy lịch sử bệnh án của bệnh nhân
    List<MedicalRecord> findByAppointmentPatientIdOrderByIdDesc(Long patientId);
}