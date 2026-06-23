package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.AppointmentStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RadiologyAppointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for RadiologyAppointment documents.
 * Key responsibility: double-booking detection via findByAppointmentDateAndTimeSlotAndEquipment.
 */
@Repository
public interface RadiologyAppointmentRepository extends MongoRepository<RadiologyAppointment, String> {

    // UCR010 — Retrieve appointment linked to a specific request
    Optional<RadiologyAppointment> findByRequestId(String requestId);

    // UCR010/UCR015 — Double-booking guard: check slot and equipment on a given date
    List<RadiologyAppointment> findByAppointmentDateAndTimeSlotAndEquipment(
            Date appointmentDate, String timeSlot, String equipment);

    // UCR010 — All appointments on a given date (schedule view)
    List<RadiologyAppointment> findByAppointmentDate(Date appointmentDate);

    // UCR015 — Radiographer views own schedule
    List<RadiologyAppointment> findByRadiographerId(String radiographerId);

    // UCR015 — Filter by status
    List<RadiologyAppointment> findByStatus(AppointmentStatus status);

    // Count helpers
    long countByStatus(AppointmentStatus status);
}
