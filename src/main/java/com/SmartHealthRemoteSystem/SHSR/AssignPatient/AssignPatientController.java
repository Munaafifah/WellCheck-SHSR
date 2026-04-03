
//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.AssignPatient;

import com.SmartHealthRemoteSystem.SHSR.Mail.MailStructure;
import com.SmartHealthRemoteSystem.SHSR.Service.AssignPatientServices;
import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assignpatient")
public class AssignPatientController {

    private final AssignPatientServices assignPatientServices;
    private final MailService mailService;

    public AssignPatientController(AssignPatientServices assignPatientServices, MailService mailService) {
        this.assignPatientServices = assignPatientServices;
        this.mailService = mailService;
    }

    @GetMapping
    public String showUnassignedPatients(Model model,
                                         @RequestParam(defaultValue = "0") int pageNo,
                                         @RequestParam(defaultValue = "5") int pageSize,
                                         @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = assignPatientServices.getDoctor(myUserDetails.getUsername());

        // List<Patient> allPatients = assignPatientServices.getListPatient();
        List<Patient> allPatients = assignPatientServices.getListUnassignedPatients();


        if (!searchQuery.isEmpty()) {
            allPatients = allPatients.stream()
                    .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                 p.getUserId().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }

        int total = allPatients.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min((pageNo + 1) * pageSize, total);
        List<Patient> patientList = allPatients.subList(start, end);

        model.addAttribute("startIndex", pageNo * pageSize);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("patientList", patientList);
        model.addAttribute("doctor", doctor);
        model.addAttribute("searchQuery", searchQuery);

        return "assignpatient";
    }

    @PostMapping("/assigntoDoctor")
    public String assignPatient(@RequestParam("patientId") String patientId,
                                Model model,
                                @RequestParam(defaultValue = "0") int pageNo,
                                @RequestParam(defaultValue = "5") int pageSize,
                                @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = assignPatientServices.getDoctor(myUserDetails.getUsername());
        Patient patient = assignPatientServices.getPatient(patientId);

        // assignPatientServices.AssignPatient(patientId, doctor.getUserId());
        assignPatientServices.assignPatient(patientId, doctor.getUserId()); // ✅ correct


        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            String subject = "You have been ASSIGNED to a Doctor";
            String message = "Dear " + patient.getName() + ",\n\nYou have been assigned to Dr. "
                    + doctor.getName() + " (" + doctor.getUserId() + ").\n\nThank you.";
            mailService.sendAssignedMail(patient.getEmail(), subject, message);
        }

        return "redirect:/assignpatient?pageNo=" + pageNo + "&pageSize=" + pageSize + "&searchQuery=" + searchQuery;
    }

    @PostMapping("/unassignDoctor")
public String unassignDoctor(@RequestParam("patientId") String patientId,
                             @RequestParam(defaultValue = "0") int pageNo,
                             @RequestParam(defaultValue = "5") int pageSize,
                             @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
    Doctor doctor = assignPatientServices.getDoctor(myUserDetails.getUsername());
    Patient patient = assignPatientServices.getPatient(patientId);

    assignPatientServices.UnassignDoctor(patientId, doctor.getUserId());

    if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
        String subject = "You have been UNASSIGNED from a Doctor";
        String message = "Dear " + patient.getName() + ",\n\nYou have been unassigned from Dr. "
                + doctor.getName() + " (" + doctor.getUserId() + ").\n\nThank you.";
        mailService.sendUnassignedMail(patient.getEmail(), subject, message);
    }

    return "redirect:/assignpatient?pageNo=" + pageNo + "&pageSize=" + pageSize + "&searchQuery=" + searchQuery;
}

    @PostMapping("/releasepatient")
public String releasePatient(@RequestParam("patientId") String patientId,
                             @RequestParam(defaultValue = "0") int pageNo,
                             @RequestParam(defaultValue = "5") int pageSize,
                             @RequestParam(defaultValue = "") String searchQuery)
        throws ExecutionException, InterruptedException {
    
    // ✅ Add debug log to confirm button triggered
    System.out.println("▶️ Releasing patient: " + patientId);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
    Doctor doctor = assignPatientServices.getDoctor(myUserDetails.getUsername());
    Patient patient = assignPatientServices.getPatient(patientId);

    assignPatientServices.releasePatient(patientId);

    if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
        String subject = "You have been RELEASED from a Doctor";
        String message = "Dear " + patient.getName() + ",\n\nYou have been released from Dr. "
                + doctor.getName() + " (" + doctor.getUserId() + ").\n\nThank you.";
        mailService.sendUnassignedMail(patient.getEmail(), subject, message);
    }

    return "redirect:/assignpatient?pageNo=" + pageNo + "&pageSize=" + pageSize + "&searchQuery=" + searchQuery;
}

}

