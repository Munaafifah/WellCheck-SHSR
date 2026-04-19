package com.SmartHealthRemoteSystem.SHSR.Radiology.repository;

import com.SmartHealthRemoteSystem.SHSR.Radiology.model.MedicalImage;
import com.SmartHealthRemoteSystem.SHSR.Radiology.model.Modality;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MedicalImageRepository extends MongoRepository<MedicalImage, String> {

    // UCR003/006 — Retrieve by patient
    List<MedicalImage> findByPatientId(String patientId);

    // UCR006 — Retrieve by uploader (Radiographer or Radiologist)
    List<MedicalImage> findByUploadedBy(String uploadedBy);

    // UCR006 — Retrieve by request ID
    List<MedicalImage> findByRequestId(String requestId);

    // UCR006 — Retrieve by imaging modality
    List<MedicalImage> findByModality(Modality modality);

    // UCR006 — Combined filter: patient + modality
    List<MedicalImage> findByPatientIdAndModality(String patientId, Modality modality);

    // UCR006 — Filter by capture date range
    List<MedicalImage> findByCaptureDateBetween(Date from, Date to);
}
