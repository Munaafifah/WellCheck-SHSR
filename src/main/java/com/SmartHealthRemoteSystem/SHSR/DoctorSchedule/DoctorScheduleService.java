package com.SmartHealthRemoteSystem.SHSR.DoctorSchedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorScheduleService {

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    // ── Get all schedules ─────────────────────────────────────────────
    public List<DoctorSchedule> getAllSchedules() {
        return doctorScheduleRepository.findAll();
    }

    // ── Get schedule by doctorId ──────────────────────────────────────
    public DoctorSchedule getScheduleByDoctorId(String doctorId) {
        Optional<DoctorSchedule> opt = doctorScheduleRepository.findById(doctorId);
        return opt.orElse(null);
    }

    // ── Save or update schedule ───────────────────────────────────────
    // If doctorId already exists → updates it
    // If new doctorId → creates it
    public DoctorSchedule saveSchedule(DoctorSchedule schedule) {
        return doctorScheduleRepository.save(schedule);
    }

    // ── Delete schedule by doctorId ───────────────────────────────────
    public void deleteSchedule(String doctorId) {
        doctorScheduleRepository.deleteById(doctorId);
    }

    // ── Toggle active status ──────────────────────────────────────────
    public String toggleActive(String doctorId) {
        DoctorSchedule schedule = getScheduleByDoctorId(doctorId);
        if (schedule == null) return "Schedule not found";
        schedule.setActive(!schedule.isActive());
        doctorScheduleRepository.save(schedule);
        return "Schedule " + (schedule.isActive() ? "activated" : "deactivated") + " successfully";
    }
}