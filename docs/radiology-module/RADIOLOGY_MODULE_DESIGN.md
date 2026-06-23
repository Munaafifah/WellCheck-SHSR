---
# WellCheck Radiance — Radiology Request & Scheduling Module
## Complete Redesign: Architecture, Database, API, Business Rules & Implementation Plan
**Version:** 2.0 | **Date:** 2026-06-10 | **Author:** System Design

---

## DELIVERABLE A — REDESIGNED ARCHITECTURE

### A.1 System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                       PRESENTATION LAYER                            │
│  ┌──────────────┐  ┌──────────────────┐  ┌──────────────────────┐  │
│  │ Doctor UI    │  │  Radiology UI    │  │   Admin Dashboard    │  │
│  │ (Submit Req) │  │ (Manage/Schedule)│  │  (Analytics/Audit)   │  │
│  └──────┬───────┘  └────────┬─────────┘  └──────────┬───────────┘  │
└─────────┼────────────────────┼───────────────────────┼─────────────┘
          │  Thymeleaf SSR + REST/JSON (fetch API)      │
┌─────────▼────────────────────▼───────────────────────▼─────────────┐
│                        API LAYER (REST Controllers)                 │
│  ImagingRequestController  AppointmentController  NotificationCtrl  │
│  SchedulingPageController  (Thymeleaf page views)                  │
└─────────────────────────────────┬───────────────────────────────────┘
                                  │ Spring Security (Role-Based)
┌─────────────────────────────────▼───────────────────────────────────┐
│                       SERVICE LAYER                                 │
│  ImagingRequestService   AppointmentService   AuditLogService       │
│  PrioritizationService   SchedulingNotificationService              │
│  RequestSequenceService  RequestReviewService                       │
└─────────────────────────────────┬───────────────────────────────────┘
                                  │ Spring Data MongoDB Repositories
┌─────────────────────────────────▼───────────────────────────────────┐
│                       PERSISTENCE LAYER (MongoDB)                   │
│  RadiologyRequest   RadiologyAppointment   RequestReview            │
│  AuditLog   Notification   requestSequence                          │
└─────────────────────────────────────────────────────────────────────┘
```

### A.2 Module Package Structure

```
com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling
├── controller/
│   ├── ImagingRequestController.java      # UCR008, UCR013, UCR014 REST
│   ├── AppointmentController.java         # UCR010, UCR015 REST
│   ├── NotificationController.java        # UCR012 REST
│   └── SchedulingPageController.java      # Thymeleaf page views
├── service/
│   ├── ImagingRequestService.java         # UCR008, UCR014 business logic
│   ├── AppointmentService.java            # UCR010, UCR015 business logic
│   ├── PrioritizationService.java         # UCR011 AI scoring
│   ├── RequestReviewService.java          # UCR009 review audit trail
│   ├── SchedulingNotificationService.java # UCR012 notifications
│   ├── AuditLogService.java               # System-wide audit trail
│   └── RequestSequenceService.java        # REQ000001 ID generation
├── model/
│   ├── ImagingRequest.java
│   ├── RadiologyAppointment.java
│   ├── RequestReview.java                 # NEW
│   ├── AuditLog.java                      # NEW
│   ├── Notification.java
│   ├── RequestStatus.java                 # EXPANDED
│   ├── AppointmentStatus.java             # EXPANDED
│   ├── Modality.java
│   ├── UrgencyLevel.java
│   └── NotificationChannel.java
├── repository/
│   ├── ImagingRequestRepository.java
│   ├── RadiologyAppointmentRepository.java
│   ├── RequestReviewRepository.java       # NEW
│   ├── AuditLogRepository.java            # NEW
│   └── NotificationRepository.java
├── dto/
│   ├── SubmitImagingRequestDTO.java
│   ├── ManageRequestDTO.java
│   ├── ScheduleAppointmentDTO.java
│   ├── CancelRequestDTO.java
│   ├── CancelAppointmentDTO.java          # NEW
│   ├── ImagingRequestResponseDTO.java
│   ├── AppointmentResponseDTO.java
│   └── NotificationResponseDTO.java
└── exception/
    ├── ImagingRequestNotFoundException.java
    ├── AppointmentNotFoundException.java
    ├── SlotUnavailableException.java
    └── SchedulingExceptionHandler.java
