package com.SmartHealthRemoteSystem.SHSR.User.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SmartHealthRemoteSystem.SHSR.Pagination.PaginationInfo;
import com.SmartHealthRemoteSystem.SHSR.Service.*;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.Pharmacist.Pharmacist;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // ──────────────────────────── services ────────────────────────────
    private final UserService         userService;
    private final PatientService      patientService;
    private final DoctorService       doctorService;
    private final PharmacistService   pharmacistService;
    private final MailService         mailService;

    @Autowired
    public AdminController(UserService userService,
                           PatientService patientService,
                           DoctorService doctorService,
                           PharmacistService pharmacistService,
                           MailService mailService) {
        this.userService        = userService;
        this.patientService     = patientService;
        this.doctorService      = doctorService;
        this.pharmacistService  = pharmacistService;
        this.mailService        = mailService;
    }

    // ──────────────────────────── dashboard ───────────────────────────
    @GetMapping
    public String getAdminDashboard(Model model,
                                    @RequestParam(defaultValue = "1")  int    pageNo,
                                    @RequestParam(defaultValue = "patient") String tab)
                                    throws ExecutionException, InterruptedException {

        // full datasets
        List<User>       adminList        = userService.getAdminList();
        List<Patient>    allPatients      = patientService.getAllPatients();
        List<Doctor>     doctorList       = doctorService.getAllDoctors();
        List<Pharmacist> pharmacistList   = pharmacistService.getListPharmacist();

        // assigned / unassigned split
        List<Patient> assignedPatients   = allPatients.stream()
                .filter(p -> p.getAssigned_doctor() != null && !p.getAssigned_doctor().isEmpty())
                .collect(Collectors.toList());

        List<Patient> unassignedPatients = allPatients.stream()
                .filter(p -> p.getAssigned_doctor() == null || p.getAssigned_doctor().isEmpty())
                .collect(Collectors.toList());

        /*  Decide which list gets the requested page number.
            Others fall back to page 1 so they don’t overlap.                 */
        PaginationInfo adminPg       = getPaginationInfo(adminList,      tab.equals("admin")       ? pageNo : 1);
        PaginationInfo patientPg     = getPaginationInfo(allPatients,    tab.equals("patient")     ? pageNo : 1);
        PaginationInfo doctorPg      = getPaginationInfo(doctorList,     tab.equals("doctor")      ? pageNo : 1);
        PaginationInfo pharmacistPg  = getPaginationInfo(pharmacistList, tab.equals("pharmacist")  ? pageNo : 1);

        PaginationInfo assignedPg    = getPaginationInfo(assignedPatients,   tab.equals("assigned") ? pageNo : 1);
        PaginationInfo unassignedPg  = getPaginationInfo(unassignedPatients, tab.equals("assigned") ? pageNo : 1);

        /*  Put everything in the model  */
        model.addAttribute("adminList",      adminPg.getDataToDisplay());
        model.addAttribute("patientList",    patientPg.getDataToDisplay());
        model.addAttribute("doctorList",     doctorPg.getDataToDisplay());
        model.addAttribute("pharmacistList", pharmacistPg.getDataToDisplay());

        model.addAttribute("adminPagination",       adminPg);
        model.addAttribute("patientPagination",     patientPg);
        model.addAttribute("doctorPagination",      doctorPg);
        model.addAttribute("pharmacistPagination",  pharmacistPg);

        model.addAttribute("assignedPatients",      assignedPg.getDataToDisplay());
        model.addAttribute("unassignedPatients",    unassignedPg.getDataToDisplay());
        model.addAttribute("assignedPagination",    assignedPg);
        model.addAttribute("unassignedPagination",  unassignedPg);

        model.addAttribute("currentTab", tab);   // <─ used by Thymeleaf to highlight the active tab
        return "adminDashboard";
    }

    // ──────────────────────────── add user ────────────────────────────
    @PostMapping("/adduser")
    public String saveUserInformation(@RequestParam("userId")      String userId,
                                      @RequestParam("userFullName")String name,
                                      @RequestParam("userPassword")String password,
                                      @RequestParam("userEmail")   String email,
                                      @RequestParam("contact")     String contact,
                                      @RequestParam("role")        String role,
                                      /* optional extras … */       @RequestParam(value="address",             required=false) String address,
                                      @RequestParam(value="emergencyContact",  required=false) String emergencyContact,
                                      @RequestParam(value="sensorId",          required=false) String sensorId,
                                      @RequestParam(value="doctorHospital",    required=false) String doctorHospital,
                                      @RequestParam(value="doctorPosition",    required=false) String doctorPosition,
                                      @RequestParam(value="pharmacistHospital",required=false) String pharmacistHospital,
                                      @RequestParam(value="pharmacistPosition",required=false) String pharmacistPosition,
                                      @RequestParam("action")      String action,
                                      RedirectAttributes redirect) // <─ flash-scope
                                      throws ExecutionException, InterruptedException {

        String message = "";

        if ("add".equalsIgnoreCase(action)) {
           switch (role) {
    case "PATIENT":
        Patient p = new Patient(userId, name, password, contact, role, email,
                                address, emergencyContact, sensorId, "", "Under Surveillance");
        message = patientService.createPatient(p);
        mailService.sendMail(email, "Welcome to WellCheck",
                "Dear " + name + ",\n\nYou have been registered as a patient.\nUser ID: " + userId +
                "\nTemporary Password: " + password + "\n\nPlease log in and change your password.");
        break;

    case "DOCTOR":
        Doctor d = new Doctor(userId, name, password, contact, role, email, doctorHospital, doctorPosition);
        message = doctorService.saveDoctor(d);
        mailService.sendMail(email, "Welcome to WellCheck",
                "Dear Dr. " + name + ",\n\nYou have been registered as a doctor.\nUser ID: " + userId +
                "\nTemporary Password: " + password + "\n\nPlease log in and change your password.");
        break;

    case "PHARMACIST":
        Pharmacist ph = new Pharmacist(userId, name, password, contact, role, email,
                                       pharmacistHospital, pharmacistPosition);
        message = pharmacistService.createPharmacist(ph);
        mailService.sendMail(email, "Welcome to WellCheck",
                "Dear " + name + ",\n\nYou have been registered as a pharmacist.\nUser ID: " + userId +
                "\nTemporary Password: " + password + "\n\nPlease log in and change your password.");
        break;

    default:
        User u = new User(userId, name, password, contact, role, email);
        message = userService.createUser(u);
        break;
}

        }

        /* stamp the time & store in flash scope */
        String time = new SimpleDateFormat("MM dd yyyy HH:mm").format(new Date());
        redirect.addFlashAttribute("message", message + " at " + time);

        /* PRG redirect: go to page-1 of the role we just added */
        return "redirect:/admin?tab=" + role.toLowerCase() + "&pageNo=1";
    }

    // ──────────────────────────── edit user ───────────────────────────
  @PostMapping("/edituser")
