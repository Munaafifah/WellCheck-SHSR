package com.SmartHealthRemoteSystem.SHSR.registerPatient;

import java.util.concurrent.ExecutionException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.UserService;
import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.User;

@Controller
@RequestMapping("/registerPatient")
public class RegisterPatientController {

    private final PatientService patientService;
    private final UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    public RegisterPatientController(PatientService patientService, UserService userService) {
        this.patientService = patientService;
        this.userService = userService;
    }

    @GetMapping
    public String registerForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "registerPatientForm";
    }

    @PostMapping
    public String registerPatient(
            @Valid @RequestParam("userId") String userId,
            @RequestParam("name") String name,
            @RequestParam("password") String password,
            @RequestParam("contact") String contact,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            @RequestParam("emergencyContact") String emergencyContact,
            Model model) throws ExecutionException, InterruptedException {

        // Check if user already exists
        if (userService.getUser(userId) != null) {
            model.addAttribute("error", "User ID already exists.");
            model.addAttribute("patient", new Patient());
            return "registerPatientForm";
        }

        // Create Patient object
        Patient newPatient = new Patient(
                userId,
                name,
                password,
                contact,
                "PATIENT",
                email,
                address,
                emergencyContact,
                "",  // sensorDataId
                "",  // assigned_doctor
                "Under Surveillance"
        );

        // Save patient to MongoDB
        String result = patientService.createPatient(newPatient);
        if (result.contains("already exists")) {
            model.addAttribute("error", result);
            model.addAttribute("patient", new Patient());
            return "registerPatientForm";
        }

        // Save user to general user collection
        User user = new User(
                userId,
                name,
                password,
                contact,
                "PATIENT",
                email
        );
        userService.createUser(user);

        // ✅ Send welcome email to the patient
        if (email != null && !email.isEmpty()) {
            String subject = "Welcome to WellCheck!";
            String message = "Dear " + name + ",\n\n"
                           + "Your registration to the WellCheck Health Monitoring System was successful.\n"
                           + "You can now log in and start using the system to track your health.\n\n"
                           + "Regards,\nWellCheck Team";
            mailService.sendMail(email, subject, message);
        }

        return "redirect:/login";
    }
}
