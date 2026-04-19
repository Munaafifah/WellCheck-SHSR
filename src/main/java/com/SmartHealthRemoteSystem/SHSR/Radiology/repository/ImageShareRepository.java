package com.SmartHealthRemoteSystem.SHSR.Radiology.repository;

import com.SmartHealthRemoteSystem.SHSR.Radiology.model.ImageShare;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageShareRepository extends MongoRepository<ImageShare, String> {

    // UCR004 — Look up a share record by its secure token
    Optional<ImageShare> findByAccessToken(String accessToken);

    // UCR004 — Retrieve all share records for a given image
    List<ImageShare> findByImageId(String imageId);

    // UCR004 — Retrieve all shares issued by a specific user
    List<ImageShare> findBySharedBy(String sharedBy);
}
