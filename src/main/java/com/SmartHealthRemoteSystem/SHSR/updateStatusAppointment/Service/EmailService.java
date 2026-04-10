package com.SmartHealthRemoteSystem.SHSR.updateStatusAppointment.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String emailPassword;

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, emailPassword);
            }
        });
    }

    public void sendAppointmentStatusEmail(String toEmail, String appointmentId, String status) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            if (status.equals("Approved")) {
                message.setSubject("Appointment Approved - ID: " + appointmentId);
                message.setText("Dear Patient,\n\n" +
                        "Your appointment (ID: " + appointmentId + ") has been approved. " +
                        "Please arrive 15 minutes before your scheduled time.\n\n" +
                        "Best regards,\nYour Healthcare Team");
            } else if (status.equals("Cancelled")) {
                message.setSubject("Appointment Cancelled - ID: " + appointmentId);
                message.setText("Dear Patient,\n\n" +
                        "Your appointment (ID: " + appointmentId + ") has been cancelled. " +
                        "Please contact us if you would like to reschedule.\n\n" +
                        "Best regards,\nYour Healthcare Team");
            }

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email notification: " + e.getMessage());
        }
    }

    public void sendAppointmentUpdateEmail(String patientEmail, String appointmentId, String newDate, String newTime) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(patientEmail));
            message.setSubject("Appointment Date/Time Updated");
            message.setText(String.format(
                "Dear Patient,\n\nYour appointment (ID: %s) has been rescheduled to %s at %s. " +
                "Please contact the hospital if this time doesn't work for you.\n\nBest regards,\nYour Healthcare Team",
                appointmentId, newDate, newTime
            ));

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email notification: " + e.getMessage());
        }
    }
}