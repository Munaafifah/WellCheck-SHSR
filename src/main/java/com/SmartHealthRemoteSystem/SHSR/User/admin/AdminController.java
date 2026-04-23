package com.SmartHealthRemoteSystem.SHSR.User.admin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SmartHealthRemoteSystem.SHSR.DoctorSchedule.DoctorSchedule;
import com.SmartHealthRemoteSystem.SHSR.DoctorSchedule.DoctorScheduleService;
import com.SmartHealthRemoteSystem.SHSR.Pagination.PaginationInfo;
import com.SmartHealthRemoteSystem.SHSR.Service.*;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.Pharmacist.Pharmacist;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import com.SmartHealthRemoteSystem.SHSR.User.ClinicAssistant.ClinicAssistant;
import com.SmartHealthRemoteSystem.SHSR.User.Radiographer.Radiographer;
import com.SmartHealthRemoteSystem.SHSR.User.Radiologist.Radiologist;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // ──────────────────────────── services ────────────────────────────
    private final UserService userService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final PharmacistService pharmacistService;
    private final MailService mailService;
    private final ClinicAssistantService clinicAssistantService;
    private final RadiographerService radiographerService;
    private final RadiologistService radiologistService;

    @Autowired
    private DoctorScheduleService doctorScheduleService;

    @Autowired
    public AdminController(UserService userService,
            PatientService patientService,
            DoctorService doctorService,
            PharmacistService pharmacistService,
            ClinicAssistantService clinicAssistantService,
            RadiographerService radiographerService,
            RadiologistService radiologistService,
            MailService mailService) {

        this.userService = userService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.pharmacistService = pharmacistService;
        this.clinicAssistantService = clinicAssistantService;
        this.radiographerService = radiographerService;
        this.radiologistService = radiologistService;
        this.mailService = mailService;
    }

    // ──────────────────────────── dashboard ───────────────────────────
    @GetMapping
    public String getAdminDashboard(Model model,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "patient") String tab,
            @RequestParam(defaultValue = "") String searchQuery)
            throws ExecutionException, InterruptedException {

        List<Patient> allPatients = patientService.getAllPatients();
        List<Doctor> doctorList = doctorService.getAllDoctors();
        List<Pharmacist> pharmacistList = pharmacistService.getListPharmacist();
        List<ClinicAssistant> clinicAssistantList = clinicAssistantService.getListClinicAssistant();
        List<Radiographer> radiographerList = radiographerService.getListRadiographer();
        List<Radiologist> radiologistList = radiologistService.getListRadiologist();
        List<User> adminList = userService.getAdminList();

        // Filter based on searchQuery
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String q = searchQuery.toLowerCase();
            switch (tab) {
                case "patient":
                    allPatients = allPatients.stream()
                            .filter(p -> contains(p.getName(), q) || contains(p.getUserId(), q)
                                    || contains(p.getContact(), q) || contains(p.getEmail(), q))
                            .collect(Collectors.toList());
                    break;
                case "doctor":
                    doctorList = doctorList.stream()
                            .filter(d -> contains(d.getName(), q) || contains(d.getUserId(), q)
                                    || contains(d.getContact(), q) || contains(d.getHospital(), q))
                            .collect(Collectors.toList());
                    break;
                case "pharmacist":
                    pharmacistList = pharmacistList.stream()
                            .filter(p -> contains(p.getName(), q) || contains(p.getUserId(), q)
                                    || contains(p.getContact(), q))
                            .collect(Collectors.toList());
                    break;
                case "clinic_assistant":
                    clinicAssistantList = clinicAssistantList.stream()
                            .filter(c -> contains(c.getName(), q) || contains(c.getUserId(), q)
                                    || contains(c.getContact(), q))
                            .collect(Collectors.toList());
                    break;
                case "radiographer":
                    radiographerList = radiographerList.stream()
                            .filter(r -> contains(r.getName(), q) || contains(r.getUserId(), q)
                                    || contains(r.getContact(), q))
                            .collect(Collectors.toList());
                    break;
                case "radiologist":
                    radiologistList = radiologistList.stream()
                            .filter(r -> contains(r.getName(), q) || contains(r.getUserId(), q)
                                    || contains(r.getContact(), q))
                            .collect(Collectors.toList());
                    break;
                case "admin":
                    adminList = adminList.stream()
                            .filter(a -> contains(a.getName(), q) || contains(a.getUserId(), q)
                                    || contains(a.getContact(), q))
                            .collect(Collectors.toList());
                    break;
            }
        }

        // assigned / unassigned split
        List<Patient> assignedPatients = allPatients.stream()
                .filter(p -> p.getAssigned_doctor() != null && !p.getAssigned_doctor().isEmpty())
                .collect(Collectors.toList());
        List<Patient> unassignedPatients = allPatients.stream()
                .filter(p -> p.getAssigned_doctor() == null || p.getAssigned_doctor().isEmpty())
                .collect(Collectors.toList());

        PaginationInfo adminPg = getPaginationInfo(adminList, tab.equals("admin") ? pageNo : 1);
        PaginationInfo patientPg = getPaginationInfo(allPatients, tab.equals("patient") ? pageNo : 1);
        PaginationInfo doctorPg = getPaginationInfo(doctorList, tab.equals("doctor") ? pageNo : 1);
        PaginationInfo pharmacistPg = getPaginationInfo(pharmacistList, tab.equals("pharmacist") ? pageNo : 1);
        PaginationInfo clinicAssistantPg = getPaginationInfo(clinicAssistantList,
                tab.equals("clinic_assistant") ? pageNo : 1);
        PaginationInfo radiographerPg = getPaginationInfo(radiographerList, tab.equals("radiographer") ? pageNo : 1);
        PaginationInfo radiologistPg = getPaginationInfo(radiologistList, tab.equals("radiologist") ? pageNo : 1);
        PaginationInfo assignedPg = getPaginationInfo(assignedPatients, tab.equals("assigned") ? pageNo : 1);
        PaginationInfo unassignedPg = getPaginationInfo(unassignedPatients, tab.equals("assigned") ? pageNo : 1);

        model.addAttribute("adminList", adminPg.getDataToDisplay());
        model.addAttribute("patientList", patientPg.getDataToDisplay());
        model.addAttribute("doctorList", doctorPg.getDataToDisplay());
        model.addAttribute("pharmacistList", pharmacistPg.getDataToDisplay());
        model.addAttribute("clinicAssistantList", clinicAssistantPg.getDataToDisplay());
        model.addAttribute("radiographerList", radiographerPg.getDataToDisplay());
        model.addAttribute("radiologistList", radiologistPg.getDataToDisplay());
        model.addAttribute("assignedPatients", assignedPg.getDataToDisplay());
        model.addAttribute("unassignedPatients", unassignedPg.getDataToDisplay());

        model.addAttribute("adminPagination", adminPg);
        model.addAttribute("patientPagination", patientPg);
        model.addAttribute("doctorPagination", doctorPg);
        model.addAttribute("pharmacistPagination", pharmacistPg);
        model.addAttribute("clinicAssistantPagination", clinicAssistantPg);
        model.addAttribute("radiographerPagination", radiographerPg);
        model.addAttribute("radiologistPagination", radiologistPg);
        model.addAttribute("assignedPagination", assignedPg);
        model.addAttribute("unassignedPagination", unassignedPg);

        model.addAttribute("currentTab", tab);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("message", model.asMap().get("message"));

        return "adminDashboard";
    }

    // ── null-safe contains helper ──────────────────────────────────────
    private boolean contains(String field, String query) {
        return field != null && field.toLowerCase().contains(query);
    }

    // ──────────────────────────── add user page ───────────────────────
    @GetMapping("/adduser")
    public String showAddUserPage() {
        return "addUser";
    }

    // ──────────────────────────── add user ────────────────────────────
    @PostMapping("/adduser")
    public String saveUserInformation(
            @RequestParam("userId") String userId,
            @RequestParam("userFullName") String name,
            @RequestParam("userPassword") String password,
            @RequestParam("userEmail") String email,
            @RequestParam("contact") String contact,
            @RequestParam("role") String role,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "emergencyContact", required = false) String emergencyContact,
            @RequestParam(value = "sensorId", required = false) String sensorId,
            @RequestParam(value = "doctorHospital", required = false) String doctorHospital,
            @RequestParam(value = "doctorPosition", required = false) String doctorPosition,
            @RequestParam(value = "pharmacistHospital", required = false) String pharmacistHospital,
            @RequestParam(value = "pharmacistPosition", required = false) String pharmacistPosition,
            @RequestParam(value = "clinicAssistantClinic", required = false) String clinicAssistantClinic,
            @RequestParam(value = "clinicAssistantPosition", required = false) String clinicAssistantPosition,
            @RequestParam(value = "radiographerDepartment", required = false) String radiographerDepartment,
            @RequestParam(value = "radiographerPosition", required = false) String radiographerPosition,
            @RequestParam(value = "radiologistDepartment", required = false) String radiologistDepartment,
            @RequestParam(value = "radiologistSpecialization", required = false) String radiologistSpecialization,
            @RequestParam("action") String action,
            RedirectAttributes redirect)
            throws ExecutionException, InterruptedException {

        String message = "";

        if ("add".equalsIgnoreCase(action)) {
            switch (role) {
                case "PATIENT":
                    Patient p = new Patient(userId, name, password, contact, role, email,
                            address, emergencyContact, sensorId, "", "Under Surveillance");
                    message = patientService.createPatient(p);
                    mailService.sendMail(email, "Welcome to WellCheck",
                            "Dear " + name + ",\n\nYou have been registered as a patient.\nUser ID: " + userId
                                    + "\nTemporary Password: " + password
                                    + "\n\nPlease log in and change your password.");
                    break;

                case "DOCTOR":
                    Doctor d = new Doctor(userId, name, password, contact, role, email,
                            doctorHospital, doctorPosition);
                    message = doctorService.saveDoctor(d);
                    mailService.sendMail(email, "Welcome to WellCheck",
                            "Dear Dr. " + name + ",\n\nYou have been registered as a doctor.\nUser ID: " + userId
                                    + "\nTemporary Password: " + password
                                    + "\n\nPlease log in and change your password.");
                    break;

                case "PHARMACIST":
                    Pharmacist ph = new Pharmacist(userId, name, password, contact, role, email,
                            pharmacistHospital, pharmacistPosition);
                    message = pharmacistService.createPharmacist(ph);
                    mailService.sendMail(email, "Welcome to WellCheck",
                            "Dear " + name + ",\n\nYou have been registered as a pharmacist.\nUser ID: " + userId
                                    + "\nTemporary Password: " + password
                                    + "\n\nPlease log in and change your password.");
                    break;

                case "CLINIC_ASSISTANT":
                    ClinicAssistant ca = new ClinicAssistant(userId, name, password, contact, role, email,
                            clinicAssistantClinic, clinicAssistantPosition);
                    message = clinicAssistantService.createClinicAssistant(ca);
                    mailService.sendMail(email, "Welcome to WellCheck",
                            "Dear " + name + ",\n\nYou have been registered as a Clinic Assistant.\nUser ID: " + userId
                                    + "\nTemporary Password: " + password
                                    + "\n\nPlease log in and change your password.");
                    break;

                case "RADIOGRAPHER":
                    Radiographer rg = new Radiographer(userId, name, password, contact, role, email,
                            radiographerDepartment, radiographerPosition);
                    message = radiographerService.createRadiographer(rg);
                    mailService.sendMail(email, "Welcome to WellCheck",
                            "Dear " + name + ",\n\nYou have been registered as a Radiographer.\nUser ID: " + userId
                                    + "\nTemporary Password: " + password
                                    + "\n\nPlease log in and change your password.");
                    break;

                case "RADIOLOGIST":
                    Radiologist rl = new Radiologist(userId, name, password, contact, role, email,
                            radiologistDepartment, radiologistSpecialization);
                    message = radiologistService.createRadiologist(rl);
                    mailService.sendMail(email, "Welcome to WellCheck",
                            "Dear Dr. " + name + ",\n\nYou have been registered as a Radiologist.\nUser ID: " + userId
                                    + "\nTemporary Password: " + password
                                    + "\n\nPlease log in and change your password.");
                    break;

                default:
                    User u = new User(userId, name, password, contact, role, email);
                    message = userService.createUser(u);
                    break;
            }
        }

        String time = new SimpleDateFormat("MM dd yyyy HH:mm").format(new Date());
        redirect.addFlashAttribute("message", message + " at " + time);
        return "redirect:/admin?tab=" + role.toLowerCase() + "&pageNo=1";
    }

    // ──────────────────────────── edit user ───────────────────────────
    @PostMapping("/edituser")
    public String editUser(
            @RequestParam("userId") String userId,
            @RequestParam("userFullName") String name,
            @RequestParam("contact") String contact,
            @RequestParam("userEmail") String email,
            @RequestParam("role") String role,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "emergencyContact", required = false) String emergencyContact,
            @RequestParam(value = "sensorId", required = false) String sensorId,
            @RequestParam(value = "doctorHospital", required = false) String doctorHospital,
            @RequestParam(value = "doctorPosition", required = false) String doctorPosition,
            @RequestParam(value = "pharmacistHospital", required = false) String pharmacistHospital,
            @RequestParam(value = "pharmacistPosition", required = false) String pharmacistPosition,
            @RequestParam(value = "clinicAssistantClinic", required = false) String clinicAssistantClinic,
            @RequestParam(value = "clinicAssistantPosition", required = false) String clinicAssistantPosition,
            @RequestParam(value = "radiographerDepartment", required = false) String radiographerDepartment,
            @RequestParam(value = "radiographerPosition", required = false) String radiographerPosition,
            @RequestParam(value = "radiologistDepartment", required = false) String radiologistDepartment,
            @RequestParam(value = "radiologistSpecialization", required = false) String radiologistSpecialization,
            RedirectAttributes redirect)
            throws ExecutionException, InterruptedException {

        String msg = "";

        switch (role) {
            case "PATIENT":
                Patient p = new Patient(userId, name, "", contact, role, email,
                        address, emergencyContact, sensorId, "", "Under Surveillance");
                msg = patientService.updatePatient(p);
                break;
            case "DOCTOR":
                Doctor d = new Doctor(userId, name, "", contact, role, email,
                        doctorHospital, doctorPosition);
                msg = doctorService.updateDoctor(d);
                break;
            case "PHARMACIST":
                Pharmacist ph = new Pharmacist(userId, name, "", contact, role, email,
                        pharmacistHospital, pharmacistPosition);
                msg = pharmacistService.updatePharmacist(ph);
                break;
            case "CLINIC_ASSISTANT":
                ClinicAssistant ca = new ClinicAssistant(userId, name, "", contact, role, email,
                        clinicAssistantClinic, clinicAssistantPosition);
                msg = clinicAssistantService.updateClinicAssistant(ca);
                break;
            case "RADIOGRAPHER":
                Radiographer rg = new Radiographer(userId, name, "", contact, role, email,
                        radiographerDepartment, radiographerPosition);
                msg = radiographerService.updateRadiographer(rg);
                break;
            case "RADIOLOGIST":
                Radiologist rl = new Radiologist(userId, name, "", contact, role, email,
                        radiologistDepartment, radiologistSpecialization);
                msg = radiologistService.updateRadiologist(rl);
                break;
            default:
                User u = new User(userId, name, "", contact, role, email);
                msg = userService.updateUser(u);
                break;
        }

        redirect.addFlashAttribute("message", msg);
        return "redirect:/admin?tab=" + role.toLowerCase() + "&pageNo=1";
    }

    // ─────────────────────────── delete user ──────────────────────────
    @PostMapping("/deleteuser")
    public String deleteUser(
            @RequestParam String userIdToBeDelete,
            @RequestParam String userRoleToBeDelete,
            RedirectAttributes redirect) {

        String msg;
        try {
            switch (userRoleToBeDelete) {
                case "PATIENT":
                    msg = patientService.deletePatient(userIdToBeDelete);
                    break;
                case "DOCTOR":
                    msg = doctorService.deleteDoctor(userIdToBeDelete);
                    break;
                case "PHARMACIST":
                    msg = pharmacistService.deletePharmacist(userIdToBeDelete);
                    break;
                case "CLINIC_ASSISTANT":
                    msg = clinicAssistantService.deleteClinicAssistant(userIdToBeDelete);
                    break;
                default:
                    msg = userService.deleteUser(userIdToBeDelete);
                    break;
            }
        } catch (Exception ex) {
            msg = "Error deleting user: " + ex.getMessage();
        }

        redirect.addFlashAttribute("message", msg);
        return "redirect:/admin?tab=" + userRoleToBeDelete.toLowerCase() + "&pageNo=1";
    }

    // ─────────────────────── reset patient password ───────────────────
    @PostMapping("/resetPassword")
    @ResponseBody
    public String resetPatientPassword(
            @RequestParam String patientId,
            @RequestParam String newPassword) {
        try {
            return patientService.resetPatientPassword(patientId, newPassword);
        } catch (Exception e) {
            return "❌ Error resetting password: " + e.getMessage();
        }
    }

    // ──────────────────── doctor schedule: list page ──────────────────
    @GetMapping("/doctor-schedule")
    public String getDoctorSchedulePage(Model model) throws ExecutionException, InterruptedException {
        List<DoctorSchedule> schedules = doctorScheduleService.getAllSchedules();
        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("schedules", schedules);
        model.addAttribute("doctors", doctors);
        return "doctorSchedule";
    }

    // ──────────────────── doctor schedule: save/update ────────────────
    @PostMapping("/doctor-schedule/save")
    public String saveSchedule(
            @RequestParam String doctorId,
            @RequestParam String doctorName,
            @RequestParam List<String> workingDays,
            @RequestParam String workingHoursStart,
            @RequestParam String workingHoursEnd,
            @RequestParam int slotDurationMinutes,
            @RequestParam(required = false) List<String> breakStart,
            @RequestParam(required = false) List<String> breakEnd,
            @RequestParam(defaultValue = "false") boolean isActive,
            RedirectAttributes redirect) {

        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctorId(doctorId);
        schedule.setDoctorName(doctorName);
        schedule.setWorkingDays(workingDays);
        schedule.setSlotDurationMinutes(slotDurationMinutes);
        schedule.setActive(isActive);

        DoctorSchedule.WorkingHours wh = new DoctorSchedule.WorkingHours();
        wh.setStart(workingHoursStart);
        wh.setEnd(workingHoursEnd);
        schedule.setWorkingHours(wh);

        if (breakStart != null && breakEnd != null) {
            List<DoctorSchedule.BreakTime> breaks = new ArrayList<>();
            for (int i = 0; i < breakStart.size(); i++) {
                if (i < breakEnd.size()
                        && breakStart.get(i) != null && !breakStart.get(i).isEmpty()
                        && breakEnd.get(i) != null && !breakEnd.get(i).isEmpty()) {
                    DoctorSchedule.BreakTime bt = new DoctorSchedule.BreakTime();
                    bt.setStart(breakStart.get(i));
                    bt.setEnd(breakEnd.get(i));
                    breaks.add(bt);
                }
            }
            schedule.setBreakTimes(breaks);
        }

        doctorScheduleService.saveSchedule(schedule);
        redirect.addFlashAttribute("message", "Schedule saved for " + doctorName);
        return "redirect:/admin/doctor-schedule";
    }

    // ──────────────────── doctor schedule: delete ─────────────────────
    @PostMapping("/doctor-schedule/delete")
    public String deleteSchedule(
            @RequestParam String doctorId,
            RedirectAttributes redirect) {
        doctorScheduleService.deleteSchedule(doctorId);
        redirect.addFlashAttribute("message", "Schedule deleted.");
        return "redirect:/admin/doctor-schedule";
    }

    // ──────────────────── doctor schedule: toggle active ──────────────
    @PostMapping("/doctor-schedule/toggle")
    public String toggleSchedule(
            @RequestParam String doctorId,
            RedirectAttributes redirect) {
        String msg = doctorScheduleService.toggleActive(doctorId);
        redirect.addFlashAttribute("message", msg);
        return "redirect:/admin/doctor-schedule";
    }

    // ───────────────────────── helper: pagination ─────────────────────
    private PaginationInfo getPaginationInfo(List<?> dataList, int pageNo) {
        int pageSize = 5;
        int totalItems = dataList.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        pageNo = Math.max(1, Math.min(pageNo, totalPages));
        int start = (pageNo - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        List<?> pageData = dataList.subList(start, end);

        return new PaginationInfo(pageData, pageSize, pageNo, totalPages,
                Math.max(1, pageNo - 1), Math.min(totalPages, pageNo + 1));
    }
}