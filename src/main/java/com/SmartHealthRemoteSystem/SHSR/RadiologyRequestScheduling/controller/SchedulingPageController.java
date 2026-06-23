package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.controller;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.AppointmentResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.ImagingRequestResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.dto.NotificationResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.AppointmentStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestStatus;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository.NotificationRepository;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service.AppointmentService;
import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service.ImagingRequestService;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.MongoPatientRepository;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.WebConfiguration.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Thymeleaf page controller for the RadiologyRequestScheduling module.
 * Serves all HTML views; data operations go through the REST controllers.
 */
@Controller
public class SchedulingPageController {

    private final ImagingRequestService  requestService;
    private final AppointmentService     appointmentService;
    private final NotificationRepository notificationRepository;
    private final MongoPatientRepository mongoPatientRepository;

    @Autowired
    public SchedulingPageController(ImagingRequestService requestService,
                                    AppointmentService appointmentService,
                                    NotificationRepository notificationRepository,
                                    MongoPatientRepository mongoPatientRepository) {
        this.requestService         = requestService;
        this.appointmentService     = appointmentService;
        this.notificationRepository = notificationRepository;
        this.mongoPatientRepository = mongoPatientRepository;
    }

    // UCR008 — Doctor submits a new imaging request
    @GetMapping("/imaging-request-form")
    public String imagingRequestFormPage(Model model) {
        populateModel(model);
        model.addAttribute("patients",      mongoPatientRepository.findAll());
        model.addAttribute("modalities",    Arrays.asList("XRAY", "CT", "MRI", "ULTRASOUND"));
        model.addAttribute("urgencyLevels", Arrays.asList("ROUTINE", "URGENT", "EMERGENCY"));
        return "imagingRequestForm";
    }

    // UCR009 — Radiologist / Radiographer manages (approve / reject) requests
    @GetMapping("/manage-requests")
    public String manageRequestsPage(Model model) {
        populateModel(model);
        List<ImagingRequestResponseDTO> pending = requestService.getPendingByPriority();
        List<ImagingRequestResponseDTO> all     = requestService.getAllRequests();

        model.addAttribute("pendingRequests",   pending);
        model.addAttribute("allRequests",       all);

        // Status summary counts for the stats banner.
        // SCHEDULED bucket also covers IN_PROGRESS (booked, imaging not yet done).
        // COMPLETED bucket also covers REPORT_PENDING / REPORT_READY (imaging is
        // done; the legacy COMPLETED status itself is never actually set by the
        // appointment-completion flow, which moves straight to REPORT_PENDING).
        model.addAttribute("pendingCount",   all.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count());
        model.addAttribute("approvedCount",  all.stream().filter(r -> r.getStatus() == RequestStatus.APPROVED).count());
        model.addAttribute("rejectedCount",  all.stream().filter(r -> r.getStatus() == RequestStatus.REJECTED).count());
        model.addAttribute("scheduledCount", all.stream().filter(r -> isScheduledOrInProgress(r.getStatus())).count());
        model.addAttribute("completedCount", all.stream().filter(r -> isImagingCompleted(r.getStatus())).count());

        return "manageRequests";
    }

    // UCR010 — Radiographer schedules an appointment for an APPROVED request
    @GetMapping("/appointment-scheduling")
    public String appointmentSchedulingPage(Model model) {
        populateModel(model);
        List<ImagingRequestResponseDTO> approved = requestService.getAllRequests().stream()
                .filter(r -> r.getStatus() == RequestStatus.APPROVED)
                .collect(Collectors.toList());

        List<AppointmentResponseDTO> scheduled = appointmentService.getAllAppointments().stream()
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
                .collect(Collectors.toList());

        model.addAttribute("approvedRequests",      approved);
        model.addAttribute("scheduledAppointments", scheduled);
        model.addAttribute("timeSlots", Arrays.asList(
                "08:00-09:00", "09:00-10:00", "10:00-11:00",
                "11:00-12:00", "13:00-14:00", "14:00-15:00",
                "15:00-16:00", "16:00-17:00"));
        return "appointmentScheduling";
    }

    // UCR013 — Admin / Doctor tracks request and appointment status
    @GetMapping("/request-status")
    public String requestStatusPage(Model model) {
        populateModel(model);
        String userId = currentUserId();
        String role   = currentRole();

        List<ImagingRequestResponseDTO> requests;
        if ("DOCTOR".equals(role)) {
            // Doctor sees only their own submitted requests
            requests = requestService.getRequestsByDoctor(userId);
        } else {
            // ADMIN sees all requests
            requests = requestService.getAllRequests();
        }

        // Build a map of requestId → appointment for efficient template lookup
        Map<String, AppointmentResponseDTO> appointmentMap =
                appointmentService.getAllAppointments().stream()
                        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                        .collect(Collectors.toMap(
                                AppointmentResponseDTO::getRequestId,
                                a -> a,
                                (a1, a2) -> a1));

        model.addAttribute("requests",        requests);
        model.addAttribute("appointmentMap",  appointmentMap);
        return "requestStatus";
    }

