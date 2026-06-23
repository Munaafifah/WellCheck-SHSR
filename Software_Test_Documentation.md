# SOFTWARE TEST DOCUMENTATION (STD)

---

## WellCheck Radiance: A Smart Health Monitoring Management System with Integrated Radiology Department Module

---

| Field | Details |
|---|---|
| **Document Title** | Software Test Documentation (STD) |
| **System Name** | WellCheck Radiance: A Smart Health Monitoring Management System with Integrated Radiology Department Module |
| **Prepared by** | Nur Aisha Binti Rohaizat |
| **Institution** | Universiti Teknologi Malaysia (UTM) |
| **Programme** | Bachelor of Computer Science (Software Engineering) |
| **Document Version** | 1.0 |
| **Document Status** | Final |
| **Date** | June 2026 |

---

---

## REVISION HISTORY

| Version | Date | Author | Description of Changes |
|---|---|---|---|
| 1.0 | June 2026 | Nur Aisha Binti Rohaizat | Initial release of Software Test Documentation |

---

---

## TABLE OF CONTENTS

1. [Introduction](#1-introduction)
   - 1.1 Purpose
   - 1.2 Scope
   - 1.3 Definitions, Acronyms, and Abbreviations
   - 1.4 References
   - 1.5 Overview
2. [Test Overview](#2-test-overview)
   - 2.1 Test Objectives
   - 2.2 Test Approach
   - 2.3 Test Environment
   - 2.4 Test Entry and Exit Criteria
3. [Module 1 — Radiology Imaging Management (UCR001–UCR007)](#3-module-1--radiology-imaging-management)
   - TC001: Upload Images (UCR001)
   - TC002: Store / Organize Images (UCR002)
   - TC003: View Images (UCR003)
   - TC004: Download / Share Images (UCR004)
   - TC005: Anonymize Images (UCR005)
   - TC006: Search & Filter Images (UCR006)
   - TC007: Delete Images (UCR007)
4. [Module 2 — Radiology Request and Scheduling (UCR008–UCR014)](#4-module-2--radiology-request-and-scheduling)
   - TC008: Submit Imaging Request (UCR008)
   - TC009: Manage Requests (UCR009)
   - TC010: Schedule Appointments (UCR010)
   - TC011: AI Prioritization (UCR011)
   - TC012: Send Notifications (UCR012)
   - TC013: View Request Status (UCR013)
   - TC014: Cancel Imaging Requests (UCR014)
5. [Module 3 — Radiology Report Management (UCR015–UCR020)](#5-module-3--radiology-report-management)
   - TC015: Cancel Appointment (UCR015)
   - TC016: Upload Report (UCR016)
   - TC017: View Diagnostic Report (UCR017)
   - TC018: Track Report Status (UCR018)
   - TC019: Automate Alerts (UCR019)
   - TC020: Download / Share Diagnostic Report (UCR020)

---

---

## 1. INTRODUCTION

### 1.1 Purpose

This Software Test Documentation (STD) defines the test cases, test procedures, and expected outcomes for the **WellCheck Radiance: A Smart Health Monitoring Management System with Integrated Radiology Department Module**. This document is prepared in accordance with IEEE Standard 829 for Software and System Test Documentation, adapted to meet the Final Year Project submission requirements of Universiti Teknologi Malaysia (UTM).

The primary purpose of this document is to provide a structured, traceable, and verifiable test plan that ensures all functional requirements of the radiology-related modules are systematically validated before system deployment.

### 1.2 Scope

This STD covers functional testing for the following three modules of the WellCheck Radiance system:

| Module | Use Case Range | Description |
|---|---|---|
| Radiology Imaging Management | UCR001 – UCR007 | Covers upload, storage, viewing, download/sharing, anonymization, search/filter, and deletion of medical images. |
| Radiology Request and Scheduling | UCR008 – UCR014 | Covers submission, management, scheduling, AI prioritization, notifications, status viewing, and cancellation of imaging requests. |
| Radiology Report Management | UCR015 – UCR020 | Covers appointment cancellation, report upload, report viewing, status tracking, automated alerts, and report download/sharing. |

Modules outside the above scope (e.g., Sensor Management, Patient Health Monitoring, Prediction) are not covered in this document.

### 1.3 Definitions, Acronyms, and Abbreviations

| Term | Definition |
|---|---|
| STD | Software Test Documentation |
| UCR | Use Case Radiology — identifier prefix for radiology-related use cases |
| TC | Test Case |
| DICOM | Digital Imaging and Communications in Medicine — standard format for medical images |
| PACS | Picture Archiving and Communication System |
| AI | Artificial Intelligence |
| JWT | JSON Web Token — used for secure user authentication |
| HTTP | Hypertext Transfer Protocol |
| REST | Representational State Transfer |
| UI | User Interface |
| RBAC | Role-Based Access Control |
| UTM | Universiti Teknologi Malaysia |
| FYP | Final Year Project |
| N/A | Not Applicable |

### 1.4 References

| Reference | Description |
|---|---|
| IEEE Std 829-2008 | IEEE Standard for Software and System Test Documentation |
| WellCheck Radiance SRS | Software Requirements Specification document for WellCheck Radiance system |
| Spring Boot 3.x Documentation | Framework documentation for backend REST API implementation |
| MongoDB Documentation | NoSQL database documentation for data persistence |
| Spring Security Documentation | Security framework used for authentication and role-based access control |
| DICOM Standard (NEMA) | Standard governing medical image format and metadata |

### 1.5 Overview

This document is organized into five major sections. Section 1 provides the introduction and context. Section 2 describes the overall test approach, objectives, and environment. Sections 3, 4, and 5 present the detailed test cases for each respective module. Each test case is formatted with a standardized structure including prerequisites, test data, test conditions, and a step-by-step execution table with expected results.

---

---

## 2. TEST OVERVIEW

### 2.1 Test Objectives

The objectives of this test documentation are as follows:

1. To verify that all functional requirements associated with UCR001 through UCR020 are correctly implemented and behave as specified.
2. To validate that the system correctly handles both valid and invalid inputs across all radiology-related operations.
3. To confirm that role-based access control (RBAC) is enforced — ensuring that only authorized users can perform sensitive operations such as image download, report viewing, and imaging request management.
4. To ensure that system feedback (error messages, notifications, and status updates) is accurate, timely, and user-friendly.
5. To verify the integrity and correctness of AI-assisted prioritization within the scheduling module.

### 2.2 Test Approach

All test cases in this document employ **Black-Box Testing** methodology, focusing on the system's external behavior against its defined functional requirements. The testing approach is as follows:

- **Functional Testing**: Each use case is tested with both positive (valid input) and negative (invalid input / unauthorized access) scenarios.
- **Boundary Testing**: Applied where applicable — particularly for file size limits, form field validation, and scheduling slot availability.
- **Security Testing**: Unauthorized access scenarios are included for all modules that implement role-based access control.
- **Integration Testing**: Test cases verify correct interaction between controllers, services, repositories, and external services (e.g., notification service, AI prioritization service).

### 2.3 Test Environment

| Component | Specification |
|---|---|
| **Operating System** | Windows 11 |
| **Backend Framework** | Spring Boot 3.x (Java 17) |
| **Database** | MongoDB (NoSQL) |
| **Authentication** | Spring Security with session-based RBAC |
| **Frontend** | Thymeleaf templates (HTML5, Bootstrap, JavaScript) |
| **Build Tool** | Apache Maven |
| **IDE** | IntelliJ IDEA / Visual Studio Code |
| **Browser** | Google Chrome (latest stable version) |
| **Test Type** | Manual Functional Testing |
| **Test Execution Mode** | Local development environment |

### 2.4 Test Entry and Exit Criteria

#### Entry Criteria

- The application has been successfully compiled and deployed in the local development environment.
- All dependent modules (authentication, user management, database connectivity) are operational.
- Test data (user accounts with assigned roles, sample DICOM/JPEG images, dummy patient records) has been prepared and loaded.
- The test environment matches the specifications listed in Section 2.3.

#### Exit Criteria

- All test cases have been executed at least once.
- All critical (severity 1) and high (severity 2) defects have been resolved and re-tested.
- Test results have been recorded for each test case.
- The test summary report has been reviewed and signed off.

---

---

## 3. MODULE 1 — RADIOLOGY IMAGING MANAGEMENT

> **Module Description**: This module governs the management of medical images within the WellCheck Radiance system. It enables authorized healthcare personnel (radiographers, radiologists, and doctors) to upload, store, view, download, share, anonymize, search, filter, and delete medical images. All operations are subject to role-based access control enforced by the system's security configuration.

---

### 2.1 Test TC001 for Module Radiology Imaging Management: Upload Images (UCR001)

---

#### TC001_01: Upload Medical Image with Valid Data

| Field | Details |
|---|---|
| **Test Case ID** | TC001_01 |
| **Test Case Description** | Verify that an authorized user (radiographer) can successfully upload a medical image using valid file data and metadata. |
| **Use Case Reference** | UCR001 — Upload Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- The user is logged into the system with the role of **Radiographer**.
- The system is accessible and the Radiology Imaging module is operational.
- The database connection to MongoDB is active.
- A valid medical image file (DICOM or JPEG format, size ≤ 10 MB) is available on the local machine.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiographer01 (Role: Radiographer) |
| Patient ID | PAT-20240601-001 |
| Image File | chest_xray_sample.dcm (DICOM, 2.4 MB) |
| Modality | X-Ray |
| Body Part | Chest |
| Study Date | 2026-06-01 |
| Description | Chest PA view — routine screening |

**Test Conditions**

- The user is authenticated and authorized with the Radiographer role.
- The uploaded file is in a supported format (DICOM / JPEG / PNG).
- All mandatory metadata fields are populated.
- File size does not exceed the system-defined maximum limit.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Launch the application and navigate to the login page. | The login page is displayed successfully. |
| 2 | Enter valid credentials for a Radiographer account (radiographer01) and click **Login**. | The system authenticates the user and redirects to the Radiographer Dashboard. |
| 3 | From the dashboard navigation menu, click on **Radiology Imaging**. | The Radiology Imaging management page is displayed. |
| 4 | Click the **Upload Image** button. | An image upload form/modal is displayed with fields for patient ID, modality, body part, study date, description, and file attachment. |
| 5 | Enter Patient ID: `PAT-20240601-001`. | The patient ID field is populated. |
| 6 | Select Modality: `X-Ray` from the dropdown. | The modality is selected. |
| 7 | Enter Body Part: `Chest`. | The body part field is populated. |
| 8 | Enter Study Date: `2026-06-01`. | The study date is populated. |
| 9 | Enter Description: `Chest PA view — routine screening`. | The description field is populated. |
| 10 | Click **Choose File** and select `chest_xray_sample.dcm` from the local machine. | The file is attached and the filename is displayed in the form. |
| 11 | Click the **Upload** / **Submit** button. | The system processes the upload request. |
| 12 | Observe the system response. | A success notification is displayed (e.g., "Image uploaded successfully"). The image appears in the image list associated with Patient ID PAT-20240601-001. |
| 13 | Navigate to the image list and verify the uploaded image entry. | The image record displays correct metadata: patient ID, modality, body part, study date, description, and uploader name. |

---

#### TC001_02: Upload Medical Image with Invalid File Type

| Field | Details |
|---|---|
| **Test Case ID** | TC001_02 |
| **Test Case Description** | Verify that the system rejects an image upload attempt when an unsupported file format is provided. |
| **Use Case Reference** | UCR001 — Upload Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | High |

**Prerequisites**

- The user is logged in as a Radiographer.
- An unsupported file type (e.g., `.exe`, `.pdf`, `.txt`) is available on the local machine.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiographer01 (Role: Radiographer) |
| Patient ID | PAT-20240601-002 |
| Image File | document_report.pdf (PDF, 1.1 MB) |
| Modality | MRI |
| Body Part | Brain |

**Test Conditions**

- The uploaded file is in an unsupported format (not DICOM / JPEG / PNG).
- All other form fields are validly populated.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiographer01 and navigate to the Radiology Imaging page. | The Radiology Imaging page is displayed. |
| 2 | Click the **Upload Image** button. | The upload form is displayed. |
| 3 | Fill in all metadata fields (Patient ID, Modality, Body Part) with valid data. | All fields are populated correctly. |
| 4 | Click **Choose File** and select `document_report.pdf`. | The PDF file is attached to the form. |
| 5 | Click the **Upload** / **Submit** button. | The system validates the file type upon submission. |
| 6 | Observe the system response. | The system rejects the upload and displays an error message such as: "Invalid file format. Only DICOM, JPEG, and PNG formats are accepted." No record is created in the database. |

---

### 2.2 Test TC002 for Module 1: Store / Organize Images (UCR002)

---

#### TC002_01: Successfully Store / Organize Images

| Field | Details |
|---|---|
| **Test Case ID** | TC002_01 |
| **Test Case Description** | Verify that successfully uploaded medical images are correctly stored and organized within the system, accessible and retrievable by patient ID and study metadata. |
| **Use Case Reference** | UCR002 — Store / Organize Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- At least one medical image has been successfully uploaded into the system (as per TC001_01).
- The user is logged in with a role that has image viewing privileges (Radiographer or Radiologist).
- The MongoDB image repository is connected and operational.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Patient ID | PAT-20240601-001 |
| Expected Image | chest_xray_sample.dcm |
| Expected Modality | X-Ray |

**Test Conditions**

- The image was previously uploaded successfully.
- The system organizes images by patient ID and study metadata (modality, date, body part).

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging module. | The Radiology Imaging page is displayed. |
| 2 | Navigate to the image repository or patient image list. | A list of patients with associated images is displayed. |
| 3 | Search or browse for Patient ID `PAT-20240601-001`. | The patient record is found and their image list is displayed. |
| 4 | Verify that the image `chest_xray_sample.dcm` appears in the list. | The image is listed with correct metadata: modality (X-Ray), body part (Chest), study date (2026-06-01), and description. |
| 5 | Confirm that the image is grouped under the correct patient record and study. | The image is correctly organized under Patient ID PAT-20240601-001 and associated with the correct study. |
| 6 | Verify the stored file can be retrieved without data corruption. | The image metadata and file reference are intact and correctly stored in the MongoDB repository. |

---

#### TC002_02: Storing / Organizing Images is Unsuccessful

| Field | Details |
|---|---|
| **Test Case ID** | TC002_02 |
| **Test Case Description** | Verify that the system handles storage failure gracefully, such as when a database write error or duplicate record conflict occurs during the image organization process. |
| **Use Case Reference** | UCR002 — Store / Organize Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- The system is in a state where storage failure can be simulated (e.g., database connection is temporarily disrupted, or a duplicate image ID conflict exists).
- The user is logged in as a Radiographer.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiographer01 (Role: Radiographer) |
| Simulated Condition | MongoDB write operation fails / duplicate image ID conflict |
| Image File | chest_xray_duplicate.dcm |
| Patient ID | PAT-20240601-001 |

**Test Conditions**

- The database is unavailable or returns a write error.
- Alternatively, an image with the same unique identifier already exists in the system.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiographer01 and navigate to the Upload Image function. | The upload form is displayed. |
| 2 | Simulate a database failure condition (e.g., disconnect MongoDB or trigger duplicate key scenario). | The database is in a state that will cause the storage operation to fail. |
| 3 | Fill in valid metadata and attach a valid image file. | The form is completed successfully. |
| 4 | Click the **Upload** / **Submit** button. | The system attempts to write the record to the database. |
| 5 | Observe the system response. | The system displays an appropriate error message such as: "Image could not be stored. Please try again or contact the system administrator." No partial record is saved. The system does not crash. |
| 6 | Verify the image does not appear in the image list. | No record exists in the database for the failed upload attempt. |

---

### 2.3 Test TC003 for Module 1: View Images (UCR003)

---

#### TC003_01: Successful View Images

| Field | Details |
|---|---|
| **Test Case ID** | TC003_01 |
| **Test Case Description** | Verify that an authorized user can successfully retrieve and view a stored medical image along with its associated metadata. |
| **Use Case Reference** | UCR003 — View Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- At least one medical image is stored in the system for a known patient (PAT-20240601-001).
- The user is logged in as Radiologist (role with view privilege).

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Patient ID | PAT-20240601-001 |
| Image ID | IMG-2026-0001 |

**Test Conditions**

- The image exists in the MongoDB repository and is accessible.
- The user's role (Radiologist) has permission to view images.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The page is displayed with a list of available images. |
| 2 | Locate the image record associated with Patient ID `PAT-20240601-001`. | The image entry is visible in the list. |
| 3 | Click on the image entry or the **View** button. | The system retrieves and renders the medical image. |
| 4 | Observe the image display. | The medical image is rendered clearly in the image viewer. Associated metadata (patient ID, modality, study date, body part, description) is displayed alongside or below the image. |
| 5 | Verify that navigation controls (zoom, pan, next/previous) function as expected where applicable. | Image viewing controls respond correctly. |

---

#### TC003_02: Unauthorized Users Want to View Images

| Field | Details |
|---|---|
| **Test Case ID** | TC003_02 |
| **Test Case Description** | Verify that a user without the required role or permission is denied access when attempting to view medical images. |
| **Use Case Reference** | UCR003 — View Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Security — Negative |
| **Priority** | Critical |

**Prerequisites**

- At least one medical image is stored in the system.
- The user is logged in with a role that does NOT have image viewing permissions (e.g., Pharmacist or ClinicAssistant).

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | pharmacist01 (Role: Pharmacist) |
| Target Resource | /radiology/images/IMG-2026-0001 |

**Test Conditions**

- The requesting user's role is not in the authorized list for image viewing.
- Spring Security RBAC configuration denies access to the resource.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as pharmacist01 (Pharmacist role). | The Pharmacist Dashboard is displayed. |
| 2 | Attempt to navigate directly to the Radiology Imaging module via URL or menu. | The navigation item for Radiology Imaging is either not displayed, or access is blocked. |
| 3 | Attempt to access the image URL directly (e.g., `/radiology/images/IMG-2026-0001`). | The system intercepts the request. |
| 4 | Observe the system response. | The system returns an HTTP 403 Forbidden response or redirects to an "Access Denied" page with message: "You do not have permission to access this resource." The image is not rendered or accessible. |

---

### 2.4 Test TC004 for Module 1: Download / Share Images (UCR004)

---

#### TC004_01: Authorized User Downloads a Stored Image Successfully

| Field | Details |
|---|---|
| **Test Case ID** | TC004_01 |
| **Test Case Description** | Verify that an authorized user can successfully download a stored medical image to their local machine. |
| **Use Case Reference** | UCR004 — Download / Share Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- A medical image (IMG-2026-0001) is stored and accessible in the system.
- The user is logged in as Radiologist with download privileges.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Image ID | IMG-2026-0001 |
| Image Format | DICOM (.dcm) |

**Test Conditions**

- The image exists in the repository.
- The user's role is authorized to download images.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The image list is displayed. |
| 2 | Locate the image `IMG-2026-0001` in the list. | The image entry is visible. |
| 3 | Click the **Download** button/icon associated with the image. | The system initiates a file download. |
| 4 | Observe the browser download behaviour. | The browser presents a file download prompt. The file is downloaded to the local machine with the correct filename and format (e.g., `chest_xray_sample.dcm`). |
| 5 | Open the downloaded file using a compatible viewer. | The downloaded file opens correctly and matches the original uploaded image. |

---

#### TC004_02: Unauthorized User Attempts to Download a Stored Image

| Field | Details |
|---|---|
| **Test Case ID** | TC004_02 |
| **Test Case Description** | Verify that a user without download authorization is prevented from downloading a stored medical image. |
| **Use Case Reference** | UCR004 — Download / Share Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Security — Negative |
| **Priority** | Critical |

**Prerequisites**

- A medical image is stored in the system.
- The user is logged in with a role that does not have download permissions (e.g., Patient or ClinicAssistant).

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | patient01 (Role: Patient) |
| Target Image ID | IMG-2026-0001 |

**Test Conditions**

- The requesting user does not have the download privilege.
- Spring Security is configured to restrict the download endpoint by role.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as patient01 (Patient role). | The Patient Dashboard is displayed. |
| 2 | Attempt to access the image download endpoint directly via URL (e.g., `/radiology/images/IMG-2026-0001/download`). | The system intercepts the HTTP request. |
| 3 | Observe the system response. | The system returns an HTTP 403 Forbidden error or redirects to an "Access Denied" page. The file is not downloaded. |

---

#### TC004_03: System Generates a Share Link for an Authorized User

| Field | Details |
|---|---|
| **Test Case ID** | TC004_03 |
| **Test Case Description** | Verify that an authorized user can generate a secure, time-limited sharing link for a stored medical image. |
| **Use Case Reference** | UCR004 — Download / Share Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Image IMG-2026-0001 is stored in the system.
- The user is logged in as Radiologist with sharing privileges.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Image ID | IMG-2026-0001 |
| Share Duration | 24 hours |
| Recipient | doctor01 |

**Test Conditions**

- The image exists in the repository.
- The image sharing functionality is enabled and the ImageShare service is operational.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The image list is displayed. |
| 2 | Locate the image `IMG-2026-0001` and click the **Share** button. | A share link dialog/modal is displayed. |
| 3 | Configure the sharing settings (expiry duration: 24 hours, recipient: doctor01). | The sharing parameters are set. |
| 4 | Click **Generate Link** or **Share**. | The system generates a unique, secure sharing token. |
| 5 | Observe the system response. | A share link is displayed (e.g., `/radiology/share?token=abc123xyz`). A success message is shown. The link is recorded in the ImageShare repository with the correct expiry timestamp. |
| 6 | Copy the generated link and open it in a new browser tab (as doctor01). | The shared image is displayed to the authorized recipient. |

---

#### TC004_04: User Does Not Have Sharing Permission / Link Expired

| Field | Details |
|---|---|
| **Test Case ID** | TC004_04 |
| **Test Case Description** | Verify that access via an expired share link or by a user without sharing permission is denied by the system. |
| **Use Case Reference** | UCR004 — Download / Share Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Security — Negative |
| **Priority** | Critical |

**Prerequisites**

- A share link has been generated previously and its expiry period has elapsed.
- Alternatively, a user without sharing permission attempts to use the share function.

**Test Data**

| Field | Value |
|---|---|
| Expired Share Token | `token=abc123xyz` (generated > 24 hours ago) |
| Logged-in User | doctor01 (Role: Doctor) |

**Test Conditions**

- The share token has passed its expiry timestamp, or the requesting user lacks the necessary permission.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Attempt to access the expired share link URL: `/radiology/share?token=abc123xyz`. | The system validates the share token. |
| 2 | Observe the system response. | The system detects the expired token and returns an error message: "This share link has expired or is no longer valid." The image is not rendered or accessible via the expired link. |
| 3 | Log in as a user without sharing permissions (e.g., pharmacist01) and attempt to access the Share function on an image. | The **Share** button is either hidden or disabled for this role. If a direct API call is attempted, the system returns HTTP 403. |

---

### 2.5 Test TC005 for Module 1: Anonymize Images (UCR005)

---

#### TC005_01: Successfully Anonymize an Image

| Field | Details |
|---|---|
| **Test Case ID** | TC005_01 |
| **Test Case Description** | Verify that an authorized user can successfully anonymize a medical image, removing or masking all patient-identifying information from the image metadata. |
| **Use Case Reference** | UCR005 — Anonymize Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Image IMG-2026-0001 is stored and accessible in the system.
- The user is logged in as Radiologist with anonymization privileges.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Image ID | IMG-2026-0001 |
| Fields to Anonymize | Patient Name, Patient ID, Date of Birth, Referring Physician |

**Test Conditions**

- The image contains identifiable patient metadata.
- The anonymization service is operational.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The image list is displayed. |
| 2 | Locate image `IMG-2026-0001` and click the **Anonymize** button. | An anonymization confirmation dialog is displayed, listing the fields to be anonymized. |
| 3 | Confirm the anonymization action. | The system processes the anonymization request. |
| 4 | Observe the system response. | A success message is displayed: "Image has been successfully anonymized." |
| 5 | Open the anonymized image and inspect its metadata. | All patient-identifying fields (Name, Patient ID, DOB, Physician) have been removed or replaced with anonymized placeholders (e.g., "ANONYMIZED"). The image pixel data remains intact and unchanged. |
| 6 | Verify that the original non-anonymized image is retained (if the system creates an anonymized copy). | The system has created a separate anonymized version of the image, preserving the original record as per audit requirements. |

---

#### TC005_02: Anonymizing an Image is Unsuccessful

| Field | Details |
|---|---|
| **Test Case ID** | TC005_02 |
| **Test Case Description** | Verify that the system handles a failed anonymization attempt gracefully, such as when the target image is not found or the anonymization service fails. |
| **Use Case Reference** | UCR005 — Anonymize Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- The user is logged in as Radiologist.
- The anonymization service is unavailable or the target image ID is invalid.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Image ID | IMG-9999-9999 (non-existent) |

**Test Conditions**

- The image ID does not correspond to any record in the database, or the anonymization backend service is down.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and attempt to anonymize a non-existent image ID `IMG-9999-9999` via direct API call or URL manipulation. | The system validates the image ID. |
| 2 | Observe the system response. | The system returns an error: "Image not found. Anonymization could not be completed." No change is made to any existing records. The application does not crash. |
| 3 | Simulate a service failure during anonymization (e.g., processing error). | The anonymization process is interrupted. |
| 4 | Observe the system response. | The system displays: "Anonymization failed due to a processing error. Please try again." The original image remains unchanged. |

---

### 2.6 Test TC006 for Module 1: Search & Filter Images (UCR006)

---

#### TC006_01: Successful Searching Images

| Field | Details |
|---|---|
| **Test Case ID** | TC006_01 |
| **Test Case Description** | Verify that a user can successfully search for a medical image using a valid patient ID or study metadata keyword. |
| **Use Case Reference** | UCR006 — Search & Filter Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Multiple medical images are stored in the system with varying patient IDs and metadata.
- The user is logged in as Radiologist or Radiographer.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Search Keyword | PAT-20240601-001 |

**Test Conditions**

- The search index is operational and contains at least one matching record.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The page with the image list and search bar is displayed. |
| 2 | Enter `PAT-20240601-001` in the search bar. | The search keyword is entered. |
| 3 | Click **Search** or press Enter. | The system executes the search query. |
| 4 | Observe the search results. | All images associated with Patient ID `PAT-20240601-001` are displayed. The results show image thumbnails or entries with metadata (modality, date, description). |

---

#### TC006_02: Search Failure (No Matching Results)

| Field | Details |
|---|---|
| **Test Case ID** | TC006_02 |
| **Test Case Description** | Verify that the system correctly handles a search query that returns no matching results. |
| **Use Case Reference** | UCR006 — Search & Filter Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- The user is logged in as Radiologist.
- The search term used does not correspond to any record in the database.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Search Keyword | `NONEXISTENT-PATIENT-99999` |

**Test Conditions**

- No image record in the system matches the search keyword.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The page is displayed. |
| 2 | Enter `NONEXISTENT-PATIENT-99999` in the search bar and click **Search**. | The search query is executed. |
| 3 | Observe the result area. | The system displays a message such as: "No images found matching your search criteria." The result list is empty. No error or crash occurs. |

---

#### TC006_03: Filter Images Successfully

| Field | Details |
|---|---|
| **Test Case ID** | TC006_03 |
| **Test Case Description** | Verify that a user can successfully filter the image list by applying one or more filter criteria (e.g., modality, date range, body part). |
| **Use Case Reference** | UCR006 — Search & Filter Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Multiple images with varied modalities and dates are stored in the system.
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Filter: Modality | X-Ray |
| Filter: Date Range | 2026-01-01 to 2026-06-30 |

**Test Conditions**

- At least one image matching the filter criteria exists in the system.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The image list is displayed. |
| 2 | Locate the filter panel and select Modality: `X-Ray`. | The filter parameter is set. |
| 3 | Set Date Range from `2026-01-01` to `2026-06-30`. | The date range is set. |
| 4 | Click **Apply Filters**. | The system applies the filter criteria and queries the repository. |
| 5 | Observe the filtered results. | Only images with modality X-Ray within the specified date range are displayed. Images outside the filter criteria are not shown. |

---

#### TC006_04: Filter Images Failure (Invalid or No Results)

| Field | Details |
|---|---|
| **Test Case ID** | TC006_04 |
| **Test Case Description** | Verify that the system handles filter operations that yield no results or are based on invalid filter values. |
| **Use Case Reference** | UCR006 — Search & Filter Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- The user is logged in as Radiologist.
- The filter values applied are either invalid (e.g., end date before start date) or produce no matching records.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Filter: Modality | PET-Scan (no records exist) |
| Filter: Date Range | 2025-01-01 to 2024-12-01 (end before start — invalid) |

**Test Conditions**

- The filter combination returns zero results or constitutes an invalid input.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The image list is displayed. |
| 2 | Apply filter: Modality = `PET-Scan` (no existing records). Click **Apply Filters**. | The system executes the filter. Result area displays: "No images found for the selected filters." |
| 3 | Apply an invalid date range (end date: `2024-12-01` before start date: `2025-01-01`). Click **Apply Filters**. | The system validates the date range. An error message is displayed: "Invalid date range. End date must be after start date." Filter is not applied. |

---

### 2.7 Test TC007 for Module 1: Delete Images (UCR007)

---

#### TC007_01: Successful Delete Image

| Field | Details |
|---|---|
| **Test Case ID** | TC007_01 |
| **Test Case Description** | Verify that an authorized user can successfully delete a medical image from the system, with the record permanently removed from the database. |
| **Use Case Reference** | UCR007 — Delete Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Image IMG-2026-0002 is stored in the system.
- The user is logged in as Radiologist or Admin with delete privileges.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Image ID to Delete | IMG-2026-0002 |

**Test Conditions**

- The image exists in the repository.
- The user's role is authorized to delete images.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Imaging page. | The image list is displayed including IMG-2026-0002. |
| 2 | Locate image `IMG-2026-0002` and click the **Delete** button. | A confirmation dialog is displayed: "Are you sure you want to delete this image? This action cannot be undone." |
| 3 | Click **Confirm** / **Yes, Delete**. | The system processes the deletion request. |
| 4 | Observe the system response. | A success message is displayed: "Image deleted successfully." The image entry `IMG-2026-0002` is removed from the image list. |
| 5 | Attempt to access the deleted image via its URL or ID. | The system returns a 404 Not Found response. The image no longer exists in the MongoDB repository. |

---

#### TC007_02: Failure to Delete Image

| Field | Details |
|---|---|
| **Test Case ID** | TC007_02 |
| **Test Case Description** | Verify that the system handles a failed image deletion gracefully — such as when a non-existent image ID is provided or the user lacks delete permissions. |
| **Use Case Reference** | UCR007 — Delete Images |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- The user is logged in.
- Scenario A: The image ID does not exist. Scenario B: The user lacks delete permission.

**Test Data**

| Scenario | Logged-in User | Image ID |
|---|---|---|
| A | radiologist01 (Role: Radiologist) | IMG-9999-0000 (non-existent) |
| B | clinicassistant01 (Role: ClinicAssistant) | IMG-2026-0001 (exists) |

**Test Conditions**

- Scenario A: Image ID does not exist in the database.
- Scenario B: User role does not have delete permission.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 (Scenario A) | Log in as radiologist01 and attempt to delete non-existent image `IMG-9999-0000` via direct API call. | The system looks up the image ID. |
| 2 (Scenario A) | Observe the response. | The system returns: "Image not found. Deletion could not be completed." No changes are made to the database. |
| 3 (Scenario B) | Log in as clinicassistant01 and attempt to access the delete function for `IMG-2026-0001`. | The system checks the user's role. |
| 4 (Scenario B) | Observe the response. | The system returns HTTP 403 Forbidden or "You do not have permission to delete images." The image remains in the repository unchanged. |

---

---

## 4. MODULE 2 — RADIOLOGY REQUEST AND SCHEDULING

> **Module Description**: This module manages the end-to-end workflow of radiology imaging requests — from submission by a doctor or clinic assistant, through approval and AI-assisted prioritization by the radiologist, to appointment scheduling and notification of relevant parties. It ensures that patient imaging requests are handled efficiently, prioritized appropriately, and communicated effectively to all stakeholders.

---

### 2.8 Test TC008 for Radiology Request and Scheduling Module: Submit Imaging Request (UCR008)

---

#### TC008_01: Successful Imaging Request

| Field | Details |
|---|---|
| **Test Case ID** | TC008_01 |
| **Test Case Description** | Verify that an authorized user (Doctor or ClinicAssistant) can successfully submit an imaging request with all required fields completed. |
| **Use Case Reference** | UCR008 — Submit Imaging Request |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- The user is logged in as Doctor or ClinicAssistant.
- A valid patient record exists in the system.
- The imaging request form is accessible.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | doctor01 (Role: Doctor) |
| Patient ID | PAT-20240601-001 |
| Requested Modality | MRI |
| Body Part | Lumbar Spine |
| Urgency Level | URGENT |
| Clinical Indication | Lower back pain with suspected disc herniation |
| Referring Physician | Dr. Ahmad Fauzi |

**Test Conditions**

- All mandatory fields are filled with valid data.
- The patient record PAT-20240601-001 exists in the system.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as doctor01 and navigate to the Radiology Request module. | The imaging request submission page is displayed. |
| 2 | Click **New Imaging Request** or equivalent button. | The imaging request form is displayed. |
| 3 | Enter Patient ID: `PAT-20240601-001`. | The patient information is populated in the form (auto-fill if supported). |
| 4 | Select Modality: `MRI` from the dropdown. | The modality field is set. |
| 5 | Enter Body Part: `Lumbar Spine`. | The body part field is populated. |
| 6 | Select Urgency Level: `URGENT`. | The urgency level is set. |
| 7 | Enter Clinical Indication: `Lower back pain with suspected disc herniation`. | The clinical indication field is populated. |
| 8 | Click **Submit Request**. | The system processes and saves the imaging request. |
| 9 | Observe the system response. | A success message is displayed: "Imaging request submitted successfully." The request appears in the pending requests list with status `PENDING`. A unique request ID is assigned. |

---

#### TC008_02: Submit Imaging Request with Incomplete Form

| Field | Details |
|---|---|
| **Test Case ID** | TC008_02 |
| **Test Case Description** | Verify that the system prevents submission of an imaging request when mandatory fields are left empty, and displays appropriate validation messages. |
| **Use Case Reference** | UCR008 — Submit Imaging Request |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | High |

**Prerequisites**

- The user is logged in as Doctor.
- The imaging request form is accessible.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | doctor01 (Role: Doctor) |
| Patient ID | (left empty) |
| Modality | CT Scan |
| Urgency Level | (left empty) |

**Test Conditions**

- Mandatory fields (Patient ID, Urgency Level) are left blank.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as doctor01 and navigate to the imaging request form. | The form is displayed. |
| 2 | Leave Patient ID blank. Enter Modality: `CT Scan`. Leave Urgency Level blank. | Some fields are populated; mandatory fields remain empty. |
| 3 | Click **Submit Request**. | The system performs client-side and server-side validation. |
| 4 | Observe the system response. | Validation error messages are displayed adjacent to the empty mandatory fields: "Patient ID is required." and "Urgency Level is required." The form is not submitted. No record is created in the database. |

---

### 2.9 Test TC009 for Radiology Request and Scheduling Module: Manage Requests (UCR009)

---

#### TC009_01: Successful Approve a Submitted Imaging Request

| Field | Details |
|---|---|
| **Test Case ID** | TC009_01 |
| **Test Case Description** | Verify that an authorized user (Radiologist) can successfully approve a submitted imaging request, updating its status accordingly. |
| **Use Case Reference** | UCR009 — Manage Requests |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- An imaging request with status `PENDING` exists in the system (REQ-2026-0001).
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Request ID | REQ-2026-0001 |
| Current Status | PENDING |
| Action | APPROVE |

**Test Conditions**

- The request exists and is in PENDING status.
- The user has approval authority.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Manage Requests section. | A list of pending imaging requests is displayed. |
| 2 | Locate request `REQ-2026-0001` (status: PENDING). | The request entry is visible in the list. |
| 3 | Click the **Approve** button for this request. | A confirmation prompt may be displayed. |
| 4 | Confirm the approval action. | The system updates the request status. |
| 5 | Observe the system response. | A success message is displayed: "Request REQ-2026-0001 has been approved." The request status is updated to `APPROVED` in the list. A notification is triggered to the requesting doctor. |

---

#### TC009_02: Successful Reject a Submitted Imaging Request

| Field | Details |
|---|---|
| **Test Case ID** | TC009_02 |
| **Test Case Description** | Verify that an authorized Radiologist can successfully reject a submitted imaging request with an optional rejection reason. |
| **Use Case Reference** | UCR009 — Manage Requests |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Imaging request REQ-2026-0002 is in `PENDING` status.
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Request ID | REQ-2026-0002 |
| Current Status | PENDING |
| Rejection Reason | Insufficient clinical indication provided. Please resubmit with additional details. |

**Test Conditions**

- The request is in PENDING status and can be rejected.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Manage Requests section. | The pending requests list is displayed. |
| 2 | Locate request `REQ-2026-0002` and click **Reject**. | A rejection dialog is displayed with an optional reason text field. |
| 3 | Enter rejection reason: `Insufficient clinical indication provided. Please resubmit with additional details.` | The reason is entered. |
| 4 | Click **Confirm Rejection**. | The system processes the rejection. |
| 5 | Observe the system response. | A success message: "Request REQ-2026-0002 has been rejected." Status is updated to `REJECTED`. The rejection reason is saved. A notification is sent to the requesting doctor informing them of the rejection and the reason. |

---

### 2.10 Test TC010 for Radiology Request and Scheduling Module: Schedule Appointments (UCR010)

---

#### TC010_01: Successfully Create Appointment

| Field | Details |
|---|---|
| **Test Case ID** | TC010_01 |
| **Test Case Description** | Verify that an authorized user can successfully schedule a radiology appointment for an approved imaging request at an available time slot. |
| **Use Case Reference** | UCR010 — Schedule Appointments |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Imaging request REQ-2026-0001 has status `APPROVED`.
- At least one available appointment slot exists in the system.
- The user is logged in as Radiographer or Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiographer01 (Role: Radiographer) |
| Request ID | REQ-2026-0001 |
| Appointment Date | 2026-06-15 |
| Appointment Time | 09:00 AM |
| Modality Room | MRI Room 1 |

**Test Conditions**

- The slot at 2026-06-15, 09:00 AM in MRI Room 1 is available.
- The request status is APPROVED.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiographer01 and navigate to the Schedule Appointments section. | The scheduling interface is displayed. |
| 2 | Select request `REQ-2026-0001` from the approved requests list. | The request details are displayed. |
| 3 | Select Appointment Date: `2026-06-15` from the date picker. | The date is selected and available slots are shown. |
| 4 | Select Time: `09:00 AM` and Room: `MRI Room 1`. | The slot is selected. |
| 5 | Click **Schedule Appointment** / **Confirm**. | The system saves the appointment. |
| 6 | Observe the system response. | A success message is displayed: "Appointment scheduled successfully for 2026-06-15 at 09:00 AM." A new appointment record is created with status `SCHEDULED`. The selected slot is marked as occupied. A notification is triggered for the patient and referring doctor. |

---

#### TC010_02: Slot is Unavailable

| Field | Details |
|---|---|
| **Test Case ID** | TC010_02 |
| **Test Case Description** | Verify that the system prevents booking an appointment at a time slot that is already occupied, and provides an appropriate message to the user. |
| **Use Case Reference** | UCR010 — Schedule Appointments |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | High |

**Prerequisites**

- The time slot 2026-06-15, 09:00 AM in MRI Room 1 is already occupied by an existing appointment.
- The user is logged in as Radiographer.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiographer01 (Role: Radiographer) |
| Request ID | REQ-2026-0003 |
| Appointment Date | 2026-06-15 |
| Appointment Time | 09:00 AM |
| Modality Room | MRI Room 1 (already booked) |

**Test Conditions**

- The slot is occupied; the system enforces slot conflict prevention.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiographer01 and navigate to Schedule Appointments. | The scheduling page is displayed. |
| 2 | Attempt to schedule `REQ-2026-0003` at 2026-06-15, 09:00 AM, MRI Room 1. | The system checks slot availability. |
| 3 | Click **Schedule Appointment**. | The system detects the slot conflict. |
| 4 | Observe the response. | An error message is displayed: "The selected time slot is not available. Please choose a different date or time." The appointment is not created. The existing appointment in that slot is unaffected. |

---

### 2.11 Test TC011 for Radiology Request and Scheduling Module: AI Prioritization (UCR011)

---

#### TC011_01: AI Prioritization Success

| Field | Details |
|---|---|
| **Test Case ID** | TC011_01 |
| **Test Case Description** | Verify that the AI prioritization service correctly assigns a priority score/ranking to pending imaging requests based on urgency level and clinical indicators. |
| **Use Case Reference** | UCR011 — AI Prioritization |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Multiple imaging requests with varied urgency levels exist in the system.
- The AI prioritization service (PrioritizationService) is operational.
- The user is logged in as Radiologist.

**Test Data**

| Request ID | Urgency Level | Clinical Indication |
|---|---|---|
| REQ-2026-0004 | URGENT | Suspected pulmonary embolism |
| REQ-2026-0005 | ROUTINE | Annual check-up imaging |
| REQ-2026-0006 | HIGH | Post-operative spinal assessment |

**Test Conditions**

- The AI prioritization service is available and responsive.
- Multiple pending requests exist for ranking.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Manage Requests section. | The pending requests list is displayed. |
| 2 | Click **Run AI Prioritization** or equivalent button (if manual trigger). | The system invokes the PrioritizationService. |
| 3 | Observe the requests list after prioritization. | The requests are reordered by AI-assigned priority. URGENT requests (e.g., REQ-2026-0004) appear at the top, followed by HIGH (REQ-2026-0006), then ROUTINE (REQ-2026-0005). Each request displays a priority score or rank. |
| 4 | Verify that priority scores are recorded in the request records. | The database records for each request contain the AI-assigned priority value. |

---

#### TC011_02: AI Prioritization Failure (Service Unavailable)

| Field | Details |
|---|---|
| **Test Case ID** | TC011_02 |
| **Test Case Description** | Verify that the system handles the unavailability of the AI prioritization service gracefully, without crashing or corrupting existing request data. |
| **Use Case Reference** | UCR011 — AI Prioritization |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- The AI prioritization service is simulated as unavailable (e.g., service throws an exception or times out).
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Simulated Condition | PrioritizationService returns a service unavailable exception |

**Test Conditions**

- The AI service is down or unresponsive during the prioritization call.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Simulate the AI service being unavailable. | The service is configured to throw an exception on invocation. |
| 2 | Log in as radiologist01 and trigger AI Prioritization. | The system attempts to invoke the PrioritizationService. |
| 3 | Observe the system response. | An error message is displayed: "AI prioritization service is currently unavailable. Requests will be displayed in submission order. Please try again later." The existing requests remain unmodified. The application does not crash. |

---

### 2.12 Test TC012 for Radiology Request and Scheduling Module: Send Notifications (UCR012)

---

#### TC012_01: Notification Sent When Request is Approved

| Field | Details |
|---|---|
| **Test Case ID** | TC012_01 |
| **Test Case Description** | Verify that the system automatically sends a notification to the requesting doctor when their imaging request is approved by a Radiologist. |
| **Use Case Reference** | UCR012 — Send Notifications |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Imaging request REQ-2026-0001 is in PENDING status, submitted by doctor01.
- The notification service (SchedulingNotificationService) is operational.
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Approving User | radiologist01 (Role: Radiologist) |
| Request ID | REQ-2026-0001 |
| Requesting Doctor | doctor01 |
| Expected Notification Channel | In-app notification |

**Test Conditions**

- The request transitions from PENDING to APPROVED.
- The notification service is active.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and approve request REQ-2026-0001. | The approval is processed (as per TC009_01). |
| 2 | Log out as radiologist01 and log in as doctor01. | The doctor01 dashboard is displayed. |
| 3 | Check the notifications panel. | A new notification is present for doctor01 stating: "Your imaging request REQ-2026-0001 has been approved." The notification includes the request details and is timestamped correctly. |
| 4 | Verify the notification record in the Notification repository. | A notification record exists with the correct request ID, recipient (doctor01), channel, and status (SENT). |

---

#### TC012_02: Notification Sent When Appointment is Scheduled

| Field | Details |
|---|---|
| **Test Case ID** | TC012_02 |
| **Test Case Description** | Verify that the system automatically sends a notification to the patient and referring doctor when a radiology appointment is scheduled. |
| **Use Case Reference** | UCR012 — Send Notifications |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- An appointment has just been created for patient PAT-20240601-001 (as per TC010_01).
- The notification service is operational.

**Test Data**

| Field | Value |
|---|---|
| Patient | PAT-20240601-001 |
| Referring Doctor | doctor01 |
| Appointment Date & Time | 2026-06-15, 09:00 AM |
| Expected Recipients | patient01, doctor01 |

**Test Conditions**

- A new appointment record is created.
- The notification service triggers upon appointment creation.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Create an appointment as per TC010_01. | The appointment is successfully scheduled. |
| 2 | Log in as patient01 and check the notifications panel. | A notification is displayed: "Your radiology appointment has been scheduled for 2026-06-15 at 09:00 AM." |
| 3 | Log in as doctor01 and check the notifications panel. | A notification is displayed: "An appointment has been scheduled for your patient PAT-20240601-001 on 2026-06-15 at 09:00 AM." |
| 4 | Verify notification records in the database. | Notification records exist for both recipients with correct content, timestamps, and SENT status. |

---

### 2.13 Test TC013 for Radiology Request and Scheduling Module: View Request Status (UCR013)

---

#### TC013_01: Successfully View Request Status

| Field | Details |
|---|---|
| **Test Case ID** | TC013_01 |
| **Test Case Description** | Verify that an authorized user can view the current status and details of a submitted imaging request. |
| **Use Case Reference** | UCR013 — View Request Status |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Imaging request REQ-2026-0001 exists with status `APPROVED`.
- The user is logged in as Doctor (the submitter of the request).

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | doctor01 (Role: Doctor) |
| Request ID | REQ-2026-0001 |
| Expected Status | APPROVED |

**Test Conditions**

- The request record exists in the database.
- The user has permission to view requests they submitted.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as doctor01 and navigate to the Imaging Request list. | A list of submitted requests is displayed. |
| 2 | Locate request `REQ-2026-0001` in the list. | The request entry is visible with a status indicator. |
| 3 | Click on the request to view its details. | The request detail page/modal is displayed. |
| 4 | Observe the request status and details. | The current status `APPROVED` is clearly displayed. Request details include: request ID, patient name, modality, urgency level, submission date, approval date, and approving radiologist. |

---

#### TC013_02: Unsuccessful Viewing — Invalid Input

| Field | Details |
|---|---|
| **Test Case ID** | TC013_02 |
| **Test Case Description** | Verify that the system handles an attempt to view a request status using an invalid or non-existent request ID. |
| **Use Case Reference** | UCR013 — View Request Status |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- The user is logged in as Doctor.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | doctor01 (Role: Doctor) |
| Invalid Request ID | REQ-9999-9999 (non-existent) |

**Test Conditions**

- The request ID provided does not correspond to any record in the database.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as doctor01 and attempt to access request details via URL: `/scheduling/requests/REQ-9999-9999`. | The system queries the database for the request. |
| 2 | Observe the system response. | The system returns a 404 Not Found response or displays: "The requested imaging request could not be found. Please verify the request ID." The application does not crash. |

---

### 2.14 Test TC014 for Radiology Request and Scheduling Module: Cancel Imaging Requests (UCR014)

---

#### TC014_01: Successfully Reject Imaging Request

| Field | Details |
|---|---|
| **Test Case ID** | TC014_01 |
| **Test Case Description** | Verify that an authorized user can successfully cancel a pending imaging request, updating its status to CANCELLED. |
| **Use Case Reference** | UCR014 — Cancel Imaging Requests |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Imaging request REQ-2026-0005 is in `PENDING` status.
- The user is logged in as the submitting Doctor or authorized Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | doctor01 (Role: Doctor) |
| Request ID | REQ-2026-0005 |
| Cancellation Reason | Patient condition has resolved; imaging no longer required. |

**Test Conditions**

- The request is in a cancellable state (PENDING or APPROVED).
- The user is the submitter or has cancellation authority.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as doctor01 and navigate to the Imaging Request list. | The request list is displayed. |
| 2 | Locate request `REQ-2026-0005` and click **Cancel**. | A cancellation dialog is displayed with an optional reason field. |
| 3 | Enter cancellation reason: `Patient condition has resolved; imaging no longer required.` | The reason is entered. |
| 4 | Click **Confirm Cancellation**. | The system processes the cancellation. |
| 5 | Observe the system response. | A success message: "Request REQ-2026-0005 has been successfully cancelled." The request status is updated to `CANCELLED`. If an appointment was linked, it is also cancelled. Relevant parties are notified. |

---

#### TC014_02: Imaging Request Cancellation Failed

| Field | Details |
|---|---|
| **Test Case ID** | TC014_02 |
| **Test Case Description** | Verify that the system prevents cancellation of an imaging request that is in a non-cancellable state (e.g., already COMPLETED or CANCELLED). |
| **Use Case Reference** | UCR014 — Cancel Imaging Requests |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- Imaging request REQ-2026-0007 is in `COMPLETED` status.
- The user is logged in as Doctor.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | doctor01 (Role: Doctor) |
| Request ID | REQ-2026-0007 |
| Current Status | COMPLETED |

**Test Conditions**

- The request is in a terminal state (COMPLETED or already CANCELLED) and cannot be cancelled.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as doctor01 and locate request `REQ-2026-0007` (status: COMPLETED). | The request is found in the list. |
| 2 | Attempt to cancel the request by clicking **Cancel** or making a direct API call. | The system checks the current request status. |
| 3 | Observe the system response. | The system displays an error: "This request cannot be cancelled as it is in COMPLETED status. Only PENDING or APPROVED requests can be cancelled." The request status remains COMPLETED. |

---

---

## 5. MODULE 3 — RADIOLOGY REPORT MANAGEMENT

> **Module Description**: This module governs the full lifecycle of radiology diagnostic reports — from appointment cancellation, report generation and upload by radiologists, through status tracking and automated alerting, to the final download and sharing of reports with authorized healthcare personnel. The module ensures secure, traceable, and efficient report management within the clinical workflow.

---

### 2.15 Test TC015 for Radiology Report Management Module: Cancel Appointment (UCR015)

---

#### TC015_01: Cancel Appointment Successfully

| Field | Details |
|---|---|
| **Test Case ID** | TC015_01 |
| **Test Case Description** | Verify that an authorized user can successfully cancel a scheduled radiology appointment, freeing the slot and notifying affected parties. |
| **Use Case Reference** | UCR015 — Cancel Appointment |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Appointment APT-2026-0001 is in `SCHEDULED` status.
- The user is logged in as Radiographer or Doctor with cancellation authority.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiographer01 (Role: Radiographer) |
| Appointment ID | APT-2026-0001 |
| Current Status | SCHEDULED |
| Cancellation Reason | Equipment maintenance required on the scheduled date. |

**Test Conditions**

- The appointment is in a cancellable state (SCHEDULED).
- The user is authorized to cancel appointments.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiographer01 and navigate to the Appointments section. | The list of scheduled appointments is displayed. |
| 2 | Locate appointment `APT-2026-0001` and click **Cancel Appointment**. | A cancellation confirmation dialog is displayed. |
| 3 | Enter cancellation reason: `Equipment maintenance required on the scheduled date.` | The reason is entered. |
| 4 | Click **Confirm**. | The system processes the cancellation. |
| 5 | Observe the system response. | Success message: "Appointment APT-2026-0001 has been cancelled." Status updated to `CANCELLED`. The associated time slot is freed. Notifications are sent to the patient and referring doctor. |

---

#### TC015_02: Cancel Appointment Failure (Invalid State)

| Field | Details |
|---|---|
| **Test Case ID** | TC015_02 |
| **Test Case Description** | Verify that the system rejects a cancellation attempt for an appointment that is already in a completed or cancelled state. |
| **Use Case Reference** | UCR015 — Cancel Appointment |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- Appointment APT-2026-0002 is already in `COMPLETED` status.
- The user is logged in as Radiographer.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiographer01 (Role: Radiographer) |
| Appointment ID | APT-2026-0002 |
| Current Status | COMPLETED |

**Test Conditions**

- The appointment is in a terminal/non-cancellable state.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiographer01 and locate appointment `APT-2026-0002` (status: COMPLETED). | The appointment entry is visible. |
| 2 | Attempt to cancel the appointment. | The system checks the appointment status. |
| 3 | Observe the system response. | An error message is displayed: "This appointment cannot be cancelled. It has already been completed." The appointment status remains COMPLETED unchanged. |

---

### 2.16 Test TC016 for Radiology Report Management Module: Upload Report (UCR016)

---

#### TC016_01: Successfully Upload Report

| Field | Details |
|---|---|
| **Test Case ID** | TC016_01 |
| **Test Case Description** | Verify that an authorized Radiologist can successfully upload a completed diagnostic report for a patient's imaging study. |
| **Use Case Reference** | UCR016 — Upload Report |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Appointment APT-2026-0001 is in `COMPLETED` status.
- A diagnostic report file (PDF, ≤ 5 MB) is available on the local machine.
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Appointment ID | APT-2026-0001 |
| Patient ID | PAT-20240601-001 |
| Report File | diagnostic_report_PAT001.pdf (PDF, 1.8 MB) |
| Report Description | MRI Lumbar Spine — findings indicate L4-L5 disc protrusion |
| Report Status | DRAFT |

**Test Conditions**

- The appointment exists and is in COMPLETED status.
- The file is in a supported format (PDF).
- All mandatory report fields are filled.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Radiology Report section. | The report management page is displayed. |
| 2 | Click **Upload New Report** or navigate to the report upload form for APT-2026-0001. | The report upload form is displayed. |
| 3 | Enter Patient ID: `PAT-20240601-001`. | The patient information is populated. |
| 4 | Enter Report Description: `MRI Lumbar Spine — findings indicate L4-L5 disc protrusion`. | The description field is populated. |
| 5 | Select Report Status: `DRAFT`. | The status is set. |
| 6 | Click **Choose File** and select `diagnostic_report_PAT001.pdf`. | The file is attached. |
| 7 | Click **Upload Report**. | The system processes the upload. |
| 8 | Observe the system response. | Success message: "Report uploaded successfully." A report record is created in the database with a unique report ID. The report appears in the report list with status `DRAFT`. |

---

#### TC016_02: Upload Report Failure

| Field | Details |
|---|---|
| **Test Case ID** | TC016_02 |
| **Test Case Description** | Verify that the system rejects a report upload when mandatory fields are missing or the file format is unsupported. |
| **Use Case Reference** | UCR016 — Upload Report |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | High |

**Prerequisites**

- The user is logged in as Radiologist.
- The report upload form is accessible.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Patient ID | (left empty) |
| Report File | presentation.pptx (unsupported format) |

**Test Conditions**

- Mandatory fields are not filled, or an unsupported file format is attached.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Navigate to the report upload form. | The form is displayed. |
| 2 | Leave Patient ID blank and attach `presentation.pptx`. | The form is partially filled with an unsupported file. |
| 3 | Click **Upload Report**. | The system performs validation. |
| 4 | Observe the system response. | Validation errors are displayed: "Patient ID is required." and "Unsupported file format. Please upload a PDF file." No report record is created. |

---

### 2.17 Test TC017 for Radiology Report Management Module: View Diagnostic Report (UCR017)

---

#### TC017_01: View Report Successfully

| Field | Details |
|---|---|
| **Test Case ID** | TC017_01 |
| **Test Case Description** | Verify that an authorized user can successfully retrieve and view a diagnostic report and its associated details. |
| **Use Case Reference** | UCR017 — View Diagnostic Report |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Report RPT-2026-0001 exists in the system with status `FINALIZED`.
- The user is logged in as Doctor (referring physician for the case).

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | doctor01 (Role: Doctor) |
| Report ID | RPT-2026-0001 |
| Expected Status | FINALIZED |

**Test Conditions**

- The report exists and is in a viewable state.
- The user's role is authorized to view reports.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as doctor01 and navigate to the Radiology Report section. | A list of reports associated with the doctor's patients is displayed. |
| 2 | Locate report `RPT-2026-0001` and click **View**. | The system retrieves the report. |
| 3 | Observe the report display. | The diagnostic report is rendered (PDF viewer or embedded display). Report metadata is displayed: patient name, study date, modality, radiologist name, report status (FINALIZED), and findings summary. |
| 4 | Verify all report sections are accessible. | All sections of the report are visible and readable without formatting errors. |

---

#### TC017_02: Unauthorized Access View

| Field | Details |
|---|---|
| **Test Case ID** | TC017_02 |
| **Test Case Description** | Verify that a user without the required role or report-access permission is denied access to a diagnostic report. |
| **Use Case Reference** | UCR017 — View Diagnostic Report |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Security — Negative |
| **Priority** | Critical |

**Prerequisites**

- Report RPT-2026-0001 exists in the system.
- The user is logged in with a role that does NOT have report viewing permissions (e.g., Pharmacist).

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | pharmacist01 (Role: Pharmacist) |
| Target Report ID | RPT-2026-0001 |
| Target URL | `/reports/RPT-2026-0001` |

**Test Conditions**

- The Pharmacist role is not in the authorized list for diagnostic report access.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as pharmacist01 and attempt to navigate to `/reports/RPT-2026-0001`. | The system checks the user's role against the security configuration. |
| 2 | Observe the system response. | The system returns HTTP 403 Forbidden or redirects to an "Access Denied" page. The report content is not rendered. A message is displayed: "You are not authorized to view this report." |

---

### 2.18 Test TC018 for Radiology Report Management Module: Track Report Status (UCR018)

---

#### TC018_01: Track Report Status Successfully

| Field | Details |
|---|---|
| **Test Case ID** | TC018_01 |
| **Test Case Description** | Verify that an authorized user can view and track the progression of a report's status through its lifecycle (DRAFT → UNDER_REVIEW → FINALIZED). |
| **Use Case Reference** | UCR018 — Track Report Status |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Report RPT-2026-0001 exists with current status `UNDER_REVIEW`.
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Report ID | RPT-2026-0001 |
| Current Status | UNDER_REVIEW |
| Target Status | FINALIZED |

**Test Conditions**

- The report exists and is in a valid state for the status transition.
- The user has permission to update report status.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the Report Management section. | The report list is displayed. |
| 2 | Locate report `RPT-2026-0001` (status: UNDER_REVIEW) and click **View / Manage**. | The report details page is displayed with the current status. |
| 3 | Click **Update Status** and select `FINALIZED`. | The status update interface is displayed. |
| 4 | Click **Confirm Update**. | The system updates the report status. |
| 5 | Observe the system response. | Success message: "Report status updated to FINALIZED." The report detail page and list now reflect status `FINALIZED`. A status change log entry is created with timestamp and updating user. |
| 6 | Verify the status history/audit trail (if available). | The audit trail shows the full history: DRAFT → UNDER_REVIEW → FINALIZED with corresponding timestamps. |

---

#### TC018_02: Track Report Status Failure (Invalid Transition)

| Field | Details |
|---|---|
| **Test Case ID** | TC018_02 |
| **Test Case Description** | Verify that the system prevents an invalid status transition (e.g., attempting to move a FINALIZED report back to DRAFT). |
| **Use Case Reference** | UCR018 — Track Report Status |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- Report RPT-2026-0001 is in `FINALIZED` status.
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Report ID | RPT-2026-0001 |
| Current Status | FINALIZED |
| Attempted Transition | DRAFT (invalid — backward transition) |

**Test Conditions**

- The system enforces a one-way status lifecycle.
- A FINALIZED report cannot be moved back to an earlier state.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and locate report `RPT-2026-0001` (status: FINALIZED). | The report is found. |
| 2 | Attempt to update the status to `DRAFT` (via UI or direct API call). | The system validates the status transition. |
| 3 | Observe the system response. | An error message is displayed: "Invalid status transition. A FINALIZED report cannot be reverted to DRAFT." The report status remains FINALIZED. No change is recorded in the audit trail. |

---

### 2.19 Test TC019 for Radiology Report Management Module: Automate Alerts (UCR019)

---

#### TC019_01: Alert Triggered Successfully

| Field | Details |
|---|---|
| **Test Case ID** | TC019_01 |
| **Test Case Description** | Verify that the system automatically triggers an alert/notification to the referring doctor when a report status reaches a defined threshold (e.g., report is FINALIZED). |
| **Use Case Reference** | UCR019 — Automate Alerts |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Report RPT-2026-0001 is being updated to `FINALIZED` status.
- The notification service is operational.
- doctor01 is the referring physician for this report.

**Test Data**

| Field | Value |
|---|---|
| Report ID | RPT-2026-0001 |
| Triggering Event | Status updated to FINALIZED |
| Alert Recipient | doctor01 |
| Expected Alert Channel | In-app notification |

**Test Conditions**

- The report status transition to FINALIZED triggers the automated alert mechanism.
- The notification service is available and responsive.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | As radiologist01, update report RPT-2026-0001 status to `FINALIZED`. | The status update is processed (as per TC018_01). |
| 2 | Log in as doctor01 and check the notification panel. | A notification is present: "The diagnostic report for patient PAT-20240601-001 (Report ID: RPT-2026-0001) has been finalized and is ready for review." |
| 3 | Verify the alert record in the Notification repository. | A notification/alert record exists with: report ID, recipient (doctor01), trigger event (FINALIZED), timestamp, and status (DELIVERED). |
| 4 | Verify the alert content includes a direct link to the report. | The notification contains a navigable link to view report RPT-2026-0001. |

---

#### TC019_02: Alert Failure (Notification Service Down)

| Field | Details |
|---|---|
| **Test Case ID** | TC019_02 |
| **Test Case Description** | Verify that the system handles a notification service failure gracefully during automated alert dispatch, without crashing or affecting the primary report update operation. |
| **Use Case Reference** | UCR019 — Automate Alerts |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Negative |
| **Priority** | Medium |

**Prerequisites**

- The notification service is simulated as unavailable.
- The user is logged in as Radiologist.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Report ID | RPT-2026-0002 |
| Simulated Condition | SchedulingNotificationService is down / throws exception |

**Test Conditions**

- The notification service fails when the report status is updated to FINALIZED.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Simulate the notification service being unavailable. | The notification service is configured to fail. |
| 2 | Log in as radiologist01 and update report `RPT-2026-0002` status to `FINALIZED`. | The system processes the status update. |
| 3 | Observe the primary update result. | The report status is successfully updated to `FINALIZED` — the primary operation is not affected by the notification failure. |
| 4 | Observe the alert dispatch result. | The system logs the notification failure internally. A system warning is recorded: "Alert notification for RPT-2026-0002 could not be delivered. Notification service unavailable." No unhandled exception is thrown. The application remains stable. |

---

### 2.20 Test TC020 for Radiology Report Management Module: Download / Share Diagnostic Report (UCR020)

---

#### TC020_01: Download Report Successfully

| Field | Details |
|---|---|
| **Test Case ID** | TC020_01 |
| **Test Case Description** | Verify that an authorized user can successfully download a finalized diagnostic report to their local machine. |
| **Use Case Reference** | UCR020 — Download / Share Diagnostic Report |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Report RPT-2026-0001 is in `FINALIZED` status and accessible.
- The user is logged in as Doctor (authorized report recipient).

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | doctor01 (Role: Doctor) |
| Report ID | RPT-2026-0001 |
| Expected File Format | PDF |

**Test Conditions**

- The report is finalized and downloadable.
- The user's role permits report download.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as doctor01 and navigate to the Radiology Report section. | The report list is displayed. |
| 2 | Locate report `RPT-2026-0001` and click **Download**. | The system initiates the file download process. |
| 3 | Observe the browser download behaviour. | The browser presents a file download prompt. The file `diagnostic_report_PAT001.pdf` is downloaded successfully to the local machine. |
| 4 | Open and verify the downloaded file. | The PDF file opens correctly and contains the complete diagnostic report content without corruption. |

---

#### TC020_02: Download Report Failure (Unauthorized User)

| Field | Details |
|---|---|
| **Test Case ID** | TC020_02 |
| **Test Case Description** | Verify that a user without report download authorization is denied access when attempting to download a diagnostic report. |
| **Use Case Reference** | UCR020 — Download / Share Diagnostic Report |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Security — Negative |
| **Priority** | Critical |

**Prerequisites**

- Report RPT-2026-0001 is stored in the system.
- The user is logged in with a role that does not have report download permission (e.g., Patient).

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | patient01 (Role: Patient) |
| Target Report ID | RPT-2026-0001 |
| Target URL | `/reports/RPT-2026-0001/download` |

**Test Conditions**

- The Patient role does not have permission to download diagnostic reports.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as patient01 and attempt to access the report download endpoint directly via URL. | The system checks the user's authorization. |
| 2 | Observe the system response. | The system returns HTTP 403 Forbidden or redirects to "Access Denied." The file is not downloaded. An error message is displayed: "You are not authorized to download this report." |

---

#### TC020_03: Share Report Successfully

| Field | Details |
|---|---|
| **Test Case ID** | TC020_03 |
| **Test Case Description** | Verify that an authorized user can generate and share a secure, time-limited link for a finalized diagnostic report with another authorized recipient. |
| **Use Case Reference** | UCR020 — Download / Share Diagnostic Report |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Functional — Positive |
| **Priority** | High |

**Prerequisites**

- Report RPT-2026-0001 is in `FINALIZED` status.
- The user is logged in as Radiologist with sharing privileges.

**Test Data**

| Field | Value |
|---|---|
| Logged-in User | radiologist01 (Role: Radiologist) |
| Report ID | RPT-2026-0001 |
| Share Recipient | doctor02 |
| Share Expiry | 48 hours |

**Test Conditions**

- The report is finalized and shareable.
- The sharing service is operational.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Log in as radiologist01 and navigate to the report RPT-2026-0001. | The report detail page is displayed. |
| 2 | Click the **Share** button. | A share dialog is displayed with fields for recipient and expiry duration. |
| 3 | Enter recipient: `doctor02`, set expiry: `48 hours`. | Sharing parameters are configured. |
| 4 | Click **Generate & Share**. | The system generates a secure sharing token. |
| 5 | Observe the system response. | A share link is generated and displayed (e.g., `/reports/share?token=def456uvw`). Success message: "Report shared successfully with doctor02. Link expires in 48 hours." The share record is saved to the database with the expiry timestamp. |
| 6 | Access the share link as doctor02. | The report is accessible via the share link without requiring a full login to the report management module (subject to implementation). |

---

#### TC020_04: Share Report Failure (Expired Link)

| Field | Details |
|---|---|
| **Test Case ID** | TC020_04 |
| **Test Case Description** | Verify that access to a diagnostic report via an expired share link is denied, and an appropriate message is displayed. |
| **Use Case Reference** | UCR020 — Download / Share Diagnostic Report |
| **Created by** | Nur Aisha Binti Rohaizat |
| **Version** | 1.0 |
| **Test Type** | Security — Negative |
| **Priority** | Critical |

**Prerequisites**

- A share link for report RPT-2026-0001 was generated previously and has expired (more than 48 hours ago).

**Test Data**

| Field | Value |
|---|---|
| Expired Share Token | `token=def456uvw` (generated > 48 hours ago) |
| Target Report | RPT-2026-0001 |

**Test Conditions**

- The share token's expiry timestamp has passed.
- The system validates token expiry on every access attempt.

**Step-by-Step Execution**

| Step # | Step Details | Expected Result |
|---|---|---|
| 1 | Attempt to access the expired share link: `/reports/share?token=def456uvw`. | The system validates the share token against the current timestamp. |
| 2 | Observe the system response. | The system detects the expired token and displays an error: "This share link has expired. Please request a new share link from the report owner." The report content is not rendered or accessible. No file download is triggered. |
| 3 | Verify the share record in the database. | The expired share record still exists in the database for audit purposes, but its status is marked as EXPIRED. |

---

---

*End of Software Test Documentation*

---

**Document prepared by:** Nur Aisha Binti Rohaizat
**Institution:** Universiti Teknologi Malaysia (UTM)
**Programme:** Bachelor of Computer Science (Software Engineering)
**Document Version:** 1.0
**Date:** June 2026
