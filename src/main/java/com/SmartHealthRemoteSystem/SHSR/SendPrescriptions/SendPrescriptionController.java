package com.SmartHealthRemoteSystem.SHSR.SendPrescriptions;

import com.SmartHealthRemoteSystem.SHSR.Medicine.Medicine;
import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
import com.SmartHealthRemoteSystem.SHSR.Service.MedicineService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PrescriptionService;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.Service.PharmacistService;
import com.SmartHealthRemoteSystem.SHSR.User.Pharmacist.Pharmacist;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Service.AppointmentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/prescription")
public class SendPrescriptionController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final PrescriptionService prescriptionService;
    private final MedicineService medicineService;
    private MailService mailService;
    private final PharmacistService pharmacistService;

    @Autowired
    private AppointmentHandler appointmentHandler; // ✅ inject AppointmentHandler

    @Autowired
    public SendPrescriptionController(DoctorService doctorService,
            PatientService patientService,
            PrescriptionService prescriptionService,
            MedicineService medicineService,
            MailService mailService,
            PharmacistService pharmacistService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.prescriptionService = prescriptionService;
        this.medicineService = medicineService;
        this.mailService = mailService;
        this.pharmacistService = pharmacistService;
    }

    // ✅ Show prescription form — appointmentId is optional
    @GetMapping("/form")
    public String getPrescriptionForm(
            @RequestParam String patientId,
            @RequestParam(required = false) String appointmentId,
            @RequestParam(defaultValue = "") String searchQuery,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,
            Model model) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(userDetails.getUsername());

        Patient patient = patientService.getPatientById(patientId);

        List<Medicine> allMedicines = medicineService.getAllMedicines();

        // Filter by search query
        if (!searchQuery.isEmpty()) {
            allMedicines = allMedicines.stream()
                    .filter(m -> m.getMedName().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Pagination
        int total = allMedicines.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min((pageNo + 1) * pageSize, total);
        List<Medicine> medicineList = allMedicines.subList(start, end);

        model.addAttribute("doctor", doctor);
        model.addAttribute("patientId", patientId);
        model.addAttribute("patientName", patient.getName());
        model.addAttribute("medicineList", medicineList);
        model.addAttribute("appointmentId", appointmentId);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (int) Math.ceil((double) total / pageSize));
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("startIndex", pageNo * pageSize);

        return "sendPrescriptionForm";
    }

    // Show medicine selection
    @GetMapping("/add-prescription")
    public String addMedication(@RequestParam String patientId, Model model) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(userDetails.getUsername());

        List<Medicine> medicineList = medicineService.getAllMedicines();
        Patient patient = patientService.getPatientById(patientId);

        model.addAttribute("doctor", doctor);
        model.addAttribute("patientId", patientId);
        model.addAttribute("patientName", patient.getName());
        model.addAttribute("medicineList", medicineList);

        return "patientMedicine";
    }

    // ✅ Submit prescription form
    @PostMapping("/form/submit")
    public String submitPrescriptionForm(
            @RequestParam String patientId,
            @RequestParam String doctorId,
            @RequestParam String prescription,
            @RequestParam String diagnosisAilment,
            @RequestParam(required = false) String appointmentId, // ✅ optional
            HttpServletRequest request,
            Model model) throws Exception {

        Map<String, String[]> paramMap = request.getParameterMap();
        Map<String, Integer> prescribedMedicines = new HashMap<>();

        int index = 0;
        while (paramMap.containsKey("medicines[" + index + "].medId")) {
            String medId = request.getParameter("medicines[" + index + "].medId");
            String selected = request.getParameter("medicines[" + index + "].selected");
            String quantityStr = request.getParameter("medicines[" + index + "].quantity");

            if ("true".equals(selected) && quantityStr != null && !quantityStr.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityStr);
                    prescribedMedicines.put(medId, quantity);
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
            index++;
        }

        // ✅ Save prescription and get result (prescriptionId + drugCost)
        Map<String, Object> prescriptionResult = prescriptionService.prescribeMedicines(
                patientId, prescribedMedicines, prescription, diagnosisAilment);

        String prescriptionId = (String) prescriptionResult.get("prescriptionId");
        double drugCost = (double) prescriptionResult.get("drugCost");

        // ✅ If from appointment — link appointmentId and save drug cost to appointment
        // ✅ If NOT from appointment — skip, prescription still saved normally
        if (appointmentId != null && !appointmentId.isEmpty()) {
            prescriptionService.linkAppointmentToPrescription(patientId, prescriptionId, appointmentId);
            appointmentHandler.updateDrugCost(appointmentId, drugCost);
        }

        // ✅ Send Email Notification to Patient
        Patient patient = patientService.getPatientById(patientId);
        Doctor doctor = doctorService.getDoctor(doctorId);

        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            String subject = "New Prescription from Dr. " + doctor.getName();
            String message = "Dear " + patient.getName() + ",\n\n"
                    + "You have received a new prescription from Dr. " + doctor.getName() + ".\n"
                    + "Diagnosis: " + diagnosisAilment + "\n"
                    + "Prescription: " + prescription + "\n\n"
                    + "Please log in to view the full details.\n\n"
                    + "Regards,\nWellCheck System";
            mailService.sendMail(patient.getEmail(), subject, message);
        }

        // ✅ Send Email Notification to All Pharmacists
        List<Pharmacist> allPharmacists = pharmacistService.getListPharmacist();
        for (Pharmacist pharmacist : allPharmacists) {
            if (pharmacist.getEmail() != null && !pharmacist.getEmail().isEmpty()) {
                String pharmacistSubject = "Prescription Issued for Patient: " + patient.getName();
                String pharmacistMessage = "Dear " + pharmacist.getName() + ",\n\n"
                        + "A new prescription has been submitted by Dr. " + doctor.getName()
                        + " for patient " + patient.getName() + ".\n"
                        + "Diagnosis: " + diagnosisAilment + "\n\n"
                        + "Please log in to view and prepare the medicine accordingly.\n\n"
                        + "Regards,\nWellCheck System";
                mailService.sendMail(pharmacist.getEmail(), pharmacistSubject, pharmacistMessage);
            }
        }

        return "redirect:/prescription/history?patientId=" + patientId;
    }

    // Submit selected medicines with quantity
    @PostMapping("/prescribemedicine/submit")
    public String submitMedicineForm(@RequestParam String patientId,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes,
            Model model) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(userDetails.getUsername());

        Map<String, Integer> selectedMedicines = allParams.entrySet().stream()
                .filter(e -> e.getKey().startsWith("quantity_") && !e.getValue().isEmpty())
                .collect(Collectors.toMap(
                        e -> e.getKey().substring("quantity_".length()),
                        e -> Integer.parseInt(e.getValue())));

        prescriptionService.prescribeMedicines(patientId, selectedMedicines,
                "Prescription Description", "Diagnosis Ailment Description");

        redirectAttributes.addAttribute("patientId", patientId);
        return "redirect:/prescription/form";
    }

    // Prescription history for a patient
    @GetMapping("/history")
    public String viewPrescriptionHistory(@RequestParam String patientId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String searchQuery,
            Model model) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Doctor doctor = doctorService.getDoctor(userDetails.getUsername());

        model.addAttribute("doctor", doctor);

        List<Prescription> allPrescriptions = prescriptionService.getAllPrescriptions(patientId);

        if (!searchQuery.isEmpty()) {
            allPrescriptions = allPrescriptions.stream()
                    .filter(p -> p.getPrescriptionDescription().toLowerCase().contains(searchQuery.toLowerCase())
                            || p.getDiagnosisAilmentDescription().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }

        int total = allPrescriptions.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min((pageNo + 1) * pageSize, total);

        List<Prescription> pagedList = allPrescriptions.subList(start, end);

        model.addAttribute("prescriptionHistory", pagedList);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("startIndex", pageNo * pageSize);

        return "prescriptionHistory";
    }
}