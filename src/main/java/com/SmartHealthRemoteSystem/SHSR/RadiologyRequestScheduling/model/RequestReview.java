package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * UCR009 audit record — every approve or reject decision made by
 * Radiologist / Radiographer is persisted here for traceability.
 * Collection: RequestReview
 */
@Document(collection = "RequestReview")
public class RequestReview {

    @Id
    private String reviewId;

    @Indexed
    private String requestId;

    @Indexed
    private String reviewerId;

    private String reviewerRole;

    /** "APPROVED" or "REJECTED" */
    private String action;

    private String notes;

    /** Required when action = "REJECTED" */
    private String rejectionReason;

    private Date reviewedAt;

    public RequestReview() {}

    public RequestReview(String reviewId, String requestId, String reviewerId,
                         String reviewerRole, String action, String notes,
                         String rejectionReason, Date reviewedAt) {
        this.reviewId        = reviewId;
        this.requestId       = requestId;
        this.reviewerId      = reviewerId;
        this.reviewerRole    = reviewerRole;
        this.action          = action;
        this.notes           = notes;
        this.rejectionReason = rejectionReason;
        this.reviewedAt      = reviewedAt;
    }

    // --- Getters ---
    public String getReviewId()        { return reviewId; }
    public String getRequestId()       { return requestId; }
    public String getReviewerId()      { return reviewerId; }
    public String getReviewerRole()    { return reviewerRole; }
    public String getAction()          { return action; }
    public String getNotes()           { return notes; }
    public String getRejectionReason() { return rejectionReason; }
    public Date   getReviewedAt()      { return reviewedAt; }

    // --- Setters ---
    public void setReviewId(String v)        { this.reviewId = v; }
    public void setRequestId(String v)       { this.requestId = v; }
    public void setReviewerId(String v)      { this.reviewerId = v; }
    public void setReviewerRole(String v)    { this.reviewerRole = v; }
    public void setAction(String v)          { this.action = v; }
    public void setNotes(String v)           { this.notes = v; }
    public void setRejectionReason(String v) { this.rejectionReason = v; }
    public void setReviewedAt(Date v)        { this.reviewedAt = v; }
}
