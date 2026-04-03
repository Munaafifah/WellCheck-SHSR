package com.SmartHealthRemoteSystem.SHSR.Service;

import com.SmartHealthRemoteSystem.SHSR.Mail.MailStructure;
import com.SmartHealthRemoteSystem.SHSR.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Autowired
    public MailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public void sendNewUserMail(String mail, MailStructure mailStructure) {
        try {
            String subject = "Welcome to WellCheck System";
            String message = "Your temporary password: " + mailStructure.getPassword() +
                             "\nPlease log in and change your password.";

            sendMail(mail, subject, message);
            logger.info("✅ Welcome email sent to {}", mail);
        } catch (MailException e) {
            logger.error("❌ Failed to send welcome email to {}: {}", mail, e.getMessage(), e);
        }
    }

    public void sendMail(String mail, String subject, String message) {
    try {
        System.out.println("📤 Attempting to send email to: " + mail);  // ✅ ADD THIS
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromMail);
        email.setTo(mail);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        logger.info("Email sent to {}", mail);
    } catch (MailException e) {
        logger.error("Email send failed to {}: {}", mail, e.getMessage());
    }
}


    public void sendAssignedMail(String mail, String subject, String message) {
        sendMail(mail, subject, message);
    }

    public void sendUnassignedMail(String mail, String subject, String message) {
        sendMail(mail, subject, message);
    }

    public void sendReleasedMail(String mail, String subject, String message) {
        sendMail(mail, subject, message);
    }
}
