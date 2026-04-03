
//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.ViewDailyHealth;

import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatus;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.HealthStatusService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/ViewDailyHealthSymptom")
public class ViewDailyHealthSymptomController {

    private final HealthStatusService healthStatusService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @Autowired
    public ViewDailyHealthSymptomController(HealthStatusService healthStatusService, DoctorService doctorService, PatientService patientService) {
        this.healthStatusService = healthStatusService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    @GetMapping("/b")
    public String getHealthStatus(@RequestParam("patientId") String patientId,
                                  @RequestParam("doctorId") String doctorId,
                                  Model model) throws ExecutionException, InterruptedException {
        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctor(doctorId);
        List<HealthStatus> healthStatus = healthStatusService.getListHealthStatus(patientId);

        model.addAttribute("patient", patient);
        model.addAttribute("doctor", doctor);
        model.addAttribute("healthStatusList", healthStatus);

        return "viewDailyHealthSymptom";
    }
}
