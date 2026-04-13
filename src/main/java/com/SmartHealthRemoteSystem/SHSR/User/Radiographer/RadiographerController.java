package com.SmartHealthRemoteSystem.SHSR.User.Radiographer;

import com.SmartHealthRemoteSystem.SHSR.Service.RadiographerService;
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
@RequestMapping("/radiographer")
public class RadiographerController {

    @Autowired
    private RadiographerService radiographerService;

    @GetMapping
    public String getDashboard(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Radiographer radiographer = radiographerService.getRadiographer(userDetails.getUsername());
        model.addAttribute("radiographer", radiographer);
        return "radiographerDashBoard";
    }

    @GetMapping("/updateProfile")
    public String showEditProfile(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
        Radiographer radiographer = radiographerService.getRadiographer(userDetails.getUsername());
        model.addAttribute("radiographer", radiographer);
        return "editProfileRadiographer";
    }

    @PostMapping("/updateProfile/profile")
    public String saveUpdatedProfile(@ModelAttribute Radiographer updated,
            @RequestParam("imageFile") MultipartFile imageFile) throws Exception {
        Radiographer existing = radiographerService.getRadiographer(updated.getUserId());
        existing.setName(updated.getName());
        existing.setContact(updated.getContact());
        existing.setDepartment(updated.getDepartment());
        existing.setPosition(updated.getPosition());

        if (!imageFile.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(imageFile.getBytes());
            existing.setProfilePicture(base64);
            existing.setProfilePictureType(imageFile.getContentType());
        }

        radiographerService.updateRadiographer(existing);
        return "redirect:/radiographer";
    }
}
