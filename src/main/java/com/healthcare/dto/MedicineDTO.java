package com.healthcare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicineDTO {
    @NotBlank(message = "Vui lòng nhập tên thuốc")
    private String name;

    @NotBlank(message = "Vui lòng nhập nhà sản xuất")
    private String manufacturer;

    @NotNull(message = "Vui lòng nhập giá thuốc")
    @Min(value = 1000, message = "Giá thuốc phải từ 1000 trở lên")
    private Double price;

    @NotNull(message = "Vui lòng nhập số lượng tồn kho")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stockQuantity;
}