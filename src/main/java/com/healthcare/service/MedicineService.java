package com.healthcare.service;

import com.healthcare.dto.MedicineDTO;
import com.healthcare.entity.Medicine;
import com.healthcare.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {

    // Tiêm repository thao tác database
    private final MedicineRepository medicineRepository;

    // Lấy danh sách thuốc
    public List<Medicine> getAll(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return medicineRepository.findByNameContainingIgnoreCaseAndDeletedFalse(keyword);
        }
        return medicineRepository.findByDeletedFalse();
    }

    // Tìm thuốc theo id
    public Medicine findById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc"));
    }

    // Thêm thuốc mới
    public void create(MedicineDTO dto) {
        Medicine medicine = Medicine.builder()
                .name(dto.getName())
                .manufacturer(dto.getManufacturer())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .deleted(false)
                .build();

        medicineRepository.save(medicine);
    }

    // Cập nhật thông tin thuốc
    public void update(Long id, MedicineDTO dto) {
        Medicine medicine = findById(id);

        medicine.setName(dto.getName());
        medicine.setManufacturer(dto.getManufacturer());
        medicine.setPrice(dto.getPrice());
        medicine.setStockQuantity(dto.getStockQuantity());

        medicineRepository.save(medicine);
    }

    // Xóa mềm thuốc
    public void delete(Long id) {
        Medicine medicine = findById(id);
        medicine.setDeleted(true);
        medicineRepository.save(medicine);
    }

    // Chuyển Entity sang DTO
    public MedicineDTO toDTO(Medicine medicine) {
        MedicineDTO dto = new MedicineDTO();

        dto.setName(medicine.getName());
        dto.setManufacturer(medicine.getManufacturer());
        dto.setPrice(medicine.getPrice());
        dto.setStockQuantity(medicine.getStockQuantity());

        return dto;
    }
}