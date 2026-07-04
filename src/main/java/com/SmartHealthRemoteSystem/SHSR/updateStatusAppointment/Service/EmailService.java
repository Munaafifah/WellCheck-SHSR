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

    // ── 1. Booking confirmation ──────────────────────────────────────
    public void sendAppointmentBookedEmail(String toEmail, String date, String time, String hospital, String typeOfSickness) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Appointment Confirmed");
            message.setText("Dear Patient,\n\n"
                    + "Your appointment has been booked and confirmed at " + hospital + " for " + date + " at " + time + ".\n"
                    + "Type of Appointment: " + typeOfSickness + "\n\n"
                    + "Please arrive 15 minutes before your scheduled time.\n\n"
                    + "Best regards,\nYour Healthcare Team");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking confirmation email: " + e.getMessage());
        }
    }

    // ── 2. Approve / Cancel status update ────────────────────────────
    public void sendAppointmentStatusEmail(String toEmail, String status, String date, String time, String hospital, String typeOfSickness) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));

            if (status.equals("Approved")) {
                message.setSubject("Appointment Approved");
                message.setText("Dear Patient,\n\n"
                        + "Your appointment at " + hospital + " on " + date + " at " + time + " has been approved.\n"
                        + "Type of Appointment: " + typeOfSickness + "\n\n"
                        + "Please arrive 15 minutes before your scheduled time.\n\n"
                        + "Best regards,\nYour Healthcare Team");
            } else if (status.equals("Cancelled")) {
                message.setSubject("Appointment Cancelled");
                message.setText("Dear Patient,\n\n"
                        + "Your appointment at " + hospital + " on " + date + " at " + time + " has been cancelled.\n"
                        + "Type of Appointment: " + typeOfSickness + "\n\n"
                        + "Please contact us if you would like to reschedule.\n\n"
                        + "Best regards,\nYour Healthcare Team");
            }

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email notification: " + e.getMessage());
        }
    }

    // ── 3. Reschedule notice ──────────────────────────────────────────
    public void sendAppointmentUpdateEmail(String toEmail, String newDate, String newTime, String hospital, String typeOfSickness) {
        try {
            Message message = new MimeMessage(createSession());
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Appointment Date/Time Updated");
            message.setText("Dear Patient,\n\n"
                    + "Your appointment at " + hospital + " has been rescheduled to " + newDate + " at " + newTime + ".\n"
                    + "Type of Appointment: " + typeOfSickness + "\n\n"
                    + "Please contact the hospital if this time doesn't work for you.\n\n"
                    + "Best regards,\nYour Healthcare Team");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email notification: " + e.getMessage());
        }
    }
}