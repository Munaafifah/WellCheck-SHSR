// package com.SmartHealthRemoteSystem.SHSR.Mail;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;

// import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
// import com.SmartHealthRemoteSystem.SHSR.User.UserRepository;

// import org.springframework.web.bind.annotation.RequestMapping;


// @RestController
// @RequestMapping("/mail")
// public class MailController {
//   @Autowired
//     private MailService mailService;

//     @Autowired
//     private UserRepository userRepository; // Assuming you have a UserRepository

//     @PostMapping("/send/{mail}")
//     public String sendMail(@PathVariable String mail, @RequestBody MailStructure mailStructure) {
//         // Check if the email already exists
//         if (emailExists(mail)) {
//             return "Error: Email " + mail + " already exists. Email not sent.";
//         }

//         // If email doesn't exist, send the email
//         mailService.sendNewUserMail(mail, mailStructure);
//         return "Successfully sent the mail";
//     }

//     // Method to check if email already exists in the user repository
//     private boolean emailExists(String email) {
//         return userRepository.existsByEmail(email);
//     }
// }

package com.SmartHealthRemoteSystem.SHSR.Mail;

import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
import com.SmartHealthRemoteSystem.SHSR.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send/{mail}")
    public String sendMail(@PathVariable String mail, @RequestBody MailStructure mailStructure) {
        if (emailExists(mail)) {
            return "Error: Email " + mail + " already exists. Mail not sent.";
        }

        mailService.sendNewUserMail(mail, mailStructure);
        return "Mail sent successfully to " + mail;
    }

    private boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}

