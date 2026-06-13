package com.healthcare.repository;

import com.healthcare.entity.Appointment;
import com.healthcare.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Kiểm tra bác sĩ đã có lịch ở ngày giờ đó chưa
    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus status
    );

    // Lấy lịch của bệnh nhân
    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);

    // Lấy lịch chờ khám của bác sĩ
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAscAppointmentTimeAsc(
            Long doctorId,
            AppointmentStatus status
    );
}