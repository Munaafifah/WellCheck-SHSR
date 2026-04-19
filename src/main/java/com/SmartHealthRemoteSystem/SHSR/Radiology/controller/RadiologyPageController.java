package com.SmartHealthRemoteSystem.SHSR.Radiology.controller;

import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/radiology")
public class RadiologyPageController {

    @GetMapping
    public String getRadiologyPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();

        String userId   = userDetails.getUser().getUserId();
        String userName = userDetails.getUser().getName();
        String role     = userDetails.getUser().getRole();

        String dashboardUrl;
        switch (role) {
            case "DOCTOR":           dashboardUrl = "/doctor";       break;
            case "RADIOGRAPHER":     dashboardUrl = "/radiographer"; break;
            case "RADIOLOGIST":      dashboardUrl = "/radiologist";  break;
            case "ADMIN":            dashboardUrl = "/admin";        break;
            default:                 dashboardUrl = "/";             break;
        }

        model.addAttribute("userId",       userId);
        model.addAttribute("userName",     userName);
        model.addAttribute("userRole",     role);
        model.addAttribute("dashboardUrl", dashboardUrl);
        return "radiology";
    }
}
