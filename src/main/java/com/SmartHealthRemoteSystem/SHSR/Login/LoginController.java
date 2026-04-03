package com.SmartHealthRemoteSystem.SHSR.Login;

// import com.SmartHealthRemoteSystem.SHSR.Service.DoctorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    @GetMapping("/")
    public String index(){
        return "login";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

}

