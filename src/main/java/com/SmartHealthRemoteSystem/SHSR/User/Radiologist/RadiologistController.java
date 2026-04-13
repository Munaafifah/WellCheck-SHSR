package com.SmartHealthRemoteSystem.SHSR.User.Radiologist;

import com.SmartHealthRemoteSystem.SHSR.Service.RadiologistService;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/radiologist")
public class RadiologistController {

    @Autowired
    private RadiologistService radiologistService;

    @GetMapping
    public String getDashboard(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Radiologist radiologist = radiologistService.getRadiologist(userDetails.getUsername());
        model.addAttribute("radiologist", radiologist);
        return "radiologistDashBoard";
    }

    @GetMapping("/updateProfile")
    public String showEditProfile(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Radiologist radiologist = radiologistService.getRadiologist(userDetails.getUsername());
        model.addAttribute("radiologist", radiologist);
        return "editProfileRadiologist";
    }

    @PostMapping("/updateProfile/profile")
    public String saveUpdatedProfile(@ModelAttribute Radiologist updated,
            @RequestParam("imageFile") MultipartFile imageFile) throws Exception {
        Radiologist existing = radiologistService.getRadiologist(updated.getUserId());
        existing.setName(updated.getName());
        existing.setContact(updated.getContact());
        existing.setDepartment(updated.getDepartment());
        existing.setSpecialization(updated.getSpecialization());

        if (!imageFile.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
            existing.setProfilePicture(base64);
            existing.setProfilePictureType(imageFile.getContentType());
        }

        radiologistService.updateRadiologist(existing);
        return "redirect:/radiologist";
    }
}
