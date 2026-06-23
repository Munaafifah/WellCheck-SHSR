package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.repository;

import com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model.RequestReview;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for UCR009 review audit records.
 */
@Repository
public interface RequestReviewRepository extends MongoRepository<RequestReview, String> {

    List<RequestReview> findByRequestId(String requestId);

    List<RequestReview> findByReviewerId(String reviewerId);
}
