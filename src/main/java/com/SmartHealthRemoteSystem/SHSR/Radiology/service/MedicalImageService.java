package com.SmartHealthRemoteSystem.SHSR.Radiology.service;

import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.MedicalImageRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.MedicalImageResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.exception.ResourceNotFoundException;
import com.SmartHealthRemoteSystem.SHSR.Radiology.model.MedicalImage;
import com.SmartHealthRemoteSystem.SHSR.Radiology.model.Modality;
import com.SmartHealthRemoteSystem.SHSR.Radiology.repository.MedicalImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalImageService {

    private final MedicalImageRepository imageRepository;
    private final ImageAuditLogService auditLogService;
    private final String uploadDir;

    @Autowired
    public MedicalImageService(MedicalImageRepository imageRepository,
                               ImageAuditLogService auditLogService,
                               @Value("${radiology.upload.dir:uploads}") String uploadDir) {
        this.imageRepository = imageRepository;
        this.auditLogService = auditLogService;
        this.uploadDir = uploadDir;
    }

    // UCR001/002 — Upload a medical image file and persist metadata
    public MedicalImageResponseDTO uploadImage(MultipartFile file, MedicalImageRequestDTO dto) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty.");
        }
        if (dto.getPatientId() == null || dto.getPatientId().isBlank()) {
            throw new IllegalArgumentException("patientId must not be blank.");
        }
        if (dto.getUploadedBy() == null || dto.getUploadedBy().isBlank()) {
            throw new IllegalArgumentException("uploadedBy must not be blank.");
        }
        if (dto.getModality() == null) {
            throw new IllegalArgumentException("modality must not be null.");
        }

        String fileUrl = saveFileToDisk(file, dto.getModality());

        MedicalImage image = MedicalImage.builder()
                .id(UUID.randomUUID().toString())
                .requestId(dto.getRequestId())
                .patientId(dto.getPatientId())
                .uploadedBy(dto.getUploadedBy())
                .modality(dto.getModality())
                .fileUrl(fileUrl)
                .captureDate(dto.getCaptureDate() != null ? dto.getCaptureDate() : new Date())
                .anonymized(false)
                .metadata(new HashMap<>())
                .createdAt(new Date())
                .build();

        return MedicalImageResponseDTO.fromEntity(imageRepository.save(image));
    }

    // UCR003 — Retrieve a single image record by its ID
    public MedicalImageResponseDTO getImageById(String id) {
        return MedicalImageResponseDTO.fromEntity(findOrThrow(id));
    }

    // UCR003/006 — Retrieve all images belonging to a patient
    public List<MedicalImageResponseDTO> getImagesByPatientId(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            throw new IllegalArgumentException("patientId must not be blank.");
        }
        return imageRepository.findByPatientId(patientId).stream()
                .map(MedicalImageResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // UCR006 — Retrieve all images uploaded by a specific user
    public List<MedicalImageResponseDTO> getImagesByUploadedBy(String uploadedBy) {
        if (uploadedBy == null || uploadedBy.isBlank()) {
            throw new IllegalArgumentException("uploadedBy must not be blank.");
        }
        return imageRepository.findByUploadedBy(uploadedBy).stream()
                .map(MedicalImageResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // UCR006 — Search with optional filters: patientId, modality, date range, requestId
    public List<MedicalImageResponseDTO> searchImages(String patientId, Modality modality,
                                                       Date from, Date to, String requestId) {
        List<MedicalImage> results;

        // Use most specific indexed query first, then refine in memory
        if (patientId != null && !patientId.isBlank() && modality != null) {
            results = imageRepository.findByPatientIdAndModality(patientId, modality);
        } else if (patientId != null && !patientId.isBlank()) {
            results = imageRepository.findByPatientId(patientId);
        } else if (modality != null) {
            results = imageRepository.findByModality(modality);
        } else if (requestId != null && !requestId.isBlank()) {
            results = imageRepository.findByRequestId(requestId);
        } else if (from != null && to != null) {
            results = imageRepository.findByCaptureDateBetween(from, to);
        } else {
            results = imageRepository.findAll();
        }

        // Secondary in-memory filter for requestId when combined with other criteria
        if (requestId != null && !requestId.isBlank()) {
            results = results.stream()
                    .filter(img -> requestId.equals(img.getRequestId()))
                    .collect(Collectors.toList());
        }

        return results.stream()
                .map(MedicalImageResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // UCR007 — Soft delete: remove file, write audit log, delete document
    public void deleteImage(String id, String deletedBy, String reason) {
        if (deletedBy == null || deletedBy.isBlank()) {
            throw new IllegalArgumentException("deletedBy must not be blank.");
        }

        MedicalImage image = findOrThrow(id);

        deleteFileFromDisk(image.getFileUrl());
        auditLogService.logDeletion(id, deletedBy, reason);
        imageRepository.deleteById(id);
    }

    // UCR005 — Strip identifiable fields and set anonymized flag
    public MedicalImageResponseDTO anonymizeImage(String id) {
        MedicalImage image = findOrThrow(id);

        if (Boolean.TRUE.equals(image.getAnonymized())) {
            throw new IllegalStateException("Image is already anonymized.");
        }

        image.setPatientId("ANONYMIZED");
        image.setUploadedBy("ANONYMIZED");

        if (image.getMetadata() != null) {
            image.getMetadata().remove("patientName");
            image.getMetadata().remove("birthDate");
            image.getMetadata().remove("address");
            image.getMetadata().remove("contactNumber");
        }
        image.setAnonymized(true);

        return MedicalImageResponseDTO.fromEntity(imageRepository.save(image));
    }

    // UCR004 — Resolve local file path for download streaming
    public Path resolveFilePath(String id) {
        MedicalImage image = findOrThrow(id);
        return Paths.get(image.getFileUrl());
    }

    // --- Private helpers ---

    private MedicalImage findOrThrow(String id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MedicalImage not found: " + id));
    }

    private String saveFileToDisk(MultipartFile file, Modality modality) {
        try {
            Path directory = Paths.get(uploadDir, modality.name().toLowerCase());
            Files.createDirectories(directory);

            String original = file.getOriginalFilename();
            String extension = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID().toString() + extension;

            Path destination = directory.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return destination.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file: " + e.getMessage(), e);
        }
    }

    private void deleteFileFromDisk(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            Files.deleteIfExists(Paths.get(fileUrl));
        } catch (IOException e) {
            // File deletion failure is logged but must not block the audit/DB flow
        }
    }
}
