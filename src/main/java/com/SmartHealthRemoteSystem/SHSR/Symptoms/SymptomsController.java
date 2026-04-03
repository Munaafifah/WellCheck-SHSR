// package com.SmartHealthRemoteSystem.SHSR.Symptoms;
// import com.SmartHealthRemoteSystem.SHSR.Service.SymptomsService;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;

// import java.util.List;
// import java.util.concurrent.ExecutionException;

// @Controller
// public class SymptomsController {

//     @Autowired
//     private SymptomsService symptomsService;

//     @GetMapping("/symptoms")
//     public String getSymptomsPage(Model model) throws ExecutionException, InterruptedException {
//         List<Symptoms> symptomsList = symptomsService.getAllSymptoms();
//         model.addAttribute("symptomsList", symptomsList);
//         return "symptoms"; 
//     }
// }


//MongoDB//
package com.SmartHealthRemoteSystem.SHSR.Symptoms;

import com.SmartHealthRemoteSystem.SHSR.Service.SymptomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SymptomsController {

    @Autowired
    private SymptomsService symptomsService;

    @GetMapping("/symptoms")
    public String getSymptomsPage(Model model) {
        List<Symptom> symptomsList = symptomsService.getAllSymptoms();
        model.addAttribute("symptomsList", symptomsList);
        return "symptoms"; // Name of the Thymeleaf view
    }

    @PostMapping("/symptoms")
    public String saveSymptom(@ModelAttribute Symptom symptom) {
        symptomsService.saveSymptom(symptom);
        return "redirect:/symptoms";
    }

    @GetMapping("/symptoms/edit/{id}")
    public String editSymptom(@PathVariable String id, Model model) {
        Symptom symptom = symptomsService.getSymptomById(id);
        model.addAttribute("symptom", symptom);
        return "edit_symptom";
    }

    @PostMapping("/symptoms/update")
    public String updateSymptom(@ModelAttribute Symptom symptom) {
        symptomsService.updateSymptom(symptom);
        return "redirect:/symptoms";
    }

    @GetMapping("/symptoms/delete/{id}")
    public String deleteSymptom(@PathVariable String id) {
        symptomsService.deleteSymptom(id);
        return "redirect:/symptoms";
    }
}



