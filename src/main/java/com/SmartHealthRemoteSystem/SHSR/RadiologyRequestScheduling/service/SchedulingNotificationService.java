package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.Notification;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.NotificationChannel;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.NotificationRepository;
import com.SmartHealthRemoteSystem.SHSR.Service.MailService;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import com.SmartHealthRemoteSystem.SHSR.User.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * UCR012 — Delivers in-app and email notifications for scheduling events.
 * Every notification is persisted to MongoDB regardless of email success.
 */
@Service
public class SchedulingNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingNotificationService.class);

    private final NotificationRepository notificationRepository;
    private final MailService            mailService;
    private final UserRepository         userRepository;

    @Autowired
    public SchedulingNotificationService(NotificationRepository notificationRepository,
                                         MailService mailService,
                                         UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.mailService            = mailService;
        this.userRepository         = userRepository;
    }

    public void notifyRequestSubmitted(String patientId, String doctorId, String requestId,
                                        int priorityScore, String urgencyLevel) {
        saveAndSend(patientId,
                "Your imaging request " + requestId + " has been submitted and is awaiting review.");

        // Confirms the submission to the doctor who made it.
        saveAndSend(doctorId,
                "Imaging request " + requestId + " has been submitted successfully and is awaiting review.");

        // UCR012 — radiology staff need to know a new request is awaiting review,
        // since nobody is assigned to it yet (no radiologistId until approval).
        // Includes the AI priority so staff can triage without opening the request.
        notifyAllRadiologyStaff(
                "New imaging request " + requestId + " (" + urgencyLevel + ", AI Priority "
                        + priorityScore + "/100) has been submitted and is awaiting review.",
                null);

        // Radiologists specifically get a distinct urgent-case alert for
        // URGENT/EMERGENCY submissions, separate from the routine broadcast above.
        if ("URGENT".equals(urgencyLevel) || "EMERGENCY".equals(urgencyLevel)) {
            notifyUsersWithRole("RADIOLOGIST",
                    "Urgent case: imaging request " + requestId + " (" + urgencyLevel
                            + ") requires prompt review.",
                    null);
        }
    }

    public void notifyRequestApproved(String doctorId, String patientId, String requestId, String approvedByUserId) {
        String msg = "Imaging request " + requestId + " has been APPROVED and will be scheduled shortly.";
        saveAndSend(doctorId, msg);
        saveAndSend(patientId, msg);

        // UCR012 — let the rest of radiology staff know it's ready for scheduling.
        // The staff member who approved it already knows, so they're excluded.
        notifyAllRadiologyStaff(
                "Imaging request " + requestId + " has been approved and is ready for scheduling.",
                approvedByUserId);
    }

    public void notifyRequestRejected(String doctorId, String patientId, String requestId, String reason) {
        String msg = "Imaging request " + requestId + " has been REJECTED. Reason: " + reason;
        saveAndSend(doctorId, msg);
        saveAndSend(patientId, msg);
    }

    public void notifyRequestCancelled(String requestId, String doctorId, String cancelledByUserId) {
        String msg = "Imaging Request " + requestId + " has been cancelled.";

        // Doctors can cancel their own PENDING requests — don't notify them
        // about an action they just took themselves.
        if (cancelledByUserId == null || !cancelledByUserId.equals(doctorId)) {
            saveAndSend(doctorId, msg);
        }

        // UCR012 — broadcast to radiology staff so anyone who might've been about
        // to act on it knows. The staff member who cancelled it is excluded.
        notifyAllRadiologyStaff(msg, cancelledByUserId);
    }

    public void notifyAppointmentScheduled(String patientId, String doctorId, String requestId,
                                            String appointmentId, String appointmentDate, String timeSlot,
                                            String scheduledByRadiographerId) {
        String msg = "Appointment " + appointmentId + " scheduled on " + appointmentDate + " at " + timeSlot + ".";
        saveAndSend(patientId, msg);
        saveAndSend(doctorId, msg);

        // UCR012 — let the rest of radiology staff know. The radiographer who
        // booked it already knows, so they're excluded.
        notifyAllRadiologyStaff(
                "Imaging request " + requestId + " — appointment scheduled for "
                        + appointmentDate + " at " + timeSlot + ".",
                scheduledByRadiographerId);
    }

    public void notifyAppointmentCancelled(String patientId, String doctorId, String appointmentId) {
        String msg = "Appointment " + appointmentId + " has been cancelled.";
        saveAndSend(patientId, msg);
        saveAndSend(doctorId, msg);
    }

    public void notifyAppointmentCompleted(String patientId, String doctorId, String requestId, String appointmentId) {
        saveAndSend(patientId,
                "Appointment " + appointmentId + " has been completed. Your imaging results will be available shortly.");
        saveAndSend(doctorId,
                "Appointment " + appointmentId + " has been completed. Your imaging results will be available shortly.");

        // UCR012 — radiologists need to know imaging is ready for their review.
        notifyUsersWithRole("RADIOLOGIST",
                "Imaging request " + requestId + " is ready for review — images have been uploaded.",
                null);
    }

    // UCR012 — fires when a radiologist finalizes a report (RadiologyReport module).
    // Combines "Report Available" + "Request Completed" for the doctor into one
    // message since both fire at the exact same moment (report finalized = the
    // whole request lifecycle is done) — avoids sending two near-identical pings.
    public void notifyReportFinalized(String doctorId, String radiologistId, String requestId, String reportId) {
        saveAndSend(doctorId,
                "Your radiology report for imaging request " + requestId + " is now available (Report ID: "
                        + reportId + "). This request is now complete.");

        saveAndSend(radiologistId,
                "Your report " + reportId + " for imaging request " + requestId
                        + " has been submitted and finalized successfully.");
    }

    // UCR012 — fires when a participant sends a message in a chat session
    // (Communication module). Resolves the sender's display name internally
    // so callers only need to pass IDs and the raw message content.
    public void notifyMessageReceived(String recipientId, String senderId, String content) {
        if (recipientId == null || recipientId.equals(senderId)) return;

        User sender = userRepository.get(senderId);
        String senderName = (sender != null && sender.getName() != null) ? sender.getName() : senderId;

        String preview = content;
        if (preview != null && preview.length() > 60) {
            preview = preview.substring(0, 60) + "...";
        }

        saveAndSend(recipientId, "New message from " + senderName
                + (preview != null && !preview.isBlank() ? ": " + preview : "."));
    }

    private void saveAndSend(String userId, String message) {
        if (userId == null) return;

        Notification notification = Notification.builder()
                .notificationId(UUID.randomUUID().toString())
                .userId(userId)
                .message(message)
                .channel(NotificationChannel.SYSTEM)
                .timestamp(new Date())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        User user = userRepository.get(userId);
        if (user != null && user.getEmail() != null) {
            try {
                mailService.sendMail(user.getEmail(), "WellCheck Radiology Notification", message);
            } catch (Exception e) {
                logger.warn("Email notification failed for user {}: {}", userId, e.getMessage());
            }
        }
    }

    // UCR012 — broadcasts to every active RADIOLOGIST and RADIOGRAPHER, optionally
    // skipping the staff member who triggered the event so they don't get a
    // notification about their own action.
    private void notifyAllRadiologyStaff(String message, String excludeUserId) {
        notifyUsersWithRole("RADIOLOGIST",  message, excludeUserId);
        notifyUsersWithRole("RADIOGRAPHER", message, excludeUserId);
    }

    private void notifyUsersWithRole(String role, String message, String excludeUserId) {
        List<User> staff = userRepository.getAllByRole(role);
        for (User u : staff) {
            if (excludeUserId != null && excludeUserId.equals(u.getUserId())) continue;
            saveAndSend(u.getUserId(), message);
        }
    }
}