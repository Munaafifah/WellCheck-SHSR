package com.SmartHealthRemoteSystem.SHSR.DoctorSchedule;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Doctor Schedule")
public class DoctorSchedule {

    @Id
    private String doctorId; // same as _id in MongoDB (e.g. "fikrilamin")

    private String doctorName;

    private List<String> workingDays; // ["Monday", "Tuesday", ...]

    private WorkingHours workingHours;

    private int slotDurationMinutes;

    private List<BreakTime> breakTimes;

    private boolean isActive;

    // ── Constructors ──────────────────────────────────────────────────

    public DoctorSchedule() {}

    public DoctorSchedule(String doctorId, String doctorName,
                          List<String> workingDays, WorkingHours workingHours,
                          int slotDurationMinutes, List<BreakTime> breakTimes,
                          boolean isActive) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.workingDays = workingDays;
        this.workingHours = workingHours;
        this.slotDurationMinutes = slotDurationMinutes;
        this.breakTimes = breakTimes;
        this.isActive = isActive;
    }

    // ── Getters & Setters ─────────────────────────────────────────────

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public List<String> getWorkingDays() { return workingDays; }
    public void setWorkingDays(List<String> workingDays) { this.workingDays = workingDays; }

    public WorkingHours getWorkingHours() { return workingHours; }
    public void setWorkingHours(WorkingHours workingHours) { this.workingHours = workingHours; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    public List<BreakTime> getBreakTimes() { return breakTimes; }
    public void setBreakTimes(List<BreakTime> breakTimes) { this.breakTimes = breakTimes; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // ── Nested: WorkingHours ──────────────────────────────────────────

    public static class WorkingHours {
        private String start; // "09:00"
        private String end;   // "17:00"

        public WorkingHours() {}

        public WorkingHours(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() { return start; }
        public void setStart(String start) { this.start = start; }

        public String getEnd() { return end; }
        public void setEnd(String end) { this.end = end; }
    }

    // ── Nested: BreakTime ─────────────────────────────────────────────

    public static class BreakTime {
        private String start; // "13:00"
        private String end;   // "14:00"

        public BreakTime() {}

        public BreakTime(String start, String end) {
            this.start = start;
            this.end = end;
        }

        public String getStart() { return start; }
        public void setStart(String start) { this.start = start; }

        public String getEnd() { return end; }
        public void setEnd(String end) { this.end = end; }
    }
}