```

---

## DELIVERABLE B — UPDATED DATABASE SCHEMA

### B.1 Collection: `RadiologyRequest`

| Field                   | Type     | Index    | Description                              |
|-------------------------|----------|----------|------------------------------------------|
| `_id` (requestId)       | String   | PK       | Format: REQ000001, REQ000002, ...        |
| `patientId`             | String   | Indexed  | References User/Patient                  |
| `doctorId`              | String   | Indexed  | Submitting doctor (UCR008)               |
| `modality`              | Enum     |          | XRAY, CT, MRI, ULTRASOUND               |
| `bodyPart`              | String   |          | Body region to image                     |
| `urgencyLevel`          | Enum     |          | ROUTINE, URGENT, EMERGENCY              |
| `clinicalNotes`         | String   |          | Clinical indication / symptoms           |
| `referringPhysician`    | String   |          | Referring doctor name                    |
| `priorityScore`         | Integer  |          | AI-computed 0–100                        |
| `status`                | Enum     | Indexed  | See RequestStatus                        |
| `rejectionReason`       | String   |          | Populated on REJECTED                    |
| `cancellationReason`    | String   |          | Populated on CANCELLED                   |
| `cancelledBy`           | String   |          | userId of cancelling staff               |
| `cancelledByRole`       | String   |          | Role of cancelling staff                 |
| `radiologistId`         | String   |          | Reviewing radiologist                    |
| `createdDate`           | Date     |          | Submission timestamp                     |
| `updatedDate`           | Date     |          | Last modification timestamp              |

### B.2 Collection: `RadiologyAppointment`

| Field              | Type     | Index    | Description                              |
|--------------------|----------|----------|------------------------------------------|
| `_id` (apptId)     | String   | PK       | UUID                                     |
| `requestId`        | String   | Indexed  | Parent ImagingRequest reference          |
| `appointmentDate`  | Date     |          | Scheduled date                           |
| `timeSlot`         | String   |          | e.g. "09:00–10:00"                       |
| `equipment`        | String   |          | Machine/room identifier                  |
| `radiographerId`   | String   | Indexed  | Assigned radiographer                    |
| `status`           | Enum     | Indexed  | See AppointmentStatus                    |
| `cancellationReason` | String |          | Reason when CANCELLED                    |
| `cancelledBy`      | String   |          | userId of cancelling staff               |
| `createdDate`      | Date     |          | Creation timestamp                       |
| `updatedDate`      | Date     |          | Last modification timestamp              |

### B.3 Collection: `RequestReview` (NEW)

| Field              | Type     | Index    | Description                              |
|--------------------|----------|----------|------------------------------------------|
| `_id` (reviewId)   | String   | PK       | UUID                                     |
| `requestId`        | String   | Indexed  | Parent ImagingRequest reference          |
| `reviewerId`       | String   | Indexed  | Radiologist/Radiographer who reviewed    |
| `reviewerRole`     | String   |          | RADIOLOGIST or RADIOGRAPHER              |
| `action`           | String   |          | APPROVED or REJECTED                     |
| `notes`            | String   |          | Optional review notes                    |
| `rejectionReason`  | String   |          | Required when action=REJECTED            |
| `reviewedAt`       | Date     |          | Timestamp of review decision             |

### B.4 Collection: `AuditLog` (NEW)

| Field            | Type     | Index    | Description                               |
|------------------|----------|----------|-------------------------------------------|
| `_id` (logId)    | String   | PK       | UUID                                      |
| `entityType`     | String   | Indexed  | IMAGING_REQUEST, APPOINTMENT              |
| `entityId`       | String   | Indexed  | The affected entity ID                    |
| `action`         | String   |          | SUBMITTED, APPROVED, REJECTED, SCHEDULED, |
|                  |          |          | CANCELLED, COMPLETED, IN_PROGRESS, etc.   |
| `performedBy`    | String   | Indexed  | userId of actor                           |
| `performedByRole`| String   |          | Role of actor                             |
| `performedAt`    | Date     | Indexed  | Action timestamp                          |
| `oldStatus`      | String   |          | Previous status                           |
| `newStatus`      | String   |          | New status after action                   |
| `details`        | String   |          | Free-text context / reason                |

### B.5 Collection: `Notification`

| Field            | Type     | Index    | Description                               |
|------------------|----------|----------|-------------------------------------------|
| `_id` (notifId)  | String   | PK       | UUID                                      |
| `userId`         | String   | Indexed  | Recipient user ID                         |
| `message`        | String   |          | Notification body text                    |
| `channel`        | Enum     |          | SYSTEM, EMAIL, SMS                        |
| `timestamp`      | Date     | Indexed  | When notification was created             |
| `isRead`         | Boolean  |          | Read/unread flag                          |

### B.6 Collection: `requestSequence`

| Field  | Type   | Description                          |
|--------|--------|--------------------------------------|
| `_id`  | String | Key: "imagingRequestSeq"             |
| `seq`  | Long   | Auto-incremented counter             |

---

## DELIVERABLE C — UPDATED ERD

```
┌──────────────────────────────────────────────────────────────────┐
│  ERD — Radiology Request & Scheduling Module                     │
│                                                                  │
│  USER (userId PK)                                                │
│    │ 1                                                           │
│    ├────────────────────── submits ───────────────┐             │
│    │                                              │             │
│    ▼ N                                            │             │
│  IMAGING_REQUEST (requestId PK)                  │             │
│    ├── patientId FK → USER                        │             │
│    ├── doctorId FK → USER ◄────────────────────── ┘             │
│    ├── radiologistId FK → USER (optional)                       │
│    │                                                            │
│    │ 1                                                          │
│    ├──── has ────────────────────────┐                          │
│    │                                │                          │
│    ▼ 0..1                           ▼ 0..N                     │
│  RADIOLOGY_APPOINTMENT           REQUEST_REVIEW                 │
│  (appointmentId PK)              (reviewId PK)                  │
│    ├── requestId FK                 ├── requestId FK            │
│    ├── radiographerId FK → USER     ├── reviewerId FK → USER    │
│    │                                                            │
│    │ 1..N                 1..N                                  │
│    └────────┐   ┌──────────┘                                    │
│             ▼   ▼                                               │
│           AUDIT_LOG                                             │
│           (logId PK)                                            │
│             ├── entityId (polymorphic: requestId or apptId)     │
│             ├── performedBy FK → USER                           │
│                                                                 │
│  USER ────── receives ──────► NOTIFICATION (notificationId PK) │
└──────────────────────────────────────────────────────────────────┘
```

---

## DELIVERABLE D — UPDATED CLASS DIAGRAM

```
«enumeration»                    «enumeration»
RequestStatus                    AppointmentStatus
─────────────                    ─────────────────
PENDING                          SCHEDULED
APPROVED                         CONFIRMED
REJECTED                         IN_PROGRESS
SCHEDULED                        COMPLETED
IN_PROGRESS                      CANCELLED
COMPLETED                        NO_SHOW
REPORT_PENDING
REPORT_READY
CANCELLED

