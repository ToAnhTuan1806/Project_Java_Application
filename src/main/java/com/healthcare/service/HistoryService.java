package com.healthcare.service;

import com.healthcare.entity.MedicalRecord;
import com.healthcare.entity.Prescription;
import com.healthcare.repository.MedicalRecordRepository;
import com.healthcare.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;

    // Lấy lịch sử bệnh án của bệnh nhân
    public List<MedicalRecord> getHistoryByPatient(Long patientId) {
        return medicalRecordRepository.findByAppointmentPatientIdOrderByIdDesc(patientId);
    }

    // Lấy đơn thuốc theo bệnh án
    public Prescription getPrescriptionByRecord(Long recordId) {
        return prescriptionRepository.findByMedicalRecordId(recordId);
    }
}