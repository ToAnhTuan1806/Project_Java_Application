package com.healthcare.controller;

import com.healthcare.dto.MedicineDTO;
import com.healthcare.entity.Medicine;
import com.healthcare.entity.User;
import com.healthcare.enums.PrescriptionStatus;
import com.healthcare.repository.PrescriptionRepository;
import com.healthcare.service.DispenseService;
import com.healthcare.service.MedicineService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final MedicineService medicineService;
    private final PrescriptionRepository prescriptionRepository;
    private final DispenseService dispenseService;

    // Trang chính của admin
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        model.addAttribute("currentUser", user);
        model.addAttribute("role", user.getRole().name());

        return "admin/dashboard";
    }

    @GetMapping("/medicines")
    public String medicineList(@RequestParam(required = false) String keyword,
                               Model model) {
        List<Medicine> medicines = medicineService.getAll(keyword);

        model.addAttribute("medicines", medicines);
        model.addAttribute("keyword", keyword);

        return "admin/medicine-list";
    }

    @GetMapping("/medicines/create")
    public String createForm(Model model) {
        model.addAttribute("medicineDTO", new MedicineDTO());
        return "admin/medicine-form";
    }

    @PostMapping("/medicines/create")
    public String create(@Valid @ModelAttribute MedicineDTO medicineDTO,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "admin/medicine-form";
        }

        medicineService.create(medicineDTO);
        return "redirect:/admin/medicines";
    }

    @GetMapping("/medicines/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Medicine medicine = medicineService.findById(id);

        model.addAttribute("medicineDTO", medicineService.toDTO(medicine));
        model.addAttribute("id", id);

        return "admin/medicine-form";
    }

    @PostMapping("/medicines/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute MedicineDTO medicineDTO,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            return "admin/medicine-form";
        }

        medicineService.update(id, medicineDTO);
        return "redirect:/admin/medicines";
    }

    @GetMapping("/medicines/delete/{id}")
    public String delete(@PathVariable Long id) {
        medicineService.delete(id);
        return "redirect:/admin/medicines";
    }
    // Hiển thị danh sách đơn thuốc chờ cấp phát
    @GetMapping("/prescriptions")
    public String prescriptions(Model model) {
        model.addAttribute("prescriptions",
                prescriptionRepository.findByStatus(PrescriptionStatus.WAITING_DISPENSE));

        return "admin/prescription-list";
    }

    // Xác nhận cấp phát thuốc
    @GetMapping("/prescriptions/dispense/{id}")
    public String dispense(@PathVariable Long id, Model model) {
        try {
            dispenseService.dispense(id);
            return "redirect:/admin/prescriptions";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("prescriptions",
                    prescriptionRepository.findByStatus(PrescriptionStatus.WAITING_DISPENSE));
            return "admin/prescription-list";
        }
    }

}