«entity»                         «entity»
ImagingRequest                   RadiologyAppointment
──────────────                   ────────────────────
- requestId: String              - appointmentId: String
- patientId: String              - requestId: String
- doctorId: String               - appointmentDate: Date
- modality: Modality             - timeSlot: String
- bodyPart: String               - equipment: String
- urgencyLevel: UrgencyLevel     - radiographerId: String
- clinicalNotes: String          - status: AppointmentStatus
- referringPhysician: String     - cancellationReason: String
- priorityScore: Integer         - cancelledBy: String
- status: RequestStatus          - createdDate: Date
- rejectionReason: String        - updatedDate: Date
- cancellationReason: String     + builder(): Builder
- cancelledBy: String
- cancelledByRole: String        «entity»
- radiologistId: String          RequestReview
- createdDate: Date              ─────────────
- updatedDate: Date              - reviewId: String
+ builder(): Builder             - requestId: String
                                 - reviewerId: String
«entity»                         - reviewerRole: String
AuditLog                         - action: String
────────                         - notes: String
- logId: String                  - rejectionReason: String
- entityType: String             - reviewedAt: Date
- entityId: String
- action: String                 «service»
- performedBy: String            ImagingRequestService
- performedByRole: String        ─────────────────────
- performedAt: Date              + submitRequest(dto, submitterId)
- oldStatus: String              + manageRequest(id, dto, staffId)
- newStatus: String              + cancelRequest(id, staffId, role, reason)
- details: String                + markScheduled(id)
                                 + markInProgress(id)
«service»                        + markCompleted(id)
AppointmentService               + markReportPending(id)
──────────────────               + markReportReady(id)
+ scheduleAppointment(dto)       + markApprovedAgain(id)
+ confirmAppointment(id)         + getRequest(id)
+ markInProgress(id)             + getAllRequests()
+ completeAppointment(id)        + getRequestsByDoctor(id)
+ cancelAppointment(id, reason)
+ cancelAppointment(id, reason)  «service»
                                 PrioritizationService
«service»                        ─────────────────────
AuditLogService                  + computeScore(urgency, notes)
───────────────                  + computePriorityLevel(score)
+ log(entityType, entityId,
      action, by, role,          «service»
      oldStatus, newStatus,      SchedulingNotificationService
      details)                   ──────────────────────────────
                                 + notifyRequestSubmitted(...)
                                 + notifyRequestApproved(...)
                                 + notifyRequestRejected(...)
                                 + notifyRequestCancelled(...)
                                 + notifyAppointmentScheduled(...)
                                 + notifyAppointmentCancelled(...)
                                 + notifyAppointmentCompleted(...)
