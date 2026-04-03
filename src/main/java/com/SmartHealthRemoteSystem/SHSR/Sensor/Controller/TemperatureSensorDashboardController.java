package com.SmartHealthRemoteSystem.SHSR.Sensor.Controller;

import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TemperatureSensorDashboardController {

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private PatientService patientService;

    @GetMapping("/patient/temperatureDashboard")
    public String viewTemperatureSensorDashboard(Model model) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Patient patient = patientService.getPatientById(myUserDetails.getUsername());

        model.addAttribute("patient", patient);
        model.addAttribute("patientid", patient.getUserId());

        if (patient.getSensorDataId() == null) {
            model.addAttribute("sensorDataList", null);
            model.addAttribute("sensorDataListHistory", null);
            return "temperatureSensorDashboard";
        }

        SensorData sensorData = sensorDataService.getSensorById(patient.getSensorDataId());
        model.addAttribute("sensorDataList", sensorData);

        if (sensorData.getHistory() != null && !sensorData.getHistory().isEmpty()) {
            model.addAttribute("sensorDataListHistory", sensorData.getHistory());
        } else {
            model.addAttribute("sensorDataListHistory", null);
        }

        return "temperatureSensorDashboard";
    }
}
