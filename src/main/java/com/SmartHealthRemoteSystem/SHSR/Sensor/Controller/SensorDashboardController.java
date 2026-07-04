package com.SmartHealthRemoteSystem.SHSR.Sensor.Controller;

import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.HistorySensorData;
import com.SmartHealthRemoteSystem.SHSR.ReadSensorData.SensorData;
import com.SmartHealthRemoteSystem.SHSR.Service.SensorDataService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/patient")
public class SensorDashboardController {

    @Autowired private SensorDataService sensorDataService;
    @Autowired private PatientService    patientService;

    @GetMapping("/sensorDashboard")
    public String patientSensorDashboard(
            @RequestParam(value = "patientId", required = false) String patientIdParam,
            @RequestParam(value = "page",       defaultValue = "1") int page,
            @RequestParam(value = "filterDate", required = false)   String filterDate,
            Model model) throws Exception {

        return buildDashboard(patientIdParam, "patient", page, filterDate, model);
    }

    public String buildDashboard(String patientIdParam,
                                 String role,
                                 int    page,
                                 String filterDate,
                                 Model  model) throws Exception {

        String patientId;
        if (patientIdParam != null && !patientIdParam.isEmpty()) {
            patientId = patientIdParam;
        } else {
            Authentication auth  = SecurityContextHolder.getContext().getAuthentication();
            MyUserDetails user   = (MyUserDetails) auth.getPrincipal();
            patientId            = user.getUsername();
        }

        Patient patient = patientService.getPatientById(patientId);
        model.addAttribute("patient",   patient);
        model.addAttribute("patientid", patientId);
        model.addAttribute("role",      role);

        if (patient.getSensorDataId() == null || patient.getSensorDataId().isEmpty()) {
            model.addAttribute("sensorData",         null);
            model.addAttribute("sensorDataPage",     null);
            model.addAttribute("latestFiveReadings", null);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages",  1);
            return "sensorDashboard";
        }

        SensorData sensorData = sensorDataService.getSensorById(patient.getSensorDataId());
        model.addAttribute("sensorData", sensorData);

        if (sensorData == null || sensorData.getHistory() == null) {
            model.addAttribute("sensorDataPage",     null);
            model.addAttribute("latestFiveReadings", null);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages",  1);
            return "sensorDashboard";
        }

        List<HistorySensorData> history = sensorData.getHistory()
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(50)
                .collect(Collectors.toList());

        if (filterDate != null && !filterDate.isEmpty()) {
            history = history.stream()
                    .filter(h -> h.getTimestamp().toString().startsWith(filterDate))
                    .collect(Collectors.toList());
            model.addAttribute("filterDate", filterDate);
        }

        int pageSize = 5;
        int start    = Math.max(0, (page - 1) * pageSize);
        int end      = Math.min(start + pageSize, history.size());

        model.addAttribute("pagedHistory", history.subList(start, end));
        model.addAttribute("currentPage",    page);
        model.addAttribute("totalPages",
                (int) Math.ceil((double) history.size() / pageSize));

        model.addAttribute("latestFiveReadings",
                history.stream().limit(5).collect(Collectors.toList()));

        return "sensorDashboard";
    }
}