```

---

## DELIVERABLE E — UPDATED USE CASE RELATIONSHIPS

```
┌─────────────────────────────────────────────────────────────────┐
│              RADIOLOGY REQUEST & SCHEDULING MODULE              │
│                     Use Case Diagram                            │
│                                                                 │
│  ┌──────────┐                                                   │
│  │  Doctor  │──── UCR008: Submit Imaging Request                │
│  └──────────┘     UCR013: View Request Status                   │
│                                                                 │
│  ┌──────────────┐                                               │
│  │ Radiologist  │──── UCR009: Manage Requests (Approve/Reject)  │
│  └──────────────┘     UCR010: Schedule Appointments             │
│         │             UCR014: Cancel Imaging Request            │
│         │             UCR015: Cancel Appointment                │
│                                                                 │
│  ┌──────────────┐                                               │
│  │ Radiographer │──── UCR009: Manage Requests (Approve/Reject)  │
│  └──────────────┘     UCR010: Schedule Appointments             │
│         │             UCR014: Cancel Imaging Request            │
│         │             UCR015: Cancel Appointment                │
│                                                                 │
│  ┌───────┐                                                      │
│  │ Admin │──── UCR013: View Request Status                      │
│  └───────┘                                                      │
│                                                                 │
│  ┌────────────┐                                                 │
│  │ AI System  │──── UCR011: Prioritize Requests (auto)          │
│  └────────────┘           <<include>> UCR010                   │
│                                                                 │
│  ┌────────┐                                                     │
│  │ System │──── UCR012: Send Notifications (auto)               │
│  └────────┘           triggered by all UCR                     │
│                                                                 │
│  Relationships:                                                 │
│  UCR009 <<extend>> UCR014  (can cancel while managing)         │
│  UCR010 <<include>> UCR011 (AI runs after scheduling)          │
│  UCR010 <<extend>> UCR015  (can cancel scheduled appt)         │
│  UCR008 <<include>> UCR011 (AI runs after submission)          │
│  All UCR <<include>> UCR012 (notifications on every event)     │
└─────────────────────────────────────────────────────────────────┘
```

---

## DELIVERABLE F — UPDATED UI LAYOUTS

### F.1 Doctor Pages

```
/request-scheduling     — Request & Scheduling Dashboard
┌────────────────────────────────────────────────────────┐
│ SIDEBAR │ UCR008 / UCR013 — Request & Scheduling        │
│         │                                              │
│         │ [Stats: Pending | Approved | Scheduled | ... ]│
│         │                                              │
│         │ ┌──── Submit New Request ─────────────────┐  │
│         │ │ Patient* | Modality* | Body Part*        │  │
│         │ │ Referring Physician* | Urgency*          │  │
│         │ │ Clinical Indication (textarea)           │  │
│         │ │ [AI Priority Preview]    [Submit]        │  │
│         │ └──────────────────────────────────────────┘  │
│         │                                              │
│         │ ┌──── My Requests (Table) ────────────────┐  │
│         │ │ ID | Patient | Modality | Priority | Status│  │
│         │ │ REQ000001 | ... | MRI | 85/100 | APPROVED │  │
│         │ └──────────────────────────────────────────┘  │
│         │                                              │
│         │ ┌──── Request Timeline ───────────────────┐  │
│         │ │ Submitted→Pending→Approved→Scheduled→Done│  │
│         │ └──────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘

/request-status         — Status Tracking (Read-Only)
┌────────────────────────────────────────────────────────┐
│ [Search by ID] [Filter by Status ▼]    N of M shown   │
│                                                        │
│ ┌── REQ000001 ─ MRI ──────────── APPROVED ──────────┐ │
│ │ Urgency: URGENT | Priority: 85/100                 │ │
│ │ Patient: P001 | Submitted: 05 Jun 2026             │ │
│ └────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

### F.2 Radiology Staff Pages (Radiologist + Radiographer)

