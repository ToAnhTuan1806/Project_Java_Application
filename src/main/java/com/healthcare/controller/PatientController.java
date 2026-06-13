package com.healthcare.controller;

import com.healthcare.dto.AppointmentDTO;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.User;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.SpecialtyRepository;
import com.healthcare.service.AppointmentService;
import com.healthcare.service.HistoryService;
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
@RequestMapping("/patient")
public class PatientController {
    private final AppointmentService appointmentService;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorRepository doctorRepository;
    private final HistoryService historyService;

    // Trang chính của bệnh nhân
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        model.addAttribute("currentUser", user);
        model.addAttribute("role", user.getRole().name());

        return "patient/dashboard";
    }

    // Hiển thị danh sách lịch khám
    @GetMapping("/appointments")
    public String appointments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        List<Appointment> appointments =
                appointmentService.getAppointmentsByPatient(user.getId());

        model.addAttribute("appointments", appointments);
        return "patient/appointment-list";
    }

    // Hiển thị form đặt lịch
    @GetMapping("/appointments/create")
    public String createForm(Model model) {
        model.addAttribute("appointmentDTO", new AppointmentDTO());
        model.addAttribute("specialties", specialtyRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());

        return "patient/appointment-form";
    }

    // Xử lý đặt lịch
    @PostMapping("/appointments/create")
    public String create(@Valid @ModelAttribute AppointmentDTO appointmentDTO,
                         BindingResult bindingResult, HttpSession session, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("specialties", specialtyRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            return "patient/appointment-form";
        }

        try {
            User user = (User) session.getAttribute("currentUser");
            appointmentService.bookAppointment(appointmentDTO, user.getId());

            return "redirect:/patient/appointments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("specialties", specialtyRepository.findAll());
            model.addAttribute("doctors", doctorRepository.findAll());
            return "patient/appointment-form";
        }
    }

    // Hủy lịch khám
    @GetMapping("/appointments/cancel/{id}")
    public String cancel(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        try {
            appointmentService.cancelAppointment(id, user.getId());
            return "redirect:/patient/appointments";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("appointments",
                    appointmentService.getAppointmentsByPatient(user.getId()));
            return "patient/appointment-list";
        }
    }

    // Xem lịch sử bệnh án
    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        model.addAttribute("records", historyService.getHistoryByPatient(user.getId()));

        return "patient/history";
    }
}