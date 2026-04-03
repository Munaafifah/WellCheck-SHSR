// // package com.SmartHealthRemoteSystem.SHSR.Medicine;

// // import com.SmartHealthRemoteSystem.SHSR.Service.MedicineService;
// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.stereotype.Controller;
// // import org.springframework.ui.Model;
// // import org.springframework.web.bind.annotation.*;

// // import java.util.List;
// // import java.util.concurrent.ExecutionException;

// // @Controller
// // public class MedicineController {

// //     private final MedicineService medicineService;

// //     @Autowired
// //     public MedicineController(MedicineService medicineService) {
// //         this.medicineService = medicineService;
// //     }

// //     @GetMapping("/edit/{medId}")
// //     public String editMedicine(@PathVariable String medId, Model model) throws ExecutionException, InterruptedException {
// //         Medicine medicine = medicineService.getMedicine(medId);
// //         model.addAttribute("medicine", medicine);
// //         return "editMedicine"; 
// //     }

// //     @PostMapping("/edit/{medId}")
// //     public String updateMedicine(@PathVariable String medId, @ModelAttribute Medicine updatedMedicine) throws ExecutionException, InterruptedException {
// //         updatedMedicine.setMedId(medId);
// //         medicineService.updateMedicine(updatedMedicine);
// //         return "redirect:/pharmacist/medicines";
// //     }

// //     @GetMapping("/delete/{medId}")
// //     public String deleteMedicine(@PathVariable String medId) throws ExecutionException, InterruptedException {
// //         medicineService.deleteMedicine(medId);
// //         return "redirect:/pharmacist/medicines";
// //     }

// //     @PostMapping("/add")
// //     public String addNewMedicine(@ModelAttribute Medicine newMedicine) throws ExecutionException, InterruptedException {
// //         medicineService.createMedicine(newMedicine);
// //         return "redirect:/pharmacist/viewMedicineList";
// //     }
// // }

// //MONGODB//
// package com.SmartHealthRemoteSystem.SHSR.Medicine;


// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import com.SmartHealthRemoteSystem.SHSR.Service.MedicineService;

// import java.util.List;

// @Controller
// @RequestMapping("/medicine")
// public class MedicineController {

//     private final MedicineService medicineService;

//     @Autowired
//     public MedicineController(MedicineService medicineService) {
//         this.medicineService = medicineService;
//     }

//     // View all medicines (optionally used in another page)
//     @GetMapping("/list")
//     public String listMedicines(Model model) {
//         List<Medicine> medicineList = medicineService.getAllMedicines();
//         model.addAttribute("medicineList", medicineList);
//         return "viewMedicineList"; // Update this if you use a different view name
//     }

//     // Show form to edit medicine
//     @GetMapping("/edit/{medId}")
//     public String editMedicine(@PathVariable String medId, Model model) {
//         Medicine medicine = medicineService.getMedicineById(medId);
//         model.addAttribute("medicine", medicine);
//         return "editMedicine";
//     }

//     // Submit edited medicine
//     @PostMapping("/edit/{medId}")
//     public String updateMedicine(@PathVariable String medId, @ModelAttribute Medicine updatedMedicine) {
//         updatedMedicine.setMedId(medId);
//         medicineService.updateMedicine(updatedMedicine);
//         return "redirect:/pharmacist";
//     }

//     // Delete a medicine
//     @GetMapping("/delete/{medId}")
//     public String deleteMedicine(@PathVariable String medId) {
//         medicineService.deleteMedicine(medId);
//         return "redirect:/pharmacist";
//     }

//     @GetMapping("/add")
// public String showAddForm(Model model) {
//     model.addAttribute("newMedicine", new Medicine());
//     return "addMedicineForm";
// }

// }

 