```
/manage-requests        — UCR009: Approve/Reject + UCR014: Cancel
┌────────────────────────────────────────────────────────┐
│ [Stats: Pending | Approved | Rejected | ...]           │
│                                                        │
│ ┌── AI Priority Queue — Pending Approvals ──────────┐ │
│ │ ID | Patient | Doctor | Modality | Urgency | Score │ │
│ │ [Approve] [Reject] [Cancel]                        │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ ┌── All Requests History ────────────────────────────┐ │
│ │ ID | Modality | Status | Updated                   │ │
│ │ APPROVED → [Cancel Request]                        │ │
│ └────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘

/appointment-scheduling — UCR010: Schedule + UCR015: Cancel
┌────────────────────────────────────────────────────────┐
│ ┌── Approved Requests (clickable) ───────────────────┐ │
│ │ REQ000001 | MRI | URGENT | Priority: 85            │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ ┌── Schedule Form (shown on request click) ─────────┐ │
│ │ Request: REQ000001 | Date* | Time Slot* | Machine* │ │
│ │ [Confirm Schedule]                                  │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ ┌── Scheduled Appointments ──────────────────────────┐ │
│ │ Appt ID | Date | Slot | Equipment | Status         │ │
│ │ [Mark Complete] [Cancel Appt]                      │ │
│ └────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

### F.3 Admin Pages

```
/admin/radiology-analytics — Request & Appointment Analytics
┌────────────────────────────────────────────────────────┐
│ [Total Requests] [Approval Rate] [Avg Wait Time] [...]  │
│                                                        │
│ ┌── Request Status Distribution ─────────────────────┐ │
│ │ PENDING: 12 | APPROVED: 8 | SCHEDULED: 15 | ...    │ │
│ └────────────────────────────────────────────────────┘ │
│ ┌── Cancellation Analytics ──────────────────────────┐ │
│ │ Request Cancellations: 3 | Appt Cancellations: 2   │ │
│ └────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────┘
```

---

## DELIVERABLE G — UPDATED API ENDPOINTS

### G.1 Imaging Request Endpoints

| Method | Endpoint                              | Role(s)              | UCR    | Description                     |
|--------|---------------------------------------|----------------------|--------|---------------------------------|
| POST   | /api/imaging-requests                 | DOCTOR               | UCR008 | Submit new imaging request      |
| GET    | /api/imaging-requests                 | RADIOLOGIST, RADIOGRAPHER, ADMIN | UCR013 | Get all requests        |
| GET    | /api/imaging-requests/{id}            | DOCTOR, ADMIN, RADIOLOGIST, RADIOGRAPHER | UCR013 | Get single request |
| GET    | /api/imaging-requests/my              | DOCTOR               | UCR013 | Get doctor's own requests       |
| GET    | /api/imaging-requests/pending         | RADIOLOGIST, RADIOGRAPHER | UCR009 | Pending priority queue     |
| GET    | /api/imaging-requests/patient/{pid}   | DOCTOR, ADMIN        | UCR013 | Requests by patient             |
| PATCH  | /api/imaging-requests/{id}/manage     | RADIOLOGIST, RADIOGRAPHER | UCR009 | Approve or reject          |
| PATCH  | /api/imaging-requests/{id}/cancel     | RADIOLOGIST, RADIOGRAPHER | UCR014 | Cancel request             |
| PATCH  | /api/imaging-requests/{id}/in-progress| RADIOLOGIST, RADIOGRAPHER | —   | Mark imaging in progress        |
| PATCH  | /api/imaging-requests/{id}/report-pending | RADIOLOGIST, RADIOGRAPHER | — | Mark imaging completed      |
| PATCH  | /api/imaging-requests/{id}/report-ready   | RADIOLOGIST       | —      | Mark report ready               |

### G.2 Appointment Endpoints

| Method | Endpoint                              | Role(s)                        | UCR    | Description                     |
|--------|---------------------------------------|--------------------------------|--------|---------------------------------|
| POST   | /api/appointments                     | RADIOLOGIST, RADIOGRAPHER      | UCR010 | Schedule new appointment        |
| GET    | /api/appointments                     | RADIOLOGIST, RADIOGRAPHER, ADMIN | —    | Get all appointments            |
| GET    | /api/appointments/{id}                | RADIOLOGIST, RADIOGRAPHER      | —      | Get single appointment          |
| GET    | /api/appointments/by-request/{reqId}  | RADIOLOGIST, RADIOGRAPHER, DOCTOR, ADMIN | — | Appointment by request     |
| GET    | /api/appointments/my                  | RADIOGRAPHER                   | —      | Own assigned appointments       |
| PATCH  | /api/appointments/{id}/confirm        | RADIOLOGIST, RADIOGRAPHER      | UCR010 | Confirm scheduled appointment   |
| PATCH  | /api/appointments/{id}/in-progress    | RADIOGRAPHER                   | —      | Mark imaging started            |
| PATCH  | /api/appointments/{id}/complete       | RADIOGRAPHER                   | UCR010 | Mark appointment completed      |
| PATCH  | /api/appointments/{id}/cancel         | RADIOLOGIST, RADIOGRAPHER      | UCR015 | Cancel appointment (with reason)|
| PATCH  | /api/appointments/{id}/no-show        | RADIOGRAPHER                   | —      | Mark as no-show                 |

### G.3 Notification Endpoints

| Method | Endpoint                              | Role(s)     | UCR    | Description                     |
|--------|---------------------------------------|-------------|--------|---------------------------------|
| GET    | /api/notifications                    | ALL         | UCR012 | Get own notifications           |
| PATCH  | /api/notifications/{id}/read          | ALL         | UCR012 | Mark notification read          |
| PATCH  | /api/notifications/read-all           | ALL         | UCR012 | Mark all as read                |

### G.4 Page Endpoints (Thymeleaf)

| Method | Path                       | Role(s)                      | UCR    |
|--------|----------------------------|------------------------------|--------|
| GET    | /request-scheduling        | DOCTOR                       | UCR008, UCR013 |
| GET    | /imaging-request-form      | DOCTOR                       | UCR008 |
| GET    | /request-status            | ADMIN, DOCTOR                | UCR013 |
| GET    | /manage-requests           | RADIOLOGIST, RADIOGRAPHER    | UCR009, UCR014 |
| GET    | /appointment-scheduling    | RADIOLOGIST, RADIOGRAPHER    | UCR010, UCR015 |
| GET    | /notifications             | ALL authenticated            | UCR012 |

---

## DELIVERABLE H — UPDATED BUSINESS RULES

### H.1 UCR008 — Submit Imaging Request

| Rule ID | Rule Description |
|---------|-----------------|
| BRQ001  | Only users with role DOCTOR may submit imaging requests. |
| BRQ002  | Patient, Modality, Body Part, Urgency Level, and Referring Physician are mandatory. |
| BRQ003  | Request ID is auto-generated in format REQ000001 (sequential, atomic). |
| BRQ004  | AI priority score is computed immediately on submission. Score range: 0–100. |
| BRQ005  | EMERGENCY urgency = base score 100; URGENT = 70; ROUTINE = 30. |
| BRQ006  | Clinical notes containing high-risk keywords (stroke, bleeding, trauma) add +20 to score (max 100). |
| BRQ007  | Initial status = PENDING. |
| BRQ008  | A notification is sent to the patient when a request is submitted on their behalf. |

### H.2 UCR009 — Manage Requests (Approve/Reject)

| Rule ID | Rule Description |
|---------|-----------------|
| BRQ009  | Only RADIOLOGIST and RADIOGRAPHER roles may approve or reject requests. |
| BRQ010  | Only PENDING requests may be approved or rejected. |
| BRQ011  | Rejection requires a non-empty rejection reason. |
| BRQ012  | On APPROVED: status changes PENDING → APPROVED; reviewing staff ID stored as radiologistId. |
| BRQ013  | On REJECTED: status changes PENDING → REJECTED; rejection reason stored. |
| BRQ014  | A ReviewRecord is created for every approve/reject decision (audit trail). |
| BRQ015  | Doctor and Patient are notified on both APPROVED and REJECTED. |

### H.3 UCR010 — Schedule Appointments

| Rule ID | Rule Description |
|---------|-----------------|
| BRQ016  | Only RADIOLOGIST and RADIOGRAPHER roles may schedule appointments. |
| BRQ017  | Only APPROVED requests may be scheduled. |
| BRQ018  | Slot conflict check: same equipment + date + time slot cannot be double-booked. |
| BRQ019  | On scheduling: request status changes APPROVED → SCHEDULED; appointment status = SCHEDULED. |
| BRQ020  | Doctor and Patient are notified when an appointment is scheduled. |
| BRQ021  | AI priority score is used to suggest scheduling order in the queue (higher score = sooner). |

### H.4 UCR011 — AI Prioritization

| Rule ID | Rule Description |
|---------|-----------------|
| BRQ022  | Priority score is computed automatically; cannot be manually overridden. |
| BRQ023  | Priority levels: CRITICAL (score ≥ 90), HIGH (score ≥ 60), STANDARD (score < 60). |
| BRQ024  | The manage-requests queue is sorted by priority score descending (highest first). |

### H.5 UCR012 — Notifications

| Rule ID | Rule Description |
|---------|-----------------|
| BRQ025  | Every system event triggers an in-app notification persisted to MongoDB. |
| BRQ026  | If the user has an email on record, an email notification is also sent. |
| BRQ027  | Email failure does NOT fail the main operation (try-catch, warn-only). |
| BRQ028  | Notifications are immutable once created. |

### H.6 UCR013 — View Request Status

| Rule ID | Rule Description |
|---------|-----------------|
| BRQ029  | Only ADMIN and DOCTOR roles may access the request status view. |
| BRQ030  | Doctors see only their own submitted requests. Admins see all requests. |
| BRQ031  | Client-side search by Request ID and filter by status are available. |
| BRQ032  | Request timeline displays the full lifecycle in visual step format. |

### H.7 UCR014 — Cancel Imaging Request

| Rule ID | Rule Description |
|---------|-----------------|
| BRQ033  | Only RADIOLOGIST and RADIOGRAPHER roles may cancel imaging requests. |
| BRQ034  | COMPLETED, REPORT_PENDING, and REPORT_READY requests cannot be cancelled. |
| BRQ035  | Already-CANCELLED requests return error "already cancelled". |
| BRQ036  | Cancellation reason is required (non-empty). |
| BRQ037  | The cancelling staff's userId and role are stored in the request document. |
| BRQ038  | Doctor is notified when their request is cancelled. |
| BRQ039  | An AuditLog entry is created for every cancellation. |

### H.8 UCR015 — Cancel Appointment

| Rule ID | Rule Description |
|---------|-----------------|
| BRQ040  | Only RADIOLOGIST and RADIOGRAPHER roles may cancel appointments. |
| BRQ041  | COMPLETED appointments cannot be cancelled. |
| BRQ042  | Already-CANCELLED appointments return error "already cancelled". |
| BRQ043  | Cancellation reason is required (non-empty). |
| BRQ044  | On cancellation: appointment status → CANCELLED; linked request reverts SCHEDULED → APPROVED. |
| BRQ045  | Doctor and Patient are notified when an appointment is cancelled. |
| BRQ046  | An AuditLog entry is created for every cancellation. |

---

## DELIVERABLE I — UPDATED STATE TRANSITIONS

### I.1 ImagingRequest State Machine

```
                    ┌──────────────────────────────────────┐
                    │         ImagingRequest States         │
                    └──────────────────────────────────────┘

  [Doctor submits]
       │
       ▼
   ┌─────────┐
   │ PENDING │ ◄─────────── Initial state on submission
   └────┬────┘
        │ [Radiologist/Radiographer reviews]
        ├─────────────────────────────────────────────────────┐
        │ [APPROVE]                                           │ [REJECT]
        ▼                                                     ▼
   ┌──────────┐                                          ┌──────────┐
   │ APPROVED │                                          │ REJECTED │ (terminal*)
   └────┬─────┘                                          └──────────┘
        │ [Radiologist/Radiographer schedules]
        ▼
   ┌───────────┐
   │ SCHEDULED │
   └─────┬─────┘
         │ [Radiographer marks imaging started]
         ▼
   ┌─────────────┐
   │ IN_PROGRESS │
   └──────┬──────┘
          │ [Radiographer marks imaging done]
          ▼
   ┌───────────┐
   │ COMPLETED │
   └─────┬─────┘
         │ [auto-transition]
         ▼
   ┌───────────────┐
   │ REPORT_PENDING│
   └───────┬───────┘
           │ [Radiologist completes report]
           ▼
   ┌─────────────┐
   │ REPORT_READY │ (terminal — success path)
   └─────────────┘

  Cancellation paths (from PENDING, APPROVED, SCHEDULED only):
  [Any cancellable state] ──[Radiologist/Radiographer cancels]──► CANCELLED (terminal)

  Forbidden transitions:
  COMPLETED → CANCELLED       (BRQ034)
  REPORT_PENDING → CANCELLED  (BRQ034)
  REPORT_READY → CANCELLED    (BRQ034)
  REJECTED → APPROVED         (no re-review without new submission)
  SCHEDULED → CANCELLED       (must cancel appointment first → reverts to APPROVED → then cancel)
