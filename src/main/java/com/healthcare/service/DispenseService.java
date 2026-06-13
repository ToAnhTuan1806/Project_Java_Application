package com.healthcare.service;

import com.healthcare.entity.Medicine;
import com.healthcare.entity.Prescription;
import com.healthcare.entity.PrescriptionDetail;
import com.healthcare.enums.PrescriptionStatus;
import com.healthcare.repository.MedicineRepository;
import com.healthcare.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DispenseService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;

    // Cấp phát thuốc và trừ tồn kho trong cùng transaction
    @Transactional
    public void dispense(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc"));

        if (prescription.getStatus() == PrescriptionStatus.DISPENSED) {
            throw new RuntimeException("Đơn thuốc đã được cấp phát");
        }

        // Kiểm tra tồn kho trước
        for (PrescriptionDetail detail : prescription.getDetails()) {
            Medicine medicine = detail.getMedicine();

            if (medicine.getStockQuantity() < detail.getQuantity()) {
                throw new RuntimeException("Thuốc " + medicine.getName() + " không đủ tồn kho");
            }
        }

        // Đủ tồn kho thì mới trừ
        for (PrescriptionDetail detail : prescription.getDetails()) {
            Medicine medicine = detail.getMedicine();
            medicine.setStockQuantity(medicine.getStockQuantity() - detail.getQuantity());
            medicineRepository.save(medicine);
        }

        prescription.setStatus(PrescriptionStatus.DISPENSED);
        prescriptionRepository.save(prescription);
    }
}