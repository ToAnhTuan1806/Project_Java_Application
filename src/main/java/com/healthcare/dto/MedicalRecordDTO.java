package com.healthcare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MedicalRecordDTO {
    @NotBlank(message = "Vui lòng nhập triệu chứng")
    private String symptoms;

    @NotBlank(message = "Vui lòng nhập chẩn đoán")
    private String diagnosis;

    private List<PrescriptionItemDTO> items = new ArrayList<>();

    @Data
    public static class PrescriptionItemDTO {

        @NotNull(message = "Vui lòng chọn thuốc")
        private Long medicineId;

        @NotNull(message = "Vui lòng nhập số lượng")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity;

        @NotBlank(message = "Vui lòng nhập hướng dẫn sử dụng")
        private String instruction;
    }
}