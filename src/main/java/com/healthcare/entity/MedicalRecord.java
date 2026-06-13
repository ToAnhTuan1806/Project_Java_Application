package com.healthcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Một lịch khám có một bệnh án
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    // Triệu chứng bệnh nhân
    @Column(nullable = false, columnDefinition = "TEXT")
    private String symptoms;

    // Kết quả chẩn đoán
    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnosis;
    
    // Một bệnh án có một đơn thuốc
    @OneToOne(mappedBy = "medicalRecord", fetch = FetchType.LAZY)
    private Prescription prescription;
}