public String editUser(@RequestParam("userId")              String userId,
                       @RequestParam("userFullName")        String name,
                       @RequestParam("contact")             String contact,
                       @RequestParam("userEmail")           String email,
                       @RequestParam("role")                String role,
                       @RequestParam(value = "address",             required = false) String address,
                       @RequestParam(value = "emergencyContact",    required = false) String emergencyContact,
                       @RequestParam(value = "sensorId",            required = false) String sensorId,
                       @RequestParam(value = "doctorHospital",      required = false) String doctorHospital,
                       @RequestParam(value = "doctorPosition",      required = false) String doctorPosition,
                       @RequestParam(value = "pharmacistHospital",  required = false) String pharmacistHospital,
                       @RequestParam(value = "pharmacistPosition",  required = false) String pharmacistPosition,
                       RedirectAttributes redirect)                     // flash scope
                       throws ExecutionException, InterruptedException {

    String msg = "";

    switch (role) {
        case "PATIENT":
            Patient p = new Patient(userId, name, "", contact, role, email,
                                    address, emergencyContact, sensorId, "",
                                    "Under Surveillance");
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
public String deleteUser(@RequestParam String userIdToBeDelete,
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
    public String resetPatientPassword(@RequestParam String patientId,
                                       @RequestParam String newPassword) {
        try {
            return patientService.resetPatientPassword(patientId, newPassword);
        } catch (Exception e) {
            return "❌ Error resetting password: " + e.getMessage();
        }
    }

    // ───────────────────────── helper: pagination ─────────────────────
    private PaginationInfo getPaginationInfo(List<?> dataList, int pageNo) {
        int pageSize   = 5;
        int totalItems = dataList.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        pageNo = Math.max(1, Math.min(pageNo, totalPages)); // clamp
        int start = (pageNo - 1) * pageSize;
        int end   = Math.min(start + pageSize, totalItems);

        List<?> pageData = dataList.subList(start, end);

        return new PaginationInfo(pageData, pageSize, pageNo, totalPages,
                                  Math.max(1, pageNo - 1), Math.min(totalPages, pageNo + 1));
    }
}
