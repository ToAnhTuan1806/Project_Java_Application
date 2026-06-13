package com.healthcare.service;

import com.healthcare.dto.AppointmentDTO;
import com.healthcare.entity.Appointment;
import com.healthcare.entity.Doctor;
import com.healthcare.entity.User;
import com.healthcare.enums.AppointmentStatus;
import com.healthcare.repository.AppointmentRepository;
import com.healthcare.repository.DoctorRepository;
import com.healthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    // Lấy danh sách lịch của bệnh nhân
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository
                .findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId);
    }

    // Bệnh nhân đặt lịch khám
    public void bookAppointment(AppointmentDTO dto, Long patientId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Chặn đặt ngày giờ trong quá khứ
        if (dto.getAppointmentDate().isBefore(today)
                || (dto.getAppointmentDate().isEqual(today)
                && dto.getAppointmentTime().isBefore(now))) {
            throw new RuntimeException("Không được đặt lịch trong quá khứ");
        }

        // Chặn trùng lịch bác sĩ
        boolean exists = appointmentRepository
                .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        dto.getDoctorId(),
                        dto.getAppointmentDate(),
                        dto.getAppointmentTime(),
                        AppointmentStatus.CANCELLED
                );

        if (exists) {
            throw new RuntimeException("Bác sĩ đã có lịch khám vào khung giờ này");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân"));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ"));

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(dto.getAppointmentDate())
                .appointmentTime(dto.getAppointmentTime())
                .status(AppointmentStatus.WAITING)
                .build();

        appointmentRepository.save(appointment);
    }

    // Hủy lịch khám của bệnh nhân
    public void cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám"));

        // Chỉ bệnh nhân sở hữu lịch mới được hủy
        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Bạn không có quyền hủy lịch này");
        }

        // Chỉ lịch đang chờ khám mới được hủy
        if (appointment.getStatus() != AppointmentStatus.WAITING) {
            throw new RuntimeException("Chỉ được hủy lịch đang chờ khám");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime()
        );

        // Phải hủy trước giờ khám ít nhất 24 giờ
        if (appointmentDateTime.minusHours(24).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Chỉ được hủy lịch trước thời điểm khám ít nhất 24 giờ");
        }

        // Cập nhật trạng thái đã hủy, slot tự được giải phóng
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    // Lấy danh sách lịch chờ khám của bác sĩ
    public List<Appointment> getWaitingAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository
                .findByDoctorIdAndStatusOrderByAppointmentDateAscAppointmentTimeAsc(
                        doctorId,
                        AppointmentStatus.WAITING
                );
    }
}