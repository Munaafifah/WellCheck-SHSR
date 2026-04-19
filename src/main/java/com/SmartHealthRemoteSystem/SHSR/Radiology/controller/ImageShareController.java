package com.SmartHealthRemoteSystem.SHSR.Radiology.controller;

import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.ImageShareRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.ImageShareResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.MedicalImageResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.service.ImageShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/images")
public class ImageShareController {

    private final ImageShareService shareService;

    @Autowired
    public ImageShareController(ImageShareService shareService) {
        this.shareService = shareService;
    }

    // UCR004 — POST /api/images/{id}/share
    // Creates a time-limited secure share token for a medical image
    @PostMapping("/{id}/share")
    public ResponseEntity<ImageShareResponseDTO> shareImage(
            @PathVariable String id,
            @Valid @RequestBody ImageShareRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(shareService.shareImage(id, request));
    }

    // UCR004 — GET /api/images/share/{token}
    // Validates token expiry and returns the image metadata
    @GetMapping("/share/{token}")
    public ResponseEntity<MedicalImageResponseDTO> getByToken(@PathVariable String token) {
        return ResponseEntity.ok(shareService.getImageByToken(token));
    }
}
