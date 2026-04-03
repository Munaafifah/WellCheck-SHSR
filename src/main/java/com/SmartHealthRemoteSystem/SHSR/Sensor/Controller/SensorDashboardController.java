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

@Controller
@RequestMapping("/patient")   // ONE base-path – patient version mounts here
public class SensorDashboardController {

    @Autowired private SensorDataService sensorDataService;
    @Autowired private PatientService    patientService;

    /* ────────────────────────────────────────────────────────────────
       PATIENT route → /patient/sensorDashboard
       ──────────────────────────────────────────────────────────────── */
    @GetMapping("/sensorDashboard")
    public String patientSensorDashboard(
            @RequestParam(value = "patientId", required = false) String patientIdParam,
            @RequestParam(value = "page",       defaultValue = "1") int page,
            @RequestParam(value = "filterDate", required = false)   String filterDate,
            Model model) throws Exception {

        // delegate to shared method (role = "patient")
        return buildDashboard(patientIdParam, "patient", page, filterDate, model);
    }

    /* =================================================================
       SHARED IMPLEMENTATION – made PUBLIC so DoctorController can call
       ================================================================= */
    public String buildDashboard(String patientIdParam,
                                 String role,
                                 int    page,
                                 String filterDate,
                                 Model  model) throws Exception {

        /* 1️⃣  Who is the patient? */
        String patientId;
        if (patientIdParam != null && !patientIdParam.isEmpty()) {
            patientId = patientIdParam;                       // doctor/admin view
        } else {                                              // logged-in patient
            Authentication auth  = SecurityContextHolder.getContext().getAuthentication();
            MyUserDetails user   = (MyUserDetails) auth.getPrincipal();
            patientId            = user.getUsername();
        }

        Patient patient = patientService.getPatientById(patientId);
        model.addAttribute("patient",   patient);
        model.addAttribute("patientid", patientId);
        model.addAttribute("role",      role);                // back-button support

        /* 2️⃣  No sensor yet */
        if (patient.getSensorDataId() == null || patient.getSensorDataId().isEmpty()) {
            model.addAttribute("sensorData",         null);
            model.addAttribute("sensorDataPage",     null);
            model.addAttribute("latestFiveReadings", null);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages",  1);
            return "sensorDashboard";
        }

        /* 3️⃣  Load sensor + history */
        SensorData sensorData = sensorDataService.getSensorById(patient.getSensorDataId());
        model.addAttribute("sensorData", sensorData);

        if (sensorData == null || sensorData.getHistory() == null) {
            model.addAttribute("sensorDataPage",     null);
            model.addAttribute("latestFiveReadings", null);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalPages",  1);
            return "sensorDashboard";
        }

        /* 4️⃣  Prepare history list (max 50, newest first) */
        List<HistorySensorData> history = sensorData.getHistory()
                                                   .stream()
                                                   .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                                                   .limit(50)
                                                   .toList();

        /* 5️⃣  Optional date-filter */
        if (filterDate != null && !filterDate.isEmpty()) {
            history = history.stream()
                             .filter(h -> h.getTimestamp().toString().startsWith(filterDate))
                             .toList();
            model.addAttribute("filterDate", filterDate);
        }

        /* 6️⃣  Pagination */
        int pageSize = 5;
        int start    = Math.max(0, (page - 1) * pageSize);
        int end      = Math.min(start + pageSize, history.size());

        model.addAttribute("sensorDataPage", history.subList(start, end));
        model.addAttribute("currentPage",    page);
        model.addAttribute("totalPages",
                           (int) Math.ceil((double) history.size() / pageSize));

        /* 7️⃣  Spark-line / chart (latest 5) */
        model.addAttribute("latestFiveReadings",
                           history.stream().limit(5).toList());

        return "sensorDashboard";
    }
}
