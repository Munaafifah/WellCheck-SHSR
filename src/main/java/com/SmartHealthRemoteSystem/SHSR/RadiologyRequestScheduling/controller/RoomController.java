package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.controller;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.AppointmentStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RadiologyAppointment;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RadiologyRoom;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.RadiologyAppointmentRepository;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.RadiologyRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for radiology room/equipment lookups.
 * Supports UCR010 — Schedule Appointment: lets the Radiographer pick a
 * room that is actually free for the chosen modality, date, and time slot.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RadiologyRoomRepository       roomRepository;
    private final RadiologyAppointmentRepository appointmentRepository;

    @Autowired
    public RoomController(RadiologyRoomRepository roomRepository,
                          RadiologyAppointmentRepository appointmentRepository) {
        this.roomRepository        = roomRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * GET /api/rooms/available?modality=CT&date=2026-06-15&timeSlot=09:00-10:00
     *
     * Returns the room names for the given modality that are NOT already
     * booked (in any non-cancelled appointment) on that date + time slot.
     *
     * Uses a day-range query (start-of-day to end-of-day) rather than exact
     * Date equality, since appointmentDate values may carry a non-midnight
     * time-of-day component depending on how they were originally parsed —
     * exact equality silently misses matches in that case.
     */
    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableRooms(
            @RequestParam String modality,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam String timeSlot) {

        String normalizedModality = modality.toUpperCase();

        // All active rooms configured for this modality
        List<RadiologyRoom> allRooms =
                roomRepository.findByModalityAndIsActiveTrue(normalizedModality);

        Date startOfDay = startOfDay(date);
        Date endOfDay   = endOfDay(date);

        // Rooms already booked on that date for that time slot
        Set<String> bookedRooms = appointmentRepository
                .findAll()
                .stream()
                .filter(a -> a.getAppointmentDate() != null)
                .filter(a -> !a.getAppointmentDate().before(startOfDay)
                          && !a.getAppointmentDate().after(endOfDay))
                .filter(a -> timeSlot.equals(a.getTimeSlot()))
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .map(RadiologyAppointment::getEquipment)
                .collect(Collectors.toSet());

        List<String> availableRooms = allRooms.stream()
                .map(RadiologyRoom::getRoomName)
                .filter(roomName -> !bookedRooms.contains(roomName))
                .collect(Collectors.toList());

        return ResponseEntity.ok(availableRooms);
    }

    /**
     * GET /api/rooms?modality=CT
     * Returns all active rooms for a modality, regardless of availability.
     * Useful for admin screens or fallback display.
     */
    @GetMapping
    public ResponseEntity<List<RadiologyRoom>> getRoomsByModality(@RequestParam String modality) {
        return ResponseEntity.ok(
                roomRepository.findByModalityAndIsActiveTrue(modality.toUpperCase()));
    }

    // --- Helpers ---

    private Date startOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}