```

### I.2 RadiologyAppointment State Machine

```
                    ┌──────────────────────────────────────┐
                    │        RadiologyAppointment States    │
                    └──────────────────────────────────────┘

  [Radiologist/Radiographer schedules]
       │
       ▼
   ┌───────────┐
   │ SCHEDULED │
   └─────┬─────┘
         │ [Radiologist/Radiographer confirms]
         ▼
   ┌───────────┐
   │ CONFIRMED │
   └─────┬─────┘
         │ [Patient arrives, imaging begins]
         ├──────────────────────────────────────────────────┐
         │                                                  │ [Patient absent]
         ▼                                                  ▼
   ┌─────────────┐                                     ┌─────────┐
   │ IN_PROGRESS │                                     │ NO_SHOW │ (terminal)
   └──────┬──────┘                                     └─────────┘
          │ [Imaging completed]
          ▼
   ┌───────────┐
   │ COMPLETED │ (terminal — success path)
   └───────────┘

  Cancellation path (from SCHEDULED or CONFIRMED):
  [SCHEDULED|CONFIRMED] ──[Radiologist/Radiographer cancels with reason]──► CANCELLED (terminal)
  On CANCELLED: linked ImagingRequest reverts SCHEDULED → APPROVED

  Forbidden transitions:
  COMPLETED → CANCELLED     (BRQ041)
  IN_PROGRESS → CANCELLED   (imaging already started)
