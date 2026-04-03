
//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.User.Pharmacist;

import com.SmartHealthRemoteSystem.SHSR.Medicine.Medicine;
import com.SmartHealthRemoteSystem.SHSR.Service.MedicineService;
import com.SmartHealthRemoteSystem.SHSR.Service.PatientService;
import com.SmartHealthRemoteSystem.SHSR.Service.PharmacistService;
import com.SmartHealthRemoteSystem.SHSR.Service.PrescriptionService;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.ViewDoctorPrescription.Prescription;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pharmacist")
public class PharmacistController {

    

@Autowired
private PharmacistService pharmacistService;

@Autowired
private MedicineService medicineService;

@Autowired
private PrescriptionService prescriptionService;

@Autowired
private PatientService patientService;


    
   @Autowired
    public PharmacistController(PharmacistService pharmacistService, MedicineService medicineService) {
    this.pharmacistService = pharmacistService;
    this.medicineService = medicineService;
}

    // ✅ Pharmacist Dashboard
    @GetMapping
    public String pharmacistDashboard(Model model,
                                      @RequestParam(defaultValue = "0") int pageNo,
                                      @RequestParam(defaultValue = "5") int pageSize,
                                      @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Pharmacist pharmacist = pharmacistService.getPharmacist(myUserDetails.getUsername());

        List<Medicine> allMedicine = pharmacistService.getListMedicine(); // ✅ Fetch real medicine list

        if (!searchQuery.isEmpty()) {
            allMedicine = allMedicine.stream()
                    .filter(m -> m.getMedName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                 m.getMedId().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }

        int total = allMedicine.size();
        int start = Math.min(pageNo * pageSize, total);
        int end = Math.min((pageNo + 1) * pageSize, total);
        int startIndex = pageNo * pageSize;

        List<Medicine> medicineStock = allMedicine.subList(start, end);

        model.addAttribute("startIndex", startIndex);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pharmacist", pharmacist);
        model.addAttribute("medicineStock", medicineStock);
        model.addAttribute("searchQuery", searchQuery);

        return "PharmacistDashboard";
    }

    // Show Edit Profile Page//
    @GetMapping("/updateProfile")
    public String updatePharmacistProfile(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
        Pharmacist pharmacist = pharmacistService.getPharmacist(myUserDetails.getUsername());

        model.addAttribute("pharmacist", pharmacist);
        return "editProfilePharmacist";
    }

    // Handle Profile Update (with Image Upload)//
    @PostMapping("/updateProfile/profile")
    public String saveUpdatedPharmacist(@ModelAttribute Pharmacist pharmacist,
                                        @RequestParam("imageFile") MultipartFile imageFile) throws Exception {

        Pharmacist existing = pharmacistService.getPharmacist(pharmacist.getUserId());
        existing.setName(pharmacist.getName());
        existing.setContact(pharmacist.getContact());

        existing.setHospital(pharmacist.getHospital());
        existing.setPosition(pharmacist.getPosition());

        if (!imageFile.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
            existing.setProfilePicture(base64);
            existing.setProfilePictureType(imageFile.getContentType());
        }

        pharmacistService.updatePharmacist(existing);
        return "redirect:/pharmacist";
    }



    //AddMedicine//
    @PostMapping("/addMedicine")
public String addMedicine(@ModelAttribute("newMedicine") Medicine newMedicine, RedirectAttributes redirectAttributes) {
    try {
        if (medicineService.isMedicineNameExists(newMedicine.getMedName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Medicine with the same name already exists.");
            return "redirect:/pharmacist/viewMedicineList";
        }

        medicineService.createMedicine(newMedicine);
        redirectAttributes.addFlashAttribute("successMessage", "Medicine added successfully.");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Error adding medicine.");
    }
    return "redirect:/pharmacist/viewMedicineList";
}


//AddMedicineForm//
@GetMapping("/addMedicineForm")
public String showAddMedicineForm(Model model) {
    model.addAttribute("newMedicine", new Medicine());
    return "addMedicineForm";
}


//ViewMedicineList//
@GetMapping("/viewMedicineList")
public String viewMedicineList(Model model,
                               @RequestParam(defaultValue = "0") int pageNo,
                               @RequestParam(defaultValue = "5") int pageSize,
                               @RequestParam(defaultValue = "") String searchQuery) throws ExecutionException, InterruptedException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
    Pharmacist pharmacist = pharmacistService.getPharmacist(myUserDetails.getUsername());
    
    List<Medicine> allMedicine = medicineService.getAllMedicines();

    if (!searchQuery.isEmpty()) {
        allMedicine = allMedicine.stream()
            .filter(p -> p.getMedName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                         p.getMedId().toLowerCase().contains(searchQuery))
            .collect(Collectors.toList());
    }

    int total = allMedicine.size();
    int start = Math.min(pageNo * pageSize, total);
    int end = Math.min((pageNo + 1) * pageSize, total);
    int startIndex = pageNo * pageSize;

    List<Medicine> medicineList = allMedicine.subList(start, end);

    model.addAttribute("startIndex", startIndex);
    model.addAttribute("currentPage", pageNo);
    model.addAttribute("totalPages", (total + pageSize - 1) / pageSize);
    model.addAttribute("pageSize", pageSize);
    model.addAttribute("medicineList", medicineList);
    model.addAttribute("searchQuery", searchQuery);
    
    // ✅ Add this:
    model.addAttribute("pharmacist", pharmacist);

    return "viewMedicineList";
}


//EditMedicine//
@GetMapping("/editMedicine/{medId}")
public String editMedicine(@PathVariable String medId, Model model) throws ExecutionException, InterruptedException {
    Medicine medicine = medicineService.getMedicineById(medId);
    model.addAttribute("medicine", medicine);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
    Pharmacist pharmacist = pharmacistService.getPharmacist(userDetails.getUsername());
    model.addAttribute("pharmacist", pharmacist); // ✅ Add this

    return "editMedicine";
}



//updateMedicine//
@PostMapping("/updateMedicine/{medId}")
public String updateMedicine(@PathVariable String medId, @ModelAttribute Medicine updatedMedicine, RedirectAttributes redirectAttributes) {
    updatedMedicine.setMedId(medId);
    String message = medicineService.updateMedicine(updatedMedicine);
    redirectAttributes.addFlashAttribute("successMessage", message);
    return "redirect:/pharmacist/viewMedicineList";
}


//DeleteMedicine//
@PostMapping("/deleteMedicine/{medId}")
public String deleteMedicine(@PathVariable String medId, RedirectAttributes redirectAttributes) {
    String message = medicineService.deleteMedicine(medId);
    redirectAttributes.addFlashAttribute("successMessage", message);
    return "redirect:/pharmacist/viewMedicineList";
}

//AddStock//
@PostMapping("/addStock")
public String addStock(@RequestParam String medId, @RequestParam int quantity, RedirectAttributes redirectAttributes) {
    String message = medicineService.addStock(medId, quantity);
    redirectAttributes.addFlashAttribute("successMessage", message);
    return "redirect:/pharmacist/viewMedicineList";
}


//PatientMedicineList//
@GetMapping("/patient-medicine-list")
public String viewPatientMedicineList(
        @RequestParam(defaultValue = "0") int pageNo,
        @RequestParam(defaultValue = "5") int pageSize,
        @RequestParam(defaultValue = "") String searchQuery,
        Model model) throws Exception {

    // 1. Fetch all patients
    List<Patient> allPatients = patientService.getAllPatients();

    // 2. Filter by search query
    if (!searchQuery.isEmpty()) {
        allPatients = allPatients.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());
    }

    // 3. Paginate by patient list
    int totalPatients = allPatients.size();
    int start = Math.min(pageNo * pageSize, totalPatients);
    int end = Math.min(start + pageSize, totalPatients);
    List<Patient> pagedPatients = allPatients.subList(start, end);

    // 4. Get prescriptions for only these patients
    Map<Patient, List<Prescription>> patientPrescriptions = new LinkedHashMap<>();
    for (Patient patient : pagedPatients) {
        Map<String, Prescription> prescriptionMap = patient.getPrescription();
        if (prescriptionMap != null && !prescriptionMap.isEmpty()) {
            List<Prescription> prescriptions = new ArrayList<>(prescriptionMap.values());
            prescriptions.sort(Comparator.comparing(Prescription::getTimestamp).reversed());
            patientPrescriptions.put(patient, prescriptions);
        }
    }

    // 5. Send to Thymeleaf
    model.addAttribute("patientPrescriptions", patientPrescriptions);
    model.addAttribute("currentPage", pageNo);
    model.addAttribute("totalPages", (int) Math.ceil((double) totalPatients / pageSize));
    model.addAttribute("searchQuery", searchQuery);

    return "pharmacistPatientMedicineList";
}






}


    