    // UCR012 — All roles view their own notifications
    @GetMapping("/notifications")
    public String notificationCenterPage(Model model) {
        populateModel(model);
        String userId = currentUserId();

        List<NotificationResponseDTO> notifications =
                notificationRepository.findByUserIdOrderByTimestampDesc(userId).stream()
                        .map(NotificationResponseDTO::fromEntity)
                        .collect(Collectors.toList());

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount",   unreadCount);
        return "notificationCenter";
    }

    // UCR008+UCR013 (Doctor) — Unified request submission and status tracking page
    @GetMapping("/request-scheduling")
    public String requestSchedulingPage(Model model) {
        populateModel(model);
        String doctorId = currentUserId();

        List<Patient> patientList = mongoPatientRepository.findAll();

        List<ImagingRequestResponseDTO> myRequests = requestService.getRequestsByDoctor(doctorId)
                .stream()
                .sorted((a, b) -> {
                    if (b.getUpdatedDate() == null) return -1;
                    if (a.getUpdatedDate() == null) return 1;
                    return b.getUpdatedDate().compareTo(a.getUpdatedDate());
                })
                .collect(Collectors.toList());

        Map<String, AppointmentResponseDTO> appointmentMap =
                appointmentService.getAllAppointments().stream()
                        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                        .collect(Collectors.toMap(
                                AppointmentResponseDTO::getRequestId,
                                a -> a,
                                (a1, a2) -> a1));

        List<NotificationResponseDTO> recentNotifications =
                notificationRepository.findByUserIdOrderByTimestampDesc(doctorId).stream()
                        .limit(10)
                        .map(NotificationResponseDTO::fromEntity)
                        .collect(Collectors.toList());

        Map<String, String> patientNameMap = patientList.stream()
                .collect(Collectors.toMap(Patient::getUserId, Patient::getName, (a, b) -> a));

        model.addAttribute("myRequests",          myRequests);
        model.addAttribute("appointmentMap",      appointmentMap);
        model.addAttribute("recentNotifications", recentNotifications);
        model.addAttribute("patients",            patientList);
        model.addAttribute("patientNameMap",      patientNameMap);
        model.addAttribute("modalities",          Arrays.asList("XRAY", "CT", "MRI", "ULTRASOUND"));
        model.addAttribute("urgencyLevels",       Arrays.asList("ROUTINE", "URGENT", "EMERGENCY"));
        model.addAttribute("pendingCount",
                myRequests.stream().filter(r -> r.getStatus() == RequestStatus.PENDING).count());
        model.addAttribute("approvedCount",
                myRequests.stream().filter(r -> r.getStatus() == RequestStatus.APPROVED).count());
        model.addAttribute("scheduledCount",
                myRequests.stream().filter(r -> isScheduledOrInProgress(r.getStatus())).count());
        model.addAttribute("completedCount",
                myRequests.stream().filter(r -> isImagingCompleted(r.getStatus())).count());
        model.addAttribute("rejectedCount",
                myRequests.stream().filter(r -> r.getStatus() == RequestStatus.REJECTED).count());
        model.addAttribute("cancelledCount",
                myRequests.stream().filter(r -> r.getStatus() == RequestStatus.CANCELLED).count());

        return "requestScheduling";
    }

    // ---- Helpers ----

    private void populateModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();

        String userId   = userDetails.getUser().getUserId();
        String userName = userDetails.getUser().getName();
        String role     = userDetails.getUser().getRole();

        String dashboardUrl;
        switch (role) {
            case "DOCTOR":       dashboardUrl = "/doctor";       break;
            case "RADIOGRAPHER": dashboardUrl = "/radiographer"; break;
            case "RADIOLOGIST":  dashboardUrl = "/radiologist";  break;
            case "PATIENT":      dashboardUrl = "/patient";      break;
            case "ADMIN":        dashboardUrl = "/admin";        break;
            default:             dashboardUrl = "/";             break;
        }

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);

        model.addAttribute("userId",       userId);
        model.addAttribute("userName",     userName);
        model.addAttribute("userRole",     role);
        model.addAttribute("dashboardUrl", dashboardUrl);
        model.addAttribute("unreadCount",  unreadCount);
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((MyUserDetails) auth.getPrincipal()).getUser().getUserId();
    }

    private String currentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((MyUserDetails) auth.getPrincipal()).getUser().getRole();
    }

    // Stats-banner bucketing — covers all 9 RequestStatus values exactly once.
    // "Scheduled" includes IN_PROGRESS (appointment booked, imaging not yet done).
    // "Completed" includes REPORT_PENDING / REPORT_READY (imaging is finished;
    // the legacy COMPLETED status is kept for forward-compatibility but is never
    // actually set by the current appointment-completion flow).
    private boolean isScheduledOrInProgress(RequestStatus status) {
        return status == RequestStatus.SCHEDULED || status == RequestStatus.IN_PROGRESS;
    }

    private boolean isImagingCompleted(RequestStatus status) {
        return status == RequestStatus.COMPLETED
            || status == RequestStatus.REPORT_PENDING
            || status == RequestStatus.REPORT_READY;
    }
}