package com.healthcare.service;

import com.healthcare.dto.MedicalRecordDTO;
import com.healthcare.entity.*;
import com.healthcare.enums.AppointmentStatus;
import com.healthcare.enums.PrescriptionStatus;
import com.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;

    // Lưu bệnh án + đơn thuốc trong cùng một giao dịch
    @Transactional
    public void createMedicalRecord(Long appointmentId, MedicalRecordDTO dto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));

        if (appointment.getStatus() != AppointmentStatus.WAITING) {
            throw new RuntimeException("Chỉ được khám lịch đang chờ khám");
        }

        MedicalRecord record = MedicalRecord.builder()
                .appointment(appointment)
                .symptoms(dto.getSymptoms())
                .diagnosis(dto.getDiagnosis())
                .build();

        medicalRecordRepository.save(record);

        Prescription prescription = Prescription.builder()
                .medicalRecord(record)
                .status(PrescriptionStatus.WAITING_DISPENSE)
                .details(new java.util.ArrayList<>())
                .build();

        for (MedicalRecordDTO.PrescriptionItemDTO item : dto.getItems()) {

            // Bỏ qua dòng thuốc chưa chọn
            if (item.getMedicineId() == null) {
                continue;
            }

            Medicine medicine = medicineRepository.findById(item.getMedicineId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc"));

            PrescriptionDetail detail = PrescriptionDetail.builder()
                    .prescription(prescription)
                    .medicine(medicine)
                    .quantity(item.getQuantity())
                    .instruction(item.getInstruction())
                    .build();

            prescription.getDetails().add(detail);
        }

        prescriptionRepository.save(prescription);

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
    }
}