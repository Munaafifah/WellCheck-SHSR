package com.SmartHealthRemoteSystem.SHSR.Radiology.controller;

import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.MedicalImageRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.MedicalImageResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.model.Modality;
import com.SmartHealthRemoteSystem.SHSR.Radiology.service.MedicalImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class MedicalImageController {

    private final MedicalImageService imageService;

    @Autowired
    public MedicalImageController(MedicalImageService imageService) {
        this.imageService = imageService;
    }

    // UCR001/002 — POST /api/images/upload
    // Accepts multipart/form-data: file + individual DTO fields as form parameters
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MedicalImageResponseDTO> uploadImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam String patientId,
            @RequestParam(required = false) String requestId,
            @RequestParam String uploadedBy,
            @RequestParam Modality modality,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date captureDate) {

        MedicalImageRequestDTO dto = new MedicalImageRequestDTO();
        dto.setPatientId(patientId);
        dto.setRequestId(requestId);
        dto.setUploadedBy(uploadedBy);
        dto.setModality(modality);
        dto.setCaptureDate(captureDate);

        MedicalImageResponseDTO response = imageService.uploadImage(file, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // UCR003 — GET /api/images/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MedicalImageResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    // UCR003/006 — GET /api/images/patient/{patientId}
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalImageResponseDTO>> getByPatient(
            @PathVariable String patientId) {
        return ResponseEntity.ok(imageService.getImagesByPatientId(patientId));
    }

    // UCR006 — GET /api/images/uploader/{uploadedBy}
    @GetMapping("/uploader/{uploadedBy}")
    public ResponseEntity<List<MedicalImageResponseDTO>> getByUploader(
            @PathVariable String uploadedBy) {
        return ResponseEntity.ok(imageService.getImagesByUploadedBy(uploadedBy));
    }

    // UCR006 — GET /api/images/search?patientId=&modality=&from=&to=&requestId=
    // All parameters are optional; at least one is recommended to avoid full-collection scan
    @GetMapping("/search")
    public ResponseEntity<List<MedicalImageResponseDTO>> search(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) Modality modality,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date to,
            @RequestParam(required = false) String requestId) {
        return ResponseEntity.ok(
                imageService.searchImages(patientId, modality, from, to, requestId));
    }

    // UCR007 — DELETE /api/images/{id}?deletedBy=&reason=
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteImage(
            @PathVariable String id,
            @RequestParam String deletedBy,
            @RequestParam(required = false, defaultValue = "No reason provided") String reason) {
        imageService.deleteImage(id, deletedBy, reason);
        return ResponseEntity.ok(Map.of("message", "Image deleted and audit log recorded."));
    }

    // UCR005 — PUT /api/images/{id}/anonymize
    @PutMapping("/{id}/anonymize")
    public ResponseEntity<MedicalImageResponseDTO> anonymize(@PathVariable String id) {
        return ResponseEntity.ok(imageService.anonymizeImage(id));
    }

    // UCR004 — GET /api/images/{id}/download
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadImage(@PathVariable String id) {
        Path filePath = imageService.resolveFilePath(id);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
