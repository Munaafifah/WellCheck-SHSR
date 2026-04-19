package com.SmartHealthRemoteSystem.SHSR.Radiology.service;

import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.ImageShareRequestDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.ImageShareResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.dto.MedicalImageResponseDTO;
import com.SmartHealthRemoteSystem.SHSR.Radiology.exception.InvalidTokenException;
import com.SmartHealthRemoteSystem.SHSR.Radiology.exception.ResourceNotFoundException;
import com.SmartHealthRemoteSystem.SHSR.Radiology.model.ImageShare;
import com.SmartHealthRemoteSystem.SHSR.Radiology.model.MedicalImage;
import com.SmartHealthRemoteSystem.SHSR.Radiology.repository.ImageShareRepository;
import com.SmartHealthRemoteSystem.SHSR.Radiology.repository.MedicalImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class ImageShareService {

    private final ImageShareRepository shareRepository;
    private final MedicalImageRepository imageRepository;
    private final SecureRandom secureRandom;

    @Autowired
    public ImageShareService(ImageShareRepository shareRepository,
                             MedicalImageRepository imageRepository) {
        this.shareRepository = shareRepository;
        this.imageRepository = imageRepository;
        this.secureRandom = new SecureRandom();
    }

    // UCR004 — Generate a time-limited secure token for a medical image
    public ImageShareResponseDTO shareImage(String imageId, ImageShareRequestDTO request) {
        // Confirm the image exists before issuing a token
        imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MedicalImage not found: " + imageId));

        if (request.getSharedBy() == null || request.getSharedBy().isBlank()) {
            throw new IllegalArgumentException("sharedBy must not be blank.");
        }

        // Cryptographically secure 32-byte token, URL-safe Base64 encoded
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        String accessToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, request.getExpirationHours());

        ImageShare share = ImageShare.builder()
                .id(UUID.randomUUID().toString())
                .imageId(imageId)
                .sharedBy(request.getSharedBy())
                .accessToken(accessToken)
                .expiresAt(calendar.getTime())
                .build();

        return ImageShareResponseDTO.fromEntity(shareRepository.save(share));
    }

    // UCR004 — Validate a share token and return the associated image metadata
    public MedicalImageResponseDTO getImageByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Access token must not be blank.");
        }

        ImageShare share = shareRepository.findByAccessToken(token)
                .orElseThrow(() -> new InvalidTokenException(
                        "Access token is invalid or does not exist."));

        if (share.getExpiresAt() == null || share.getExpiresAt().before(new Date())) {
            throw new InvalidTokenException("Access token has expired.");
        }

        MedicalImage image = imageRepository.findById(share.getImageId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MedicalImage not found for this token: " + share.getImageId()));

        return MedicalImageResponseDTO.fromEntity(image);
    }
}
