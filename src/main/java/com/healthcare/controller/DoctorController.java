package com.healthcare.controller;

import com.healthcare.dto.MedicalRecordDTO;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.User;
import com.healthcare.enums.AppointmentStatus;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.MedicineRepository;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.MedicalRecordService;
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
@RequestMapping("/doctor")
public class DoctorController {
    private final DoctorRepository doctorRepository;
    private final MedicineRepository medicineRepository;
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    // Trang chính của bác sĩ
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        model.addAttribute("currentUser", user);
        model.addAttribute("role", user.getRole().name());

        return "doctor/dashboard";
    }

    // Xem danh sách bệnh nhân chờ khám
    @GetMapping("/appointments")
    public String waitingAppointments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        Doctor doctor = doctorRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        List<Appointment> appointments =
                appointmentService.getWaitingAppointmentsByDoctor(doctor.getId());

        model.addAttribute("appointments", appointments);
        return "doctor/appointment-list";
    }

    // Hiển thị form khám bệnh
    @GetMapping("/appointments/exam/{id}")
    public String examForm(@PathVariable Long id, Model model) {
        MedicalRecordDTO dto = new MedicalRecordDTO();

        for (int i = 0; i < 3; i++) {
            dto.getItems().add(new MedicalRecordDTO.PrescriptionItemDTO());
        }

        model.addAttribute("medicalRecordDTO", dto);
        model.addAttribute("appointmentId", id);
        model.addAttribute("medicines", medicineRepository.findByDeletedFalse());

        return "doctor/exam-form";
    }

    // Xử lý lưu kết quả khám
    @PostMapping("/appointments/exam/{id}")
    public String exam(@PathVariable Long id,
                       @Valid @ModelAttribute MedicalRecordDTO medicalRecordDTO,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("appointmentId", id);
            model.addAttribute("medicines", medicineRepository.findByDeletedFalse());
            return "doctor/exam-form";
        }

        try {
            medicalRecordService.createMedicalRecord(id, medicalRecordDTO);
            return "redirect:/doctor/appointments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("appointmentId", id);
            model.addAttribute("medicines", medicineRepository.findByDeletedFalse());
            return "doctor/exam-form";
        }
    }
}