```

### I.3 State Transition Guard Table

| From           | To             | Guard Condition                   | Actor                         |
|----------------|----------------|-----------------------------------|-------------------------------|
| PENDING        | APPROVED       | request.status == PENDING         | RADIOLOGIST, RADIOGRAPHER     |
| PENDING        | REJECTED       | request.status == PENDING AND reason != empty | RADIOLOGIST, RADIOGRAPHER |
| APPROVED       | SCHEDULED      | request.status == APPROVED        | RADIOLOGIST, RADIOGRAPHER     |
| SCHEDULED      | IN_PROGRESS    | appointment.status == CONFIRMED   | RADIOGRAPHER                  |
| IN_PROGRESS    | COMPLETED      | request.status == IN_PROGRESS     | RADIOGRAPHER                  |
| COMPLETED      | REPORT_PENDING | auto-transition                   | SYSTEM                        |
| REPORT_PENDING | REPORT_READY   | —                                 | RADIOLOGIST                   |
| PENDING        | CANCELLED      | reason != empty                   | RADIOLOGIST, RADIOGRAPHER     |
| APPROVED       | CANCELLED      | reason != empty                   | RADIOLOGIST, RADIOGRAPHER     |
| SCHEDULED      | CANCELLED      | FORBIDDEN — cancel appointment first | —                          |

---

## DELIVERABLE J — COMPLETE IMPLEMENTATION PLAN

### J.1 Phase 1 — Role & Permission Corrections (Priority: CRITICAL)

| Task | File | Change |
|------|------|--------|
| J1-1 | SecurityConfiguration.java | Remove CLINIC_ASSISTANT from all scheduling routes |
| J1-2 | SecurityConfiguration.java | /request-status → ADMIN + DOCTOR only |
| J1-3 | SecurityConfiguration.java | /api/imaging-requests/*/cancel → RADIOLOGIST + RADIOGRAPHER |
| J1-4 | SecurityConfiguration.java | /manage-requests → RADIOLOGIST + RADIOGRAPHER |
| J1-5 | ImagingRequestService.java | cancelRequest: authorization = RADIOLOGIST + RADIOGRAPHER |
| J1-6 | requestStatus.html | Remove cancel button from Doctor view |
| J1-7 | manageRequests.html | Add Cancel Request buttons for RADIOLOGIST + RADIOGRAPHER |

### J.2 Phase 2 — Extended State Machine (Priority: HIGH)

| Task | File | Change |
|------|------|--------|
| J2-1 | RequestStatus.java | Add IN_PROGRESS, REPORT_PENDING, REPORT_READY |
| J2-2 | AppointmentStatus.java | Add CONFIRMED, IN_PROGRESS, NO_SHOW |
| J2-3 | ImagingRequest.java | Add cancelledBy, cancelledByRole fields |
| J2-4 | RadiologyAppointment.java | Add cancellationReason, cancelledBy, updatedDate |
| J2-5 | ImagingRequestService.java | Add markInProgress, markReportPending, markReportReady |
| J2-6 | AppointmentService.java | Add confirmAppointment, markInProgress, markNoShow; add reason to cancel |
| J2-7 | CancelAppointmentDTO.java | New DTO with cancellationReason field |
| J2-8 | AppointmentController.java | Add confirm, in-progress, no-show, cancel-with-reason endpoints |
| J2-9 | ImagingRequestController.java | Add in-progress, report-pending, report-ready endpoints |

### J.3 Phase 3 — New Audit & Review Entities (Priority: HIGH)

| Task | File | Change |
|------|------|--------|
| J3-1 | RequestReview.java | New entity: request review audit record |
| J3-2 | RequestReviewRepository.java | Spring Data MongoDB repository |
| J3-3 | AuditLog.java | New entity: system-wide audit trail |
| J3-4 | AuditLogRepository.java | Spring Data MongoDB repository |
| J3-5 | AuditLogService.java | log() method called on every state transition |
| J3-6 | ImagingRequestService.java | Inject AuditLogService; log submit, approve, reject, cancel |
| J3-7 | AppointmentService.java | Inject AuditLogService; log schedule, cancel, complete |
| J3-8 | RequestReviewService.java | saveReview() called on approve/reject |

### J.4 Phase 4 — UI Enhancements (Priority: MEDIUM)

| Task | File | Change |
|------|------|--------|
| J4-1 | manageRequests.html | Add Cancel Request modal + JS for RADIOLOGIST/RADIOGRAPHER |
| J4-2 | requestStatus.html | Remove cancel button; keep read-only timeline for DOCTOR/ADMIN |
| J4-3 | requestScheduling.html | Update status badges for new states (IN_PROGRESS, REPORT_PENDING, REPORT_READY) |
| J4-4 | appointmentScheduling.html | Add Confirm, In-Progress buttons; update badge states |
| J4-5 | All templates | Add new status badge CSS classes |

### J.5 Phase 5 — Analytics & Reporting (Priority: LOW)

| Task | File | Change |
|------|------|--------|
| J5-1 | AnalyticsController.java | Aggregation queries for request/appointment metrics |
| J5-2 | analytics-dashboard.html | Charts for request distribution, wait times, cancellation rate |
| J5-3 | AuditLog queries | Timeline view in admin dashboard |

---

## ROLE PERMISSIONS MATRIX

| Entity / Action         | Doctor | Radiologist | Radiographer | Admin | AI System |
|-------------------------|--------|-------------|--------------|-------|-----------|
| **ImagingRequest**      |        |             |              |       |           |
| Create (Submit)         | ✓      | ✗           | ✗            | ✗     | ✗         |
| Read (Own)              | ✓      | ✗           | ✗            | ✓     | ✗         |
| Read (All)              | ✗      | ✓           | ✓            | ✓     | ✗         |
| Approve                 | ✗      | ✓           | ✓            | ✗     | ✗         |
| Reject                  | ✗      | ✓           | ✓            | ✗     | ✗         |
| Cancel                  | ✗      | ✓           | ✓            | ✗     | ✗         |
| Set Priority Score      | ✗      | ✗           | ✗            | ✗     | ✓ (auto)  |
| **RadiologyAppointment**|        |             |              |       |           |
| Create (Schedule)       | ✗      | ✓           | ✓            | ✗     | ✗         |
| Read (Own Assigned)     | ✗      | ✓           | ✓            | ✓     | ✗         |
| Confirm                 | ✗      | ✓           | ✓            | ✗     | ✗         |
| Mark In Progress        | ✗      | ✗           | ✓            | ✗     | ✗         |
| Mark Completed          | ✗      | ✗           | ✓            | ✗     | ✗         |
| Cancel                  | ✗      | ✓           | ✓            | ✗     | ✗         |
| **Notification**        |        |             |              |       |           |
| Read (Own)              | ✓      | ✓           | ✓            | ✓     | ✗         |
| Mark Read               | ✓      | ✓           | ✓            | ✓     | ✗         |
| **AuditLog**            |        |             |              |       |           |
| Read                    | ✗      | ✗           | ✗            | ✓     | ✗         |
| **RequestReview**       |        |             |              |       |           |
| Create                  | ✗      | ✓           | ✓            | ✗     | ✗         |
| Read                    | ✗      | ✓           | ✓            | ✓     | ✗         |

---

*End of Radiology Module Design Document v2.0*
*WellCheck Radiance — Final Year Project*
*Prepared for inclusion in SRS, SDD, STD, and